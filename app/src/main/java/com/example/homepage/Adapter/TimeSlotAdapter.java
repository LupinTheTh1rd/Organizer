package com.example.homepage.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepage.Interface.IRecyclerItemSelectedListener;
import com.example.homepage.R;
import com.example.homepage.TimeSlot_Data;
import com.example.homepage.global_variables;

import java.util.ArrayList;
import java.util.List;



public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {

    Context context;
    List<TimeSlot_Data> timeSlotList;
    //newly added (for clickable listview, for booking (17june'19)
    List<CardView> cardViewList;
//    Button proceedBooking;


    public TimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>(); //coz if empty, just return default list
        //#
        cardViewList = new ArrayList<>();
        //#
    }

    public TimeSlotAdapter(Context context, List<TimeSlot_Data> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        //#
        cardViewList = new ArrayList<>();
        //#
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int cur_slot) {
        myViewHolder.time_slot.setText(convertSlotToString(cur_slot));     //return time slot (like 9:00 - 9:30)
        if(timeSlotList.size() == 0)           //if all slots are available, just display the list
        {
//            myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));     //deprecated
            myViewHolder.card_time_slot.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                                                //show this slot in white (i.e mark is as not booked)
            myViewHolder.time_slot_status.setText("Available");
            myViewHolder.time_slot.setTextColor(ContextCompat.getColor(context,R.color.availableText));
            myViewHolder.time_slot_status.setTextColor(ContextCompat.getColor(context,R.color.availableText));
//            myViewHolder.time_slot_status.setTextColor(context.getResources().getColor(R.color.availableText));             //deprecated
//            myViewHolder.time_slot.setTextColor(context.getResources().getColor(R.color.availableText));                    //deprecated
        }
        else                                 //if that position is booked
        {
            for(TimeSlot_Data slotValue: timeSlotList) {
                int slot = slotValue.getSlot();
                if(slot == cur_slot) {          //if the slot == current position   [for booked slots]
                    //#
                    //set tag for all the time slots, which are full
                    //so based on this tag, we can set remaining card backgrounds (without changing the full time slots)
                    myViewHolder.card_time_slot.setTag(global_variables.DISABLE_TAG);
                    //#
//                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.bookedSlot));        //deprecated
                    myViewHolder.card_time_slot.setCardBackgroundColor(ContextCompat.getColor(context, R.color.bookedSlot));
                                                //show this slot in dark gray (i.e mark it as booked)
                    myViewHolder.time_slot_status.setText("Booked");
                    myViewHolder.time_slot.setTextColor(ContextCompat.getColor(context,R.color.bookedText));
                    myViewHolder.time_slot_status.setTextColor(ContextCompat.getColor(context,R.color.bookedText));
//                    myViewHolder.time_slot_status.setTextColor(context.getResources().getColor(R.color.bookedText));                //deprecated
//                    myViewHolder.time_slot.setTextColor(context.getResources().getColor(R.color.bookedText));                       //deprecated
                }
//                else {                          //for rest                          [for available slots]
//                    myViewHolder.time_slot_status.setTextColor(context.getResources().getColor(R.color.availableText));
//                    myViewHolder.time_slot.setTextColor(context.getResources().getColor(R.color.availableText));
//                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
//                }
            }


        }

        //#
        //Add all time slot cards (i.e 18 cards, coz 9-6 working hours)
        //initially, no cards added in cardViewList
        if(!cardViewList.contains(myViewHolder.card_time_slot))
            cardViewList.add(myViewHolder.card_time_slot);

        //Check if card time slot is available
        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                //Loop all cards in card list view
                Toast.makeText(context, pos+"", Toast.LENGTH_SHORT).show();

                for(CardView cardView: cardViewList)
                {
//                    if(cardView.getTag() == null)   //only available time slots can be booked, so only their background color will be changed on selection
                    if(cardView.getTag() != "DISABLE")
//                        cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));            //deprecated
                        cardView.setCardBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
                }
                //only the background color of the currently selected time slot should change
                if(myViewHolder.card_time_slot.getTag() == null) {
//                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));    //deprecated
                    myViewHolder.card_time_slot.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
                    myViewHolder.time_slot_status.setTextColor(ContextCompat.getColor(context, R.color.availableText));
                    myViewHolder.time_slot.setTextColor(ContextCompat.getColor(context, R.color.availableText));
//                    myViewHolder.time_slot.setTextColor(context.getResources().getColor(R.color.availableText));              //deprecated
//                    myViewHolder.time_slot_status.setTextColor(context.getResources().getColor(R.color.availableText));         //deprecated
                }

                //After that, enable the proceed button
//                ((global_variables) context.getApplicationContext()).setCheckSlot(true);
                /*
//                LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View newView = layoutInflater.inflate(R.layout.calendar_slots, null);
//                Button proceed = newView.findViewById(R.id.proceed_btn);
//                proceed.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(v.getContext(), "Hello bitches!", Toast.LENGTH_SHORT).show();
//                    }
//                });
                */
            }
        });
        //#

    }

    private String convertSlotToString(int slot) {
        switch (slot) {
            case 0:
                return "9:00-9:30";
            case 1:
                return "9:30-10:00";
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

    @Override
    public int getItemCount() {
        return 18;      //working hours: 9AM to 6PM; so for 30min each; 9-6 has 18 slots
                        //check if you can make this global (at the end)
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView time_slot, time_slot_status;
        CardView card_time_slot;

        //#
        void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;
        //#

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_time_slot = itemView.findViewById(R.id.card_time_slot);
            time_slot = itemView.findViewById(R.id.txt_time_slot);
            time_slot_status = itemView.findViewById(R.id.txt_time_slot_description);

            //#
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
                }
            });
            //#
        }
    }
}
