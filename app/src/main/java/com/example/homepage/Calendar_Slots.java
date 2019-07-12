package com.example.homepage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.homepage.Adapter.TimeSlotAdapter;
import com.example.homepage.Interface.ITimeSlotLoadListener;
import com.example.homepage.ViewDecorator.SpacesItemDecoration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class Calendar_Slots extends Fragment implements ITimeSlotLoadListener {
    //My variables
    Bundle bundle, timeBundle;
    int picked_day;
    int picked_month;
    int picked_year;
    String room;
    View itemView;
    HorizontalCalendar horizontalCalendar;
    Button proceedBooking;


    //Variables
    FirebaseFirestore db;
    DocumentReference docRef;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    AlertDialog alertDialog;

    BroadcastReceiver displayTimeSlot;
    //UnBinder unbinder;
    LocalBroadcastManager localBroadcastManager;
    Calendar selected_date;
//    @BindView(R.id.recycler_time_slot)
    RecyclerView recycler_time_slot;
    //@BindView(R.id.calender_view)
    HorizontalCalendarView  calendarView;
    SimpleDateFormat simpleDateFormat;

    //inner class problem = string
    String booking_date;
    Calendar cal;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //View itemView = getView();
        itemView = getView();
        //wrote myself (NOT OFFICIAL)
        recycler_time_slot = itemView.findViewById(R.id.recycler_time_slot);
        calendarView = itemView.findViewById(R.id.calendar_view);
        bundle = this.getArguments();
        picked_day = bundle.getInt("picked_day");
        picked_month = bundle.getInt("picked_month");
        picked_year = bundle.getInt("picked_year");
        room = bundle.getString("room");

        proceedBooking = itemView.findViewById(R.id.proceed_btn);


        //from OnCreate (shouldn't be here)
        iTimeSlotLoadListener = this;
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(displayTimeSlot, new IntentFilter());
        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");   //eg: 25_03_2019 (this is key)
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        selected_date = Calendar.getInstance();
        selected_date.add(Calendar.DATE, 0);        //initialize with the current date

        displayTimeSlot = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Calendar date = Calendar.getInstance();
                date.add(Calendar.DATE, 0);
                loadAvailableTimeSlot(simpleDateFormat.format(date.getTime()));
            }
        };

        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));      //add spaces around each slot in booking view

        //Calendar
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 0);    //current date (i.e present day)
        final Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);      //I THINK allows booking of max 2days from current day
        //FOR OUR APP; we'll select the user-picked date as the defaultSelectedDate in Horizontal Calendar
        final Calendar pickedDate = Calendar.getInstance();
        pickedDate.set(picked_year, picked_month, picked_day);

        //used to be: "FINAL HORIZONTALcALENDAR hORIZONTALcALENDAR = {..}
        horizontalCalendar = new HorizontalCalendar.Builder(itemView, R.id.calendar_view)
                .range(pickedDate, endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
//                .defaultSelectedDate(startDate)
                .defaultSelectedDate(pickedDate)
                .build();

        ///#####################ADDED MYSELF
//        cal = Calendar.getInstance();
//        horizontalCalendar.selectDate(pickedDate,false);
        loadAvailableTimeSlot(simpleDateFormat.format(pickedDate.getTime()));
            //test
//            Calendar cal1 = Calendar.getInstance();
//            cal1.add(Calendar.DAY_OF_MONTH,day);
//        Toast.makeText(getContext(), cal1.getTime()+"", Toast.LENGTH_SHORT).show();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if(selected_date.getTimeInMillis() != date.getTimeInMillis()) {
                    selected_date = date;       //this is done so that code won't reload when you choose the date same as the current date
                    //in simple terms, code needs to load when you change the date which is currently selected
                    //not when you reselect the date
                    loadAvailableTimeSlot(simpleDateFormat.format(date.getTime()));
                }
            }

//            @Override
//            public boolean onDateLongClicked(Calendar date, int position) {
//
//                DatePickerDialog dpd = new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        Calendar calendar = Calendar.getInstance();
////                        horizontalCalendar.selectDate(moveToDate, false);
//                        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
//                            @Override
//                            public void onDateSelected(Calendar date, int position) {
//                            }
//                        });
//                        calendar.set(year,month,dayOfMonth);
//                        loadAvailableTimeSlot(simpleDateFormat.format(calendar.getTime()));
//
//                    }
//                },date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
//                dpd.show();
//                return super.onDateLongClicked(date, position);
//            }
        });

//        proceedBooking.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TimeSlotAdapter selectedItem = new TimeSlotAdapter().get
//                if( ((global_variables) getContext().getApplicationContext()).isCheckSlot() ) {
//                    ((global_variables) getContext().getApplicationContext()).setCheckSlot(false);
//                    Fragment newFragment = new Details();
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.content_main, newFragment);
//                    ft.addToBackStack(null);
//                    ft.commit();
//                } else {
////                    Toast.makeText(getContext(), "Please select a time slot to book!", Toast.LENGTH_SHORT).show();
//                    Snackbar snackbar = Snackbar
//                            .make(getView(),"Please select an available time slot!",Snackbar.LENGTH_SHORT)
//                            .setAction("OK", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                }
//                            });
//                    snackbar.show();
//                    ((global_variables) getContext().getApplicationContext()).setCheckSlot(false);
//                }
//            }
//        });

    }

    private void loadAvailableTimeSlot(String date) {
        alertDialog.show();
        booking_date = date;

        db = FirebaseFirestore.getInstance();
//        DocumentReference bookedDoc = db.collection("booked").document("user10");   //dummy booking for today
        DocumentReference bookedDoc = db.collection("Bookings").document("room1");
        bookedDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {
//                        CollectionReference bookedDate = db.collection("booked").document("user10").collection(booking_date);
                        CollectionReference bookedDate = db.collection("Bookings").document("room1").collection(booking_date);
                        Toast.makeText(getContext(), booking_date+"", Toast.LENGTH_SHORT).show();
                        bookedDate.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if(querySnapshot.isEmpty()) {
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();        // if no appointment, load as empty
                                    } else {
                                        //if the slot is booked
                                        List<TimeSlot_Data> timeSlots = new ArrayList<>();
                                        for(QueryDocumentSnapshot document: task.getResult())
                                            timeSlots.add(document.toObject(TimeSlot_Data.class));
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);     //successfully loaded booked/non-booked slots
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());         //couldn't load the slots
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot_Data> timeSlotList) {
        TimeSlotAdapter adapter = new TimeSlotAdapter(getContext(), timeSlotList);
        recycler_time_slot.setAdapter(adapter);                             //if bookings fetched successfully,
        alertDialog.dismiss();
        proceedBooking.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();   //if failed to fetch bookings, display error message as a toast
        alertDialog.dismiss();
        proceedBooking.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        TimeSlotAdapter adapter = new TimeSlotAdapter(getContext());
        recycler_time_slot.setAdapter(adapter);                             //if no bookings, just returns default list of slots
        alertDialog.dismiss();
        proceedBooking.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.calendar_slots, container, false);

//        recycler_time_slot.setHasFixedSize(true);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
//        recycler_time_slot.setLayoutManager(gridLayoutManager);
//        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));      //add spaces around each slot in booking view
//
//        //Calendar
//        Calendar startDate = Calendar.getInstance();
//        startDate.add(Calendar.DATE, 0);    //current date (i.e present day)
//        Calendar endDate = Calendar.getInstance();
//        endDate.add(Calendar.DATE, 2);      //I THINK allows booking of max 2days from current day
//        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(itemView, R.id.calendar_view)
//                .range(startDate, endDate)
//                .datesNumberOnScreen(1)
//                .mode(HorizontalCalendar.Mode.DAYS)
//                .defaultSelectedDate(startDate)
//                .build();
//
//        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
//            @Override
//            public void onDateSelected(Calendar date, int position) {
//                if(selected_date.getTimeInMillis() != date.getTimeInMillis()) {
//                    selected_date = date;       //this is done so that code won't reload when you choose the date same as the current date
//                                                //in simple terms, code needs to load when you change the date which is currently selected
//                                                //not when you reselect the date
//                    loadAvailableTimeSlot(simpleDateFormat.format(date.getTime()));
//                }
//            }
//        });
        return itemView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("View Available Slots");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        iTimeSlotLoadListener = this;
//        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
//        localBroadcastManager.registerReceiver(displayTimeSlot, new IntentFilter());
//        simpleDateFormat = new SimpleDateFormat("dd_mm_yyyy");   //eg: 25_03_2019 (this is key)
//        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
//
//        selected_date = Calendar.getInstance();
//        selected_date.add(Calendar.DATE, 0);        //initialize with the current date
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        localBroadcastManager.unregisterReceiver(displayTimeSlot);
    }
}
