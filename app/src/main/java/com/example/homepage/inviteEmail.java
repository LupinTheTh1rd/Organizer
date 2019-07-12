package com.example.homepage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class inviteEmail extends Fragment {

    EditText emailId;
    TextView header2;
    HashMap<String, String> email_map;
    List<HashMap<String, String>> aList;
    Button send_email,skip_email;

    int EMAIL_SENT=100;

    //NEW
    String bookedDate,bookedUser,bookedSlots,bookedCompany,bookedRoom;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invite_email, container,false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
              @Override
              public boolean onKey(View v, int keyCode, KeyEvent event) {
//                  return keyCode == KeyEvent.KEYCODE_BACK;
                  if (event.getAction() == KeyEvent.ACTION_DOWN) {
                      if (keyCode == KeyEvent.KEYCODE_BACK) {

                          return true;
                      }
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

        getActivity().setTitle("Send Invites");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        aList = new ArrayList<>();
        header2 = view.findViewById(R.id.email_header2);
        emailId = view.findViewById(R.id.email_text);
        send_email=view.findViewById(R.id.send_email);
        skip_email=view.findViewById(R.id.skip_email);

        emailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final ListView listView =getView().findViewById(R.id.list_email);

//               char a = s.charAt(before);
                if (emailId.getText().toString().contains(",")) {
                    String str=emailId.getText().toString();
                    String listitem = str.substring(0,str.length()-1);
                    listView.setVisibility(View.VISIBLE);
                    header2.setVisibility(View.VISIBLE);
//                   header2.setText(str.substring(0,str.length()-1));
                    emailId.setText("");
//                   start=0;
//                   before=0;
//                   count=0;
//                    Toast.makeText(MainActivity.this, s.charAt(start), Toast.LENGTH_SHORT).show();
                    email_map = new HashMap<>();
                    email_map.put("email", listitem);
                    emailId.setText(null);
                    aList.add(email_map);
                    add_list(listView);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                            aList.remove(index);

                            add_list(listView);
                        }
                    });
//               }
                    listView.setOnTouchListener(new ListView.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = event.getAction();
                            switch (action) {
                                case MotionEvent.ACTION_DOWN:
                                    // Disallow ScrollView to intercept touch events.
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                    break;

                                case MotionEvent.ACTION_UP:
                                    // Allow ScrollView to intercept touch events.
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    break;
                            }

                            // Handle ListView touch events.
                            v.onTouchEvent(event);
                            return true;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //receive booking info from previous class
        Bundle info = getArguments();
        bookedDate = info.getString("date");
        bookedUser= info.getString("name");
        bookedSlots = fetchSlots(info.getString("slots"));
        bookedRoom = info.getString("room").equals("room1")
                ? "Meeting Room"
                : "Conference Room";
        bookedCompany = ((global_variables)getActivity().getApplication()).getCompany();


        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(v);
            }
        });

        skip_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipEmail(v);
            }
        });
    }

    public void sendEmail(View view)
    {
        StringBuilder str=new StringBuilder();
        String[] recipient=new String[aList.size()+1];
        for (int i=0;i<aList.size();i++)
        {
            recipient[i]=(aList.get(i).get("email"));
            str.append(recipient[i]);
        }

        //NEW
        if(!emailId.getText().toString().isEmpty()) {     // if the user types in the last email id, and does not segregate it with a comma, take the id as it is from the edit text itself
            recipient[aList.size()]=emailId.getText().toString();
        }
        String subject="Invitation for meeting at "+bookedCompany.toUpperCase();
        String message="I am pleased to invite you for a meeting at "+bookedCompany.toUpperCase()+"\n"+
                        "Venue: "+bookedRoom+"\n"+
                        "Date:  "+formatDate(bookedDate)+"\n"+
                        "Time:  "+bookedSlots.substring(0,5)+"\n"+
                        "Have a nice day!"+"\n\n\n"+
                        "--THIS IS AN AUTO GENERATED MAIL--\n--PLEASE DO NOT REPLY--\n";
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "", null));
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
//        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        //NEW
        startActivityForResult(Intent.createChooser(intent, "Choose an Email client :"),EMAIL_SENT);
    }

    public void add_list(ListView listView)
    {
        String[] from = {"email"};
        int[] to = {R.id.email};
//


        final SimpleAdapter adapter = new SimpleAdapter(getContext(),aList, R.layout.layout_email_list, from, to);
//
//
        listView.setAdapter(adapter);
    }

    public void skipEmail(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Success!");
        alertDialog.setMessage("Your booking has been registered successfully!\nThank you for using Organizer!");
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment newFragment = new Viewing();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, newFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EMAIL_SENT) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("SUCCESS!");
            dialog.setMessage("Invitations sent successfully!\n Thank you for using Organizer!");
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Fragment newFragment = new Viewing();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_main, newFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            dialog.show();
        }
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

    private String formatDate(String date) {
        return date.substring(0,2)+"/"+date.substring(3,5)+"/"+date.substring(6);
    }
}
