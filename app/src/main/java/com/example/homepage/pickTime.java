package com.example.homepage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class pickTime extends Fragment {
    Button validate;
    TextView date, time_from, time_to;
    FirebaseFirestore mFirestore;
    String prefix = "user";
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        date = view.findViewById(R.id.tvdate);
        time_from = view.findViewById(R.id.tvtime1);
        time_to = view.findViewById(R.id.tvtime2);
        validate = view.findViewById(R.id.bbook);

        //Set a date picker dialog to choose date of booking, and set the selected date on the respective textview
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int d = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
//                        date.setText(dayOfMonth+" "+MONTHS[month]+" "+year);
                    }
                },y,m,d);
                datePickerDialog.show();
            }
        });

        //Set a time picker dialog to choose the start time of the meeting(booking) and set the selected time on the respective textview
        time_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int Hr = cal.get(Calendar.HOUR);
                int Min = cal.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String curtime = hourOfDay+":"+minute;
                        if(minute < 10){
                            curtime = hourOfDay+":0"+minute;
                        } else if(hourOfDay == 0) {
                            curtime = "12:"+minute;
                        }
                        time_from.setText(curtime);
//                        Toast.makeText(getContext(),curtime, Toast.LENGTH_SHORT).show();
                    }
                },Hr, Min, false);
                tpd.show();
            }
        });

        //Set a time picker dialog to choose the start time of the meeting(booking) and set the selected time on the respective textview
        time_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int Hr = cal.get(Calendar.HOUR)+1;              //suggesting the booking time of 1hour
                int Min = cal.get(Calendar.MINUTE);
                TimePickerDialog tpd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String curtime = hourOfDay+":"+minute;
                        if(minute < 10){
                            curtime = hourOfDay+":0"+minute;
                        } else if(hourOfDay == 0) {
                            curtime = "12:"+minute;
                        }
                        time_to.setText(curtime);
//                        Toast.makeText(getContext(),curtime, Toast.LENGTH_SHORT).show();
                    }
                },Hr, Min, false);
                tpd.show();
            }
        });
        
        //Button
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "ALL GOOD!!", Toast.LENGTH_SHORT).show();

                //fetch entered fields
                String d = date.getText().toString();
                String t1 = time_from.getText().toString();
                String t2 = time_to.getText().toString();
                String book_room = ((global_variables) getContext().getApplicationContext()).getRoom();

                //only let the user proceed, if he/she has entered all the details.
                if (!d.isEmpty() && !t1.isEmpty() && !t2.isEmpty()) {
//                    validate.setEnabled(true);
//                    if(verify_time(d, t1, t2, book_room)) {
                        enter_data();
//                    }


                    //add to firestore
//                    mFirestore.collection("booked").add(bookedMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Toast.makeText(getContext(), "Data added son!", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            String error = e.getMessage();
//                            Toast.makeText(getContext(), "Error: "+error, Toast.LENGTH_SHORT).show();
//                        }
//                    });


                } else {
                    Toast.makeText(getContext(), "Enter all details first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    public boolean verify_time(String date, String from, String to, String book_room) {
//
//        //Fetch the main collection based
//
//    }

    public void enter_data() {
        //Fetch the details user entered earlier
        String name, mobile, company, purpose, room;
        name = ((global_variables) getContext().getApplicationContext()).getName();
        company = ((global_variables) getContext().getApplicationContext()).getCompany();
        mobile = ((global_variables) getContext().getApplicationContext()).getMobile();
        purpose = ((global_variables) getContext().getApplicationContext()).getPurpose();
        room = ((global_variables) getContext().getApplicationContext()).getRoom();

        //Create instance for database
        mFirestore = FirebaseFirestore.getInstance();
        //Create a Hash Map object (to insert the data into database)
        final Map<String, Object> bookedMap = new HashMap<>();
        bookedMap.put("name", name);
        bookedMap.put("mobile", mobile);
        bookedMap.put("company", company);
        bookedMap.put("purpose", purpose);
        bookedMap.put("room", room);
        bookedMap.put("start_time", time_from.getText().toString());
        bookedMap.put("end_time", time_to.getText().toString());
        bookedMap.put("date", date.getText().toString());

        //Fetch the incremented user-id suffix (added to support uniqueness in id's of each document in database)
        DocumentReference docRef = mFirestore.collection("booked").document("UserID");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
//                                    ((global_variables) getContext().getApplicationContext()).setId(doc.getLong("user_id").intValue());
                        mFirestore.collection("booked").document(prefix +(doc.getLong("user_id").intValue())).set(bookedMap);
                        Toast.makeText(getContext(), "DATA INSERTED!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch:" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Increment the id-suffix and store it in database (so as to assign a new user, ending with the succeeding user-id-suffix)
        DocumentReference docRef1 = mFirestore.collection("booked").document("UserID");
        docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
//                                Toast.makeText(getContext(), "number of docs:", Toast.LENGTH_SHORT).show();
//                                    ((global_variables) getContext().getApplicationContext()).setId(doc.getLong("user_id").intValue());
                        mFirestore.collection("booked").document("UserID").update("user_id",(doc.getLong("user_id").intValue())+1);
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch:" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pick_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Timings");
    }
}
