package com.example.homepage.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.homepage.R;
import com.example.homepage.TimeSlot_Data;
import com.example.homepage.global_variables;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class BookingTimeSlotAdapter extends RecyclerView.Adapter<BookingTimeSlotAdapter.MyViewHolder> {

    private Context context;
    private List<TimeSlot_Data> timeSlotList;

    //my variables

    private int i = 0;
    private String slotNumber = "";
    private FirebaseFirestore db;

    HashMap<Integer, Boolean> checkSlot;
    //NEW
    private String viewingDate;
    private String viewingRoom;

    public BookingTimeSlotAdapter(Context context, List<TimeSlot_Data> timeSlotList, String slotNumber) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.slotNumber = slotNumber;
    }

    @NonNull
    @Override
    public BookingTimeSlotAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.layout_time_slot, viewGroup, false);
//        Toast.makeText(context, slotNumber.length()+"", Toast.LENGTH_SHORT).show();
        return new BookingTimeSlotAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookingTimeSlotAdapter.MyViewHolder myViewHolder, final int position) {
        //fetch the particular slot, which is to be binded
        final TimeSlot_Data selectedSlot = timeSlotList.get(position);
        //write the time slot text, corresponding to each slot
        myViewHolder.time_slot.setText(convertSlotToString(position));


        //now, segregate which slots are booked and which are not
        fetchBookedSlots();

        if (!checkBookedSlot(position)) {           //if the slot is available/not booked
            //color the available slots differently
            myViewHolder.card_time_slot.setCardBackgroundColor(selectedSlot.isSelected()
                    ? (ContextCompat.getColor(context, R.color.selectedSlot))
                    : (ContextCompat.getColor(context, android.R.color.white)));        //depending on whether or not this slot has been selected b4, we can change the color
            //show this slot in white (i.e mark is as not booked)
            myViewHolder.time_slot_status.setText("Available");
            myViewHolder.time_slot.setTextColor(ContextCompat.getColor(context, R.color.availableText));
            myViewHolder.time_slot_status.setTextColor(ContextCompat.getColor(context, R.color.availableText));
        } else {       //if the slot is booked
            //color the booked slots differently
            myViewHolder.card_time_slot.setTag(global_variables.DISABLE_TAG);           //mark it as booked, so later on, we can make it non-selectable
            myViewHolder.card_time_slot.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            //show this slot in white (i.e mark is as not booked)
            myViewHolder.time_slot_status.setText("Booked");
            myViewHolder.time_slot.setTextColor(ContextCompat.getColor(context, R.color.bookedText));
            myViewHolder.time_slot_status.setTextColor(ContextCompat.getColor(context, R.color.bookedText));
        }

        myViewHolder.card_time_slot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myViewHolder.card_time_slot.getTag() != "DISABLE") {         //selection of the card view only allowed on available slot
                    selectedSlot.setSelected(!selectedSlot.isSelected());       //toggle selection, i.e if current slot already selected, deselect; and vice-versa
                    myViewHolder.card_time_slot.setCardBackgroundColor(selectedSlot.isSelected()
                            ? (ContextCompat.getColor(context, R.color.selectedSlot))
                            : (ContextCompat.getColor(context, android.R.color.white)));        //depending on whether or not this slot has been selected b4, we can change the color
                }
            }
        });

    }

    @Override
    public int getItemCount() {
//        return 18;
        return (timeSlotList == null)
                ? 0
                : timeSlotList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView time_slot, time_slot_status;
        CardView card_time_slot;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_time_slot = itemView.findViewById(R.id.card_time_slot);
            time_slot = itemView.findViewById(R.id.txt_time_slot);
            time_slot_status = itemView.findViewById(R.id.txt_time_slot_description);
        }
    }

    //extra method
    private String convertSlotToString(int slot) {
        switch (slot) {
            case 0:
                return "09:00-09:30";
            case 1:
                return "09:30-10:00";
            case 2:
                return "10:00-10:30";
            case 3:
                return "10:30-11:00";
            case 4:
                return "11:00-11:30";
            case 5:
                return "11:30-12:00";
            case 6:
                return "12:00-12:30";
            case 7:
                return "12:30-13:00";
            case 8:
                return "13:00-13:30";
            case 9:
                return "13:30-14:00";
            case 10:
                return "14:00-14:30";
            case 11:
                return "14:30-15:00";
            case 12:
                return "15:00-15:30";
            case 13:
                return "15:30-16:00";
            case 14:
                return "16:00-16:30";
            case 15:
                return "16:30-17:00";
            case 16:
                return "17:00-17:30";
            case 17:
                return "17:30-18:00";
            default:
                return "Closed";
        }
    }

    private boolean checkBookedSlot(int current_position) {
        return checkSlot.get(current_position);
    }

    @SuppressLint("UseSparseArrays")
    private void fetchBookedSlots() {
        checkSlot = new HashMap<>();
        for (int i = 0; i < 18; i++) {
            checkSlot.put(i, false);
        }
        if (slotNumber != null && !slotNumber.isEmpty()) {
            for (String i : slotNumber.split("\\s")) {
                checkSlot.put(Integer.parseInt(i), true);
            }
        }
    }
}

