package com.example.homepage;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.homepage.Adapter.BookingTimeSlotAdapter;
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

public class NewCalendar_Slots extends Fragment{

    RecyclerView recycler_time_slot;
    HorizontalCalendarView horizontalCalendarView;
    Button proceed_btn;

    List<TimeSlot_Data> timeSlotList;

    Bundle bundle;
    int picked_day,picked_month,picked_year;
    String room;
    SimpleDateFormat simpleDateFormat;
    AlertDialog alertDialog;
    Calendar selected_date;
    HorizontalCalendar horizontalCalendar;

    FirebaseFirestore db;
//    NewTimeSlotAdapter adapter;
    //NEW
    BookingTimeSlotAdapter adapter;

    String tempString="";

    Date bookingDate;
    Calendar pickedDate;
    String bookedRoom;

    //NEW
    Bundle loggedIn;
    String loggedInCompany;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.calendar_slots,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("View Booking Slots");
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View itemView = getView();
        recycler_time_slot = itemView.findViewById(R.id.recycler_time_slot);
        horizontalCalendarView = itemView.findViewById(R.id.calendar_view);
        proceed_btn = itemView.findViewById(R.id.proceed_btn);

        bundle = this.getArguments();
        picked_day = bundle.getInt("picked_day");
        picked_month = bundle.getInt("picked_month");
        picked_year = bundle.getInt("picked_year");
        room = bundle.getString("room");


        if(room.equals("Meeting Room")) {
            bookedRoom = "room1";
        } else if(room.equals("Conference Room")) {
            bookedRoom = "room2";
        }

        //instantiate some necessary objects
        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");   //eg: 25_03_2019 (this is key)
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        selected_date = Calendar.getInstance();
        selected_date.add(Calendar.DATE, 0);        //initialize with the current date

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
        //FOR OUR APP; we'll select the user-picked date as the defaultSelectedDate in Horizontal Calendar
        pickedDate = Calendar.getInstance();
        pickedDate.set(picked_year, picked_month, picked_day);

        horizontalCalendar = new HorizontalCalendar.Builder(itemView, R.id.calendar_view)
                .range(pickedDate, endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(pickedDate)
                .build();

        //we set an adapter for the recycler view, initially for the startDate on the horizontal calendar
        attachAdapter(getContext(),simpleDateFormat.format(pickedDate.getTime()),bookedRoom);
        bookingDate = pickedDate.getTime();

        //set the horizontal calendar listener, so that the slots update when the date is changed
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if(pickedDate.getTimeInMillis() != date.getTimeInMillis()) {
                    pickedDate = date;
                    //set the adapter for recycler view, with the new date; as selected upon change in horizontal calendar
                    attachAdapter(getContext(), simpleDateFormat.format(date.getTime()),bookedRoom);
                    Toast.makeText(getContext(), simpleDateFormat.format(date.getTime()), Toast.LENGTH_SHORT).show();
                    bookingDate = date.getTime();
                }
            }
        });

        proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder selectedTime = new StringBuilder();
//                selectedTime.append("BookedSlots: ");
                for (TimeSlot_Data timeSlot_data : timeSlotList) {
                    if (timeSlot_data.isSelected())
                        selectedTime.append(timeSlot_data.getSlot()).append(" ");
                }

                if(selectedTime.length() == 0) {
                    final Snackbar alert = Snackbar.make(v,"Please select a slot to book!", Snackbar.LENGTH_SHORT);
                    alert.setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.dismiss();
                                }
                            });
                    alert.setActionTextColor(ContextCompat.getColor(getContext(),R.color.snackbarColor));
                    alert.show();
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("bookedSlots", selectedTime.toString());
                    bundle.putString("room", room);
                    bundle.putString("bookingDate", simpleDateFormat.format(bookingDate));

                    //NEW
                    bundle.putString("company",loggedInCompany);

                    Fragment newFragment = new Details();
                    newFragment.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_main, newFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }

//                Snackbar.make(v,selectedTime, Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private List<TimeSlot_Data> getSlotList() {
        timeSlotList = new ArrayList<>();
        for(int i=0; i< 18; i++)       //coz 18 slots in total, so we are sending an arraylist of timeslot_data type; which will then be segregated
            // into booked and available ones, in the adapter class
            timeSlotList.add(new TimeSlot_Data(i));
        return timeSlotList;
    }

    private boolean fetchBookedSlot(final String slotDate,String bookedRoom) {

        db = FirebaseFirestore.getInstance();
        final global_variables var = new global_variables();
        final CollectionReference bookedSlots = db.collection("Bookings")
                .document(bookedRoom)
                .collection(slotDate);
        bookedSlots.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty())  {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            tempString += document.getLong("slot").intValue() + " ";
                        }
//                        tempString += dialogMsg;
//                        adapter = new NewTimeSlotAdapter(getContext(),getSlotList(),tempString);
                        //NEW
                        adapter = new BookingTimeSlotAdapter(getContext(),getSlotList(),tempString);
                    } else {
//                        adapter = new NewTimeSlotAdapter(getContext(),getSlotList(),"");
                        //NEW
                        adapter = new BookingTimeSlotAdapter(getContext(),getSlotList(),"");
                    }
                    recycler_time_slot.setAdapter(adapter);
                    alertDialog.dismiss();
                    proceed_btn.setVisibility(View.VISIBLE);
                    tempString = "";
                }
            }
        });

        return true;
    }

    public void attachAdapter(Context context, String slotDate,String bookedRoom){
        alertDialog.show();
        fetchBookedSlot(slotDate,bookedRoom);
    }
}

