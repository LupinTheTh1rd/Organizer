package com.example.homepage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.homepage.Adapter.NewTimeSlotAdapter;
import com.example.homepage.ViewDecorator.SpacesItemDecoration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

public class Viewing extends Fragment {

    RecyclerView recycler_time_slot;
    HorizontalCalendarView horizontalCalendarView;

    List<TimeSlot_Data> timeSlotList;

    Bundle bundle;
    Button pickRoom1, pickRoom2;
    SimpleDateFormat simpleDateFormat;
    AlertDialog alertDialog;
    Calendar selected_date;
    HorizontalCalendar horizontalCalendar;

    FirebaseFirestore db;
    NewTimeSlotAdapter adapter;

    String tempString = "";

    Calendar scrollDate;
    String bookedRoom = "room1";

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewing, container, false);
        view.setOnKeyListener(new View.OnKeyListener() {
              @Override
              public boolean onKey(View v, int keyCode, KeyEvent event) {
                  if (keyCode == KeyEvent.KEYCODE_BACK) {
                      return true;
                  }
                  return false;
              }
          }
        );
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("View Bookings");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View itemView = getView();
        recycler_time_slot = itemView.findViewById(R.id.recycler_time_slot);
        horizontalCalendarView = itemView.findViewById(R.id.calendar_view);

        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");   //eg: 25_03_2019 (this is key)
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        pickRoom1 = itemView.findViewById(R.id.room1);
        pickRoom2 = itemView.findViewById(R.id.room2);

        selected_date = Calendar.getInstance();
        selected_date.add(Calendar.DATE, 0);        //initialize with the current date

        scrollDate = Calendar.getInstance();
        scrollDate.add(Calendar.DATE, 0);           //to check which scrolled date the user was on, before he chose to switch to viewing other room's bookings

        //handle attributes of recycler view
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);      //display as a grid, with three in each row
        recycler_time_slot.setLayoutManager(gridLayoutManager);                                      // set this LayoutManager for the recycler view
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(6));      //add spaces around each slot in booking view

        //now, create the horizontal calendar
        final Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 0);    //current date (i.e present day)
        final Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 4);

        horizontalCalendar = new HorizontalCalendar.Builder(itemView, R.id.calendar_view)
                .range(startDate, endDate)
                .datesNumberOnScreen(3)
                .mode(HorizontalCalendar.Mode.DAYS)
                .configure()
                .textColor(
                        ContextCompat.getColor(getContext(), android.R.color.white),
                        ContextCompat.getColor(getContext(), android.R.color.white)
                )
                .selectorColor(ContextCompat.getColor(getContext(), android.R.color.transparent))
                .end()
                .defaultSelectedDate(startDate)
                .build();


        //we set an adapter for the recycler view, initially for the startDate on the horizontal calendar
        attachAdapter(getContext(), simpleDateFormat.format(startDate.getTime()), bookedRoom);

        //set button action, to choose which room's booking will be shown
        pickRoom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachAdapter(getContext(), simpleDateFormat.format(startDate.getTime()), "room1");
                bookedRoom = "room1";
                pickRoom1.setBackgroundResource(R.drawable.selected_left_button);
                pickRoom2.setBackgroundResource(R.drawable.right_round_button);
                pickRoom2.setTextColor(ContextCompat.getColor(getContext(), R.color.notPickedRoom));
                pickRoom1.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));

                horizontalCalendar.goToday(true);
//                horizontalCalendar.setRange(startDate,endDate);
                horizontalCalendar.refresh();
            }
        });

        pickRoom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachAdapter(getContext(), simpleDateFormat.format(startDate.getTime()), "room2");
                bookedRoom = "room2";
                pickRoom2.setBackgroundResource(R.drawable.selected_right_button);
                pickRoom1.setBackgroundResource(R.drawable.left_round_button);
                pickRoom2.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                pickRoom1.setTextColor(ContextCompat.getColor(getContext(), R.color.notPickedRoom));

//                horizontalCalendar.selectDate(scrollDate,false);
//                horizontalCalendar.setRange(startDate,endDate);
                horizontalCalendar.goToday(true);
                horizontalCalendar.refresh();
            }
        });

        //set the horizontal calendar listener, so that the slots update when the date is changed
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
//                if(selected_date.getTimeInMillis() != date.getTimeInMillis()) {
//                    selected_date = date;
//                    //make it stick to the one selected, not the next one (some bug in original material calendar, cloned off github)
////                    scrollDate = date;
////                    scrollDate.add(Calendar.DATE,1);
////                    horizontalCalendar.selectDate(scrollDate,true);
////                    horizontalCalendar.refresh()
//                    //set the adapter for recycler view, with the new date; as selected upon change in horizontal calendar
//                    attachAdapter(getContext(), simpleDateFormat.format(date.getTime()),bookedRoom);
//                    Toast.makeText(getContext(), simpleDateFormat.format(date.getTime()), Toast.LENGTH_SHORT).show();
//                }
//                horizontalCalendar.selectDate(date,false);
                attachAdapter(getContext(), simpleDateFormat.format(date.getTime()), bookedRoom);
                Toast.makeText(getContext(), simpleDateFormat.format(date.getTime()), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private List<TimeSlot_Data> getSlotList() {
        timeSlotList = new ArrayList<>();
        for (int i = 0; i < 18; i++)       //coz 18 slots in total, so we are sending an arraylist of timeslot_data type; which will then be segregated
            // into booked and available ones, in the adapter class
            timeSlotList.add(new TimeSlot_Data(i));
        return timeSlotList;
    }

    private boolean fetchBookedSlot(final String slotDate, final String bookedRoom) {

        db = FirebaseFirestore.getInstance();
        final CollectionReference bookedSlots = db.collection("Bookings")
                .document(bookedRoom)
                .collection(slotDate);
        bookedSlots.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            tempString += document.getLong("slot").intValue() + " ";
                        }
                        adapter = new NewTimeSlotAdapter(getContext(), getSlotList(), tempString, slotDate, bookedRoom);
                    } else {
                        adapter = new NewTimeSlotAdapter(getContext(), getSlotList(), "", slotDate, bookedRoom);
                    }
                    recycler_time_slot.setAdapter(adapter);
                    alertDialog.dismiss();
                    tempString = "";
                }
            }
        });

        return true;
    }

    public void attachAdapter(Context context, String slotDate, String bookedRoom) {
        alertDialog.show();
        fetchBookedSlot(slotDate, bookedRoom);
    }


}
