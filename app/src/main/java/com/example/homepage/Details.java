//package com.example.homepage;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.telephony.SmsManager;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.homepage.Adapter.NewTimeSlotAdapter;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.auth.User;
//
//import java.lang.reflect.Array;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import com.example.homepage.Adapter.NewTimeSlotAdapter.*;
//import com.jaredrummler.materialspinner.MaterialSpinner;
//
//import dmax.dialog.SpotsDialog;
//
//public class Details extends Fragment {
//    View view;
//    TextView room, bookedSlots;
//    private Button bproceed;
//        private EditText etname, etmobile, etcname, etpurpose;
//
//    Bundle bundle;
//    FirebaseFirestore mFirestore;
//
//    String name, mobile, cname, purpose, requestedRoom;
//    String slots;
//    String bookingDate, bookedRoom;
//    AlertDialog alertDialog;
////    int currentLimit;
//
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        view = getView();
//        room = view.findViewById(R.id.tvroomval);
//        bproceed = view.findViewById(R.id.bproceed);
//        etname = view.findViewById(R.id.etname);
//        etcname = view.findViewById(R.id.etcname);
//        etmobile = view.findViewById(R.id.etmobile);
//        etpurpose = view.findViewById(R.id.etpurpose);
//        bookedSlots = view.findViewById(R.id.tvtimingval);
//        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
//
//        //Receive the name of the conference hall to be booked
//        bundle = this.getArguments();
//        room.setText(bundle.getString("room"));
//        slots = bundle.getString("bookedSlots");
//        bookingDate = bundle.getString("bookingDate");
//        bookedSlots.setText(fetchSlots(slots));             //set the timings booked, to display
//
//        TextWatcher loginTextWatcher = new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String name, mobile, cname, purpose;
//                name = etname.getText().toString();
//                mobile = etmobile.getText().toString();
//                cname = etcname.getText().toString();
//                purpose = etpurpose.getText().toString();
//
//                //enable the proceed button, only when the necessary entries have been filled
//                bproceed.setEnabled(!name.isEmpty() && !mobile.isEmpty() && !cname.isEmpty() && !purpose.isEmpty());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//        };
//        etname.addTextChangedListener(loginTextWatcher);
//        etmobile.addTextChangedListener(loginTextWatcher);
//        etcname.addTextChangedListener(loginTextWatcher);
//        etpurpose.addTextChangedListener(loginTextWatcher);
//
//        //Actions to perform when user wants to proceed (after all the details have been entered)
//        bproceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//                name = etname.getText().toString();
//                mobile = etmobile.getText().toString();
//                cname = etcname.getText().toString();
//                purpose = etpurpose.getText().toString();
////                slots = bookedSlots.getText().toString();
//                requestedRoom = room.getText().toString();
//                bookedRoom = "";
//                if (requestedRoom.equals("Conference Room 1")) {
//                    bookedRoom = "room1";
//                } else if (requestedRoom.equals("Conference Room 2")) {
//                    bookedRoom = "room2";
//                }
//
//                //Check if mobile number is valid
//                //Mobile number checked on 3 constraints: 1. starts with 0 or 91, 2. has first digit as 7/8/9, 3. remaining 9 digits are 0-9
////                String mob_no = etmobile.getText().toString();
////                Pattern p = Pattern.compile("(0/91)?[6-9][0-9]{9}");
////                Matcher m = p.matcher(mobile);
////                if (!(m.find() && m.group().equals(mobile))) {
////                    Toast.makeText(getContext(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
////                } else {      //if control enters here, it means everything is confirmed, just write to database
//
//                    mFirestore = FirebaseFirestore.getInstance();
//                    final int numberOfBookings = numberOfBookedSlots(slots);
//                    final int[] slotNumberList = getSlotList(slots);
//
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//                    dialog.setTitle("Confirmation");
//                    String bookingMessage = "\n\t";
//                    for (int i = 0; i < numberOfBookings; i++) {
//                        bookingMessage += convertSlotToString(slotNumberList[i]);
//                        bookingMessage += " on " + bookingDate;
//                        bookingMessage += "\n\t";
//                    }
//                    dialog.setMessage("Confirm Booking?\n" + bookingMessage);
//                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
//                    dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            alertDialog.show();
//                            confirmedBooking(numberOfBookings, name, mobile, cname, purpose, slotNumberList);
//                            Fragment newFragment = new Viewing();
//                            FragmentTransaction ft = getFragmentManager().beginTransaction();
//                            ft.replace(R.id.content_main, newFragment);
//                            ft.addToBackStack(null);
//                            ft.commit();
//                        }
//                    });
//                    dialog.setNegativeButton(android.R.string.no, null);
//                    dialog.show();
////                }
//            }
//        });
//
//    }
//
//    private int checkLimitOrahi(final String company) {
//        final DocumentReference docRef = mFirestore
//                .collection("Bookings")
//                .document("room2");
//        final int[] confirmOrahi = new int[1];
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot doc = task.getResult();
//                    if (doc.exists()) {
////                        mFirestore.collection("Bookings").document("UserID").update("user_id",(doc.getLong("user_id").intValue())+1);
//                        int currentLimit = doc.getLong("limit_orahi").intValue();
//                        if (currentLimit >= 20) {            //Limit for Orahi is 10hr/month; i.e 20 slots
//                            confirmOrahi[0] = 0;           //if 20 slots completed;
////                            confirmOrahi = false;
////                            Toast.makeText(getContext(), currentLimit+"", Toast.LENGTH_SHORT).show();
//                        } else {
//                            confirmOrahi[0] = 1;            //if less than 20 slots booked, allow
//                            docRef.update(company, currentLimit + 1);
////                            confirmOrahi = true;
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "Failed to fetch:" + task.getException(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//        return confirmOrahi[0];
//    }
//
//    private void confirmedBooking(final int numberOfBookings, String name, String mobile, String cname, String purpose, int[] slotNumberList) {
//        for (int j = 0; j < numberOfBookings; j++) {
//            final Map<String, Object> bookedMap = new HashMap<>();
//            bookedMap.put("name", name);
//            bookedMap.put("mobile", mobile);
//            bookedMap.put("company", cname);
//            bookedMap.put("purpose", purpose);
//            bookedMap.put("slot", slotNumberList[j]);
//            bookedMap.put("time", convertSlotToString(slotNumberList[j]));
//
//            if (bookedRoom.equals("room2")) {
//                switch (cname.toLowerCase()) {
//                    case "orahi":
//                        //check if limit reached or not
//                        //based on whether or not the limit has been reached,
//                        //write to database, and confirm booking if limit not reached
//                        //reject booking, and inform user that no more booking possible if limit reached.
//                        final DocumentReference docRef = mFirestore
//                                .collection("Bookings")
//                                .document("room2");
//                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    DocumentSnapshot doc = task.getResult();
//                                    if (doc.exists()) {
//                                        final int currentLimit = doc.getLong("limit_orahi").intValue();
////                                        currentLimit = doc.getLong("limit_orahi").intValue();
//                                        if (currentLimit >= 20) {            //Limit for Orahi is 10hr/month; i.e 20 slots
////                                            Toast.makeText(getContext(), "Limit Reached!! Contact Admin to extend..", Toast.LENGTH_SHORT).show();
//                                            notAllowed();
//                                            alertDialog.dismiss();
//                                        } else {
////                                            docRef.update("limit_orahi", currentLimit + 1);
//                                            mFirestore.collection("Bookings")
//                                                    .document(bookedRoom)
//                                                    .collection(bookingDate)
//                                                    .document(bookedMap.get("slot").toString())
//                                                    .set(bookedMap)
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void aVoid) {
//                                                            alertDialog.dismiss();
////                                                            Toast.makeText(getContext(), "BOOKING SUCCESSFUL!", Toast.LENGTH_SHORT).show();
//                                                            allowed();
//                                                            docRef.update("limit_orahi", currentLimit+numberOfBookings);
//                                                        }
//                                                    }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
////                                            docRef.update("limit_orahi", currentLimit + 1);
////                                            docRef.update("limit_orahi", currentLimit+numberOfBookings);
//                                        }
//                                    } else {
//                                        Toast.makeText(getContext(), "Failed to fetch:" + task.getException(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        });
//                        break;
//
//                    case "designcut":
//                        //check if limit reached or not
//                        //based on whether or not the limit has been reached,
//                        //write to database, and confirm booking if limit not reached
//                        //reject booking, and inform user that no more booking possible if limit reached.
//                        final DocumentReference docRef1 = mFirestore
//                                .collection("Bookings")
//                                .document("room2");
//                        docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    DocumentSnapshot doc = task.getResult();
//                                    if (doc.exists()) {
//                                        int currentLimit = doc.getLong("limit_designcut").intValue();
//                                        if (currentLimit >= 10) {            //Limit for DesignCut is 5hr/month; i.e 10 slots
////                                            Toast.makeText(getContext(), "Limit Reached!! Contact Admin to extend..", Toast.LENGTH_SHORT).show();
//                                            notAllowed();
//                                            alertDialog.dismiss();
//                                        } else {
////                                            docRef1.update("limit_designcut", currentLimit + 1);
//                                            mFirestore.collection("Bookings")
//                                                    .document(bookedRoom)
//                                                    .collection(bookingDate)
//                                                    .document(bookedMap.get("slot").toString())
//                                                    .set(bookedMap)
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void aVoid) {
//                                                            alertDialog.dismiss();
////                                                            Toast.makeText(getContext(), "BOOKING SUCCESSFUL!", Toast.LENGTH_SHORT).show();
//                                                            allowed();
//                                                        }
//                                                    }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                            docRef1.update("limit_designcut", currentLimit+numberOfBookings);
//                                        }
//                                    } else {
//                                        Toast.makeText(getContext(), "Failed to fetch:" + task.getException(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        });
//                        break;
//
//                    case "pds":
//                        //no limit for pds employees
//                        break;
//                }
//            }
//
//            else {
//                mFirestore.collection("Bookings")
//                        .document(bookedRoom)
//                        .collection(bookingDate)
//                        .document(bookedMap.get("slot").toString())
//                        .set(bookedMap)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                alertDialog.dismiss();
////                                Toast.makeText(getContext(), "BOOKING SUCCESSFUL!", Toast.LENGTH_SHORT).show();
//                                allowed();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }
//    }
//
//    private void allowed() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
//        alertDialog.setTitle("Success!");
//        alertDialog.setMessage("Your booking has been registered successfully!\nThank you for using Organizer!");
//        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
//        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        alertDialog.create();
//        alertDialog.show();
//    }
//
//    private void notAllowed() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
//        alertDialog.setTitle("Booking Failed!");
//        alertDialog.setMessage("Limit Reached! No more bookings allowed for this company!");
//        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
//        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        alertDialog.create();
//        alertDialog.show();
//    }
//
//
//    private int[] getSlotList(String slots) {
//        int numberOfSlots = numberOfBookedSlots(slots);
//        int[] listOfSlots = new int[numberOfSlots];
//        int temp = 0;
//        for (String i : slots.split("\\s")) {
//            listOfSlots[temp] = Integer.parseInt(i);
//            temp++;
//        }
//        return listOfSlots;
//    }
//
//    private String fetchSlots(String slots) {
//        String time_slots = "";
//        if (slots != null && !slots.isEmpty()) {
//            for (String i : slots.split("\\s")) {
//                time_slots += convertSlotToString(Integer.parseInt(i));
//                time_slots += "\n";
//            }
//        }
//        return time_slots;
//    }
//
//    private int numberOfBookedSlots(String bookedSlots) {
//        int count = 0;
//        if (bookedSlots != null && !bookedSlots.isEmpty()) {
//            for (String i : slots.split("\\s")) {
//                count++;
//            }
//        }
//        return count;
//    }
//
//    private String convertSlotToString(int slot) {
//        switch (slot) {
//            case 0:
//                return "09:00 - 09:30";
//            case 1:
//                return "09:30 - 10:00";
//            case 2:
//                return "10:00 - 10:30";
//            case 3:
//                return "10:30 - 11:00";
//            case 4:
//                return "11:00 - 11:30";
//            case 5:
//                return "11:30 - 12:00";
//            case 6:
//                return "12:00 - 12:30";
//            case 7:
//                return "12:30 - 13:00";
//            case 8:
//                return "13:00 - 13:30";
//            case 9:
//                return "13:30 - 14:00";
//            case 10:
//                return "14:00 - 14:30";
//            case 11:
//                return "14:30 - 15:00";
//            case 12:
//                return "15:00 - 15:30";
//            case 13:
//                return "15:30 - 16:00";
//            case 14:
//                return "16:00 - 16:30";
//            case 15:
//                return "16:30 - 17:00";
//            case 16:
//                return "17:00 - 17:30";
//            case 17:
//                return "17:30 - 18:00";
//            default:
//                return "Closed";
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.details, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        getActivity().setTitle("Enter Details");
//    }
//}
//
///*
//        android:id="@+id/ivconference2"
//        android:layout_width="903dp"
//        android:layout_height="350dp"
//        app:layout_constraintEnd_toEndOf="parent"
//        app:layout_constraintHorizontal_bias="0.504"
//        app:layout_constraintStart_toStartOf="parent"
//        app:layout_constraintTop_toTopOf="parent"
//        app:srcCompat="@drawable/dummy1"
// */

package com.example.homepage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepage.Adapter.NewTimeSlotAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.homepage.Adapter.NewTimeSlotAdapter.*;
import com.jaredrummler.materialspinner.MaterialSpinner;

import dmax.dialog.SpotsDialog;

public class Details extends Fragment implements View.OnFocusChangeListener{
    View view;
    TextView room, bookedSlots;
    private Button bproceed;
    private EditText etname, etmobile, etcname, etpurpose;

    Bundle bundle;
    FirebaseFirestore mFirestore;

    String name, mobile, cname, purpose, requestedRoom;
    String slots;
    String bookingDate, bookedRoom;
    AlertDialog alertDialog;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    boolean success;
//    int currentLimit;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        view = getView();
        room = view.findViewById(R.id.tvroomval);
        bproceed = view.findViewById(R.id.bproceed);
        etname = view.findViewById(R.id.etname);
        etcname = view.findViewById(R.id.etcname);
        etmobile = view.findViewById(R.id.etmobile);
        etpurpose = view.findViewById(R.id.etpurpose);
        bookedSlots = view.findViewById(R.id.tvtimingval);
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        //NEW
        etcname.setText(((global_variables) getActivity().getApplication()).getCompany());
        //set the company name in edittext for company
        // (company fetched from user who logged in)

        //Receive the name of the conference hall to be booked
        bundle = this.getArguments();
        room.setText(bundle.getString("room"));
        slots = bundle.getString("bookedSlots");
        bookingDate = bundle.getString("bookingDate");
        bookedSlots.setText(fetchSlots(slots));             //set the timings booked, to display

        TextWatcher loginTextWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name, mobile, cname, purpose;
                name = etname.getText().toString();
                mobile = etmobile.getText().toString();
//                cname = etcname.getText().toString();
                //NEW
                cname = ((global_variables) getActivity().getApplication()).getCompany();
                purpose = etpurpose.getText().toString();

                //enable the proceed button, only when the necessary entries have been filled
                bproceed.setEnabled(!name.isEmpty() && !mobile.isEmpty() && !cname.isEmpty() && !purpose.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        };
        etname.addTextChangedListener(loginTextWatcher);
        etmobile.addTextChangedListener(loginTextWatcher);
        etcname.addTextChangedListener(loginTextWatcher);
        etpurpose.addTextChangedListener(loginTextWatcher);

        //Actions to perform when user wants to proceed (after all the details have been entered)
        bproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                name = etname.getText().toString();
                mobile = etmobile.getText().toString();
//                cname = etcname.getText().toString();
                //NEW
                cname = ((global_variables) getActivity().getApplication()).getCompany();
                purpose = etpurpose.getText().toString();
//                slots = bookedSlots.getText().toString();
                requestedRoom = room.getText().toString();
                bookedRoom = "";
                if (requestedRoom.equals("Meeting Room")) {
                    bookedRoom = "room1";
                } else if (requestedRoom.equals("Conference Room")) {
                    bookedRoom = "room2";
                }

                //Check if mobile number is valid
                if (mobile.length() != 10) {
                    Toast.makeText(getContext(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                } else {      //if control enters here, it means everything is confirmed, just write to database

                    mFirestore = FirebaseFirestore.getInstance();
                    final int numberOfBookings = numberOfBookedSlots(slots);
                    final int[] slotNumberList = getSlotList(slots);

                    if (bookedRoom.equals("room1")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle("Confirmation");
                        String bookingMessage = "\n\t";
                        for (int i = 0; i < numberOfBookings; i++) {
                            bookingMessage += convertSlotToString(slotNumberList[i]);
                            bookingMessage += " on " + bookingDate;
                            bookingMessage += "\n\t";
                        }
                        dialog.setMessage("Confirm Booking?\n" + bookingMessage);
                        dialog.setIcon(android.R.drawable.ic_dialog_alert);
                        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.show();
                                writeBooking(numberOfBookings, name, mobile, cname, purpose, slotNumberList);
                                //NEW
                                ((global_variables) getActivity().getApplication()).setLogin_done(false);
                                //NEW
//                                allowed();
                                //NEW
                                Fragment newFragment = new inviteEmail();
                                Bundle info = new Bundle();
                                info.putString("name",name);
                                info.putString("date",bookingDate);
                                info.putString("slots",slots);
                                info.putString("room",bookedRoom);
                                newFragment.setArguments(info);
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.content_main, newFragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });
                        dialog.setNegativeButton(android.R.string.no, null);
                        dialog.show();
                    } else {
                        switch (cname.toLowerCase()) {
                            case "orahi":
                                //check if limit reached or not
                                //based on whether or not the limit has been reached,
                                //write to database, and confirm booking if limit not reached
                                //reject booking, and inform user that no more booking possible if limit reached.
                                final DocumentReference docRef = mFirestore
                                        .collection("Bookings")
                                        .document("room2");
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            if (doc.exists()) {
                                                int currentLimit = doc.getLong("limit_orahi").intValue();
                                                checkForLimit(currentLimit, 20, numberOfBookings, slotNumberList);
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;

                            case "designcut":
                                //check if limit reached or not
                                //based on whether or not the limit has been reached,
                                //write to database, and confirm booking if limit not reached
                                //reject booking, and inform user that no more booking possible if limit reached.
                                final DocumentReference docRef1 = mFirestore
                                        .collection("Bookings")
                                        .document("room2");
                                docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            if (doc.exists()) {
                                                int currentLimit = doc.getLong("limit_designcut").intValue();
                                                checkForLimit(currentLimit, 10, numberOfBookings, slotNumberList);
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;

                            case "pds":
                                //no limit for pds employees
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                                dialog.setTitle("Confirmation");
                                String bookingMessage = "\n\t";
                                for (int i = 0; i < numberOfBookings; i++) {
                                    bookingMessage += convertSlotToString(slotNumberList[i]);
                                    bookingMessage += " on " + bookingDate;
                                    bookingMessage += "\n\t";
                                }
                                dialog.setMessage("Confirm Booking?\n" + bookingMessage);
                                dialog.setIcon(android.R.drawable.ic_dialog_alert);
                                dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.show();
                                        writeBooking(numberOfBookings, name, mobile, cname, purpose, slotNumberList);
                                        //NEW
                                        ((global_variables) getActivity().getApplication()).setLogin_done(false);
                                        //NEW
//                                        allowed();
                                        //NEW
                                        Fragment newFragment = new inviteEmail();
                                        Bundle info = new Bundle();
                                        info.putString("name",name);
                                        info.putString("date",bookingDate);
                                        info.putString("slots",slots);
                                        info.putString("room",bookedRoom);
                                        newFragment.setArguments(info);
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_main, newFragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                });
                                dialog.setNegativeButton(android.R.string.no, null);
                                dialog.show();
                                break;
                        }
                    }
                }
            }
        });
        etname.setOnFocusChangeListener(this);
        etmobile.setOnFocusChangeListener(this);
        etpurpose.setOnFocusChangeListener(this);
    }

    private void checkForLimit(int currentLimit, int limit, final int numberOfBookings, final int[] slotNumberList) {
        int total_limit = currentLimit + numberOfBookings;
        if (total_limit <= limit) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Confirmation");
            String bookingMessage = "\n\t";
            for (int i = 0; i < numberOfBookings; i++) {
                bookingMessage += convertSlotToString(slotNumberList[i]);
                bookingMessage += " on " + bookingDate;
                bookingMessage += "\n\t";
            }
            dialog.setMessage("Confirm Booking?\n" + bookingMessage);
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.show();
                    confirmedBooking(numberOfBookings, name, mobile, cname, purpose, slotNumberList);
                    //NEW
                    ((global_variables) getActivity().getApplication()).setLogin_done(false);
                    //NEW
//                    allowed();
                    //NEW
                    Fragment newFragment = new inviteEmail();
                    Bundle info = new Bundle();
                    info.putString("name",name);
                    info.putString("date",bookingDate);
                    info.putString("slots",slots);
                    info.putString("room",bookedRoom);
                    newFragment.setArguments(info);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_main, newFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            dialog.setNegativeButton(android.R.string.no, null);
            dialog.show();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Request Failed!");
            int remaining_limit = limit - currentLimit;
            if (remaining_limit == 0) {
                dialog.setMessage("Limit Reached!" + "\n" + "No more available slots!" + "\n" + "Limit will be renewed next month!");
            } else {
                dialog.setMessage("Limit Reached!\n Available slots = " + (limit - currentLimit) + "\nPlease choose appropriate number of slots!");
            }
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Fragment newFragment = new Booking();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_main, newFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            dialog.show();
        }
    }

    private int checkLimitOrahi(final String company) {
        final DocumentReference docRef = mFirestore
                .collection("Bookings")
                .document("room2");
        final int[] confirmOrahi = new int[1];
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
//                        mFirestore.collection("Bookings").document("UserID").update("user_id",(doc.getLong("user_id").intValue())+1);
                        int currentLimit = doc.getLong("limit_orahi").intValue();
                        if (currentLimit >= 20) {            //Limit for Orahi is 10hr/month; i.e 20 slots
                            confirmOrahi[0] = 0;           //if 20 slots completed;
//                            confirmOrahi = false;
//                            Toast.makeText(getContext(), currentLimit+"", Toast.LENGTH_SHORT).show();
                        } else {
                            confirmOrahi[0] = 1;            //if less than 20 slots booked, allow
                            docRef.update(company, currentLimit + 1);
//                            confirmOrahi = true;
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch:" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return confirmOrahi[0];
    }

    private void confirmedBooking(final int numberOfBookings, String name, String mobile, final String cname, String purpose, int[] slotNumberList) {
        for (int j = 0; j < numberOfBookings; j++) {
            final Map<String, Object> bookedMap = new HashMap<>();
            bookedMap.put("name", name);
            bookedMap.put("mobile", mobile);
            bookedMap.put("company", cname);
            bookedMap.put("purpose", purpose);
            bookedMap.put("slot", slotNumberList[j]);
            bookedMap.put("time", convertSlotToString(slotNumberList[j]));

            mFirestore.collection("Bookings")
                    .document(bookedRoom)
                    .collection(bookingDate)
                    .document(bookedMap.get("slot").toString())
                    .set(bookedMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            alertDialog.dismiss();
                            updateLimit(numberOfBookings, cname);
//                                Toast.makeText(getContext(), "BOOKING SUCCESSFUL!", Toast.LENGTH_SHORT).show();
//                                allowed();
                            setSuccess(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
//            }
        }
    }

    private void writeBooking(final int numberOfBookings, String name, String mobile, final String cname, String purpose, int[] slotNumberList) {
        for (int j = 0; j < numberOfBookings; j++) {
            final Map<String, Object> bookedMap = new HashMap<>();
            bookedMap.put("name", name);
            bookedMap.put("mobile", mobile);
            bookedMap.put("company", cname);
            bookedMap.put("purpose", purpose);
            bookedMap.put("slot", slotNumberList[j]);
            bookedMap.put("time", convertSlotToString(slotNumberList[j]));

            mFirestore.collection("Bookings")
                    .document(bookedRoom)
                    .collection(bookingDate)
                    .document(bookedMap.get("slot").toString())
                    .set(bookedMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            alertDialog.dismiss();
//                            updateLimit(numberOfBookings,cname);
//                                Toast.makeText(getContext(), "BOOKING SUCCESSFUL!", Toast.LENGTH_SHORT).show();
//                                allowed();
                            setSuccess(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
//            }
        }
    }

    private void updateLimit(final int numberOfBookings, String company) {
        String whichLimit = "";
        company = company.toLowerCase().trim();
        if (company.equals("orahi")) {
            whichLimit = "limit_orahi";
        } else if (company.equals("designcut")) {
            whichLimit = "limit_designcut";
        }

        final DocumentReference docRef1 = mFirestore
                .collection("Bookings")
                .document("room2");
        final String finalWhichLimit = whichLimit;
        docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        int currentLimit = doc.getLong(finalWhichLimit).intValue();
                        docRef1.update(finalWhichLimit, currentLimit + numberOfBookings);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void allowed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Success!");
        alertDialog.setMessage("Your booking has been registered successfully!\nThank you for using Organizer!");
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    private void notAllowed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Booking Failed!");
        alertDialog.setMessage("Limit Reached! No more bookings allowed for this company!");
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create();
        alertDialog.show();
    }


    private int[] getSlotList(String slots) {
        int numberOfSlots = numberOfBookedSlots(slots);
        int[] listOfSlots = new int[numberOfSlots];
        int temp = 0;
        for (String i : slots.split("\\s")) {
            listOfSlots[temp] = Integer.parseInt(i);
            temp++;
        }
        return listOfSlots;
    }

    private String fetchSlots(String slots) {
        String time_slots = "";
        if (slots != null && !slots.isEmpty()) {
            for (String i : slots.split("\\s")) {
                time_slots += convertSlotToString(Integer.parseInt(i));
                time_slots += "\n";
            }
        }
        return time_slots;
    }

    private int numberOfBookedSlots(String bookedSlots) {
        int count = 0;
        if (bookedSlots != null && !bookedSlots.isEmpty()) {
            for (String i : slots.split("\\s")) {
                count++;
            }
        }
        return count;
    }

    private String convertSlotToString(int slot) {
        switch (slot) {
            case 0:
                return "09:00 - 09:30";
            case 1:
                return "09:30 - 10:00";
            case 2:
                return "10:00 - 10:30";
            case 3:
                return "10:30 - 11:00";
            case 4:
                return "11:00 - 11:30";
            case 5:
                return "11:30 - 12:00";
            case 6:
                return "12:00 - 12:30";
            case 7:
                return "12:30 - 13:00";
            case 8:
                return "13:00 - 13:30";
            case 9:
                return "13:30 - 14:00";
            case 10:
                return "14:00 - 14:30";
            case 11:
                return "14:30 - 15:00";
            case 12:
                return "15:00 - 15:30";
            case 13:
                return "15:30 - 16:00";
            case 14:
                return "16:00 - 16:30";
            case 15:
                return "16:30 - 17:00";
            case 16:
                return "17:00 - 17:30";
            case 17:
                return "17:30 - 18:00";
            default:
                return "Closed";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Enter Details");
    }

    //NEW
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //NEW
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideKeyboard(v);
        }
    }
}
