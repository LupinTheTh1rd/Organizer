package com.example.homepage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import dmax.dialog.SpotsDialog;

public class Login extends Fragment implements View.OnFocusChangeListener{

//    Button bdirector,badmin,benter;
//    boolean dir_or_admin=false;
//    TextView tvpass;
//    FirebaseFirestore db;
//    String pass_admin, pass_dir;
//    AlertDialog alertDialog;
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        //Fetch the password for admin and director, as soon as the activity is created
//        //this way, no delay in validating login; when user enters the password and clicks the submit button
////        pass_dir = dir_pass();
////        pass_admin = admin_pass();
//
//        //tried using callback, so as to make the app wait till password are fetched from database (not using anymore)
////        pass_admin = admin_pass(new MyCallback() {
////            @Override
////            public String onAdminData(String pass) {
////                return null;
////            }
////        });
//
//        final View view = getView();
//        bdirector =  view.findViewById(R.id.bdirector);
//        badmin =  view.findViewById(R.id.badmin);
//        tvpass =  view.findViewById(R.id.tvpass);
//        benter = view.findViewById(R.id.benter);
//
//        badmin.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorTrans));
//        badmin.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPress));
//        bdirector.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPress));
//        bdirector.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorTrans));
//
//        bdirector.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                tvpass.setText(null);
//                badmin.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorTrans));
//                badmin.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPress));
//                bdirector.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPress));
//                bdirector.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorTrans));
//                dir_or_admin = false;
//            }
//        });
//        badmin.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                tvpass.setText(null);
//                badmin.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPress));
//                badmin.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorTrans));
//                bdirector.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorTrans));
//                bdirector.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPress));
//                dir_or_admin = true;
//            }
//        });
//        benter.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //if dir_or_admin is false; it refers to the DIRECTOR
//                alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
////                alertDialog.show();
//                if (!dir_or_admin) {
////                    if (tvpass.getText().toString().equals(dir_pass())) {         //verify password with the password stored in Firebase (for director)
//                    if(tvpass.getText().toString().equals("1122")) {
//                        Fragment newFragment = new Booking();
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        ft.replace(R.id.content_main, newFragment);
//                        ft.addToBackStack(null);
//                        ft.commit();
//
//                        //Specify that the user has successfully logged in, so next time, the navbar button for Booking will redirect direct to the
//                        //booking page.
//                        ((global_variables) getContext().getApplicationContext()).setLogin_done(true);
//
//    //Set the details of login into the navigation bar header
////
////                        method-1
////                        View nav_view = View.inflate(getActivity(), R.layout.nav_header_main, null);
////                        TextView header = nav_view.findViewById(R.id.nav_header);
////                        TextView sub_header = nav_view.findViewById(R.id.nav_sub_header);
////                        header.setText("DIRECTOR");
////                        sub_header.setVisibility(View.VISIBLE);
////                        sub_header.setText("director23@gmail.com");
//
//                        //method-2
////                        View nav_view = View.inflate(getActivity(), R.layout.activity_main, null);
////                        NavigationView navigationView = nav_view.findViewById(R.id.nav_view);
////                        View hView =  navigationView.inflateHeaderView(R.layout.nav_header_main);
////                        TextView nav_user = hView.findViewById(R.id.nav_header);
////                        nav_user.setText("DIRECTOR");
//
//                        //method-3
////                        LayoutInflater lf = getActivity().getLayoutInflater();
////                        View nav_view =  lf.inflate(getContext(),R.layout.nav_header_main, false);
//
//                        //was trying to add a new navbar option to logout
////                        View view1 = View.inflate(getContext(), R.layout.activity_main, null);
////                        NavigationView navView = view1.findViewById(R.id.nav_view);
////                        Menu menu = navView.getMenu();
//////                        Menu submenu = menu.addSubMenu("New Super SubMenu");
////                        menu.add("Logout");
////                        navView.invalidate();
//                    //Tried changing the text on nav bar
//                    } else if (tvpass.getText().toString().isEmpty()) {
//                        Toast.makeText(getContext(), "PLEASE ENTER PIN", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getContext(), "PLEASE ENTER CORRECT PIN", Toast.LENGTH_SHORT).show();
////                        Toast.makeText(getContext(), dir_pass(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    if (tvpass.getText().toString().equals(pass_admin)) {        //verify password with the password stored in Firebase (for admin)
//                        Fragment newFragment = new Booking();
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        ft.replace(R.id.content_main, newFragment);
//                        ft.addToBackStack(null);
//                        ft.commit();
//
//                        //Specify that the user has successfully logged in, so next time, the navbar button for Booking will redirect direct to the
//                        //booking page.
//                        ((global_variables) getContext().getApplicationContext()).setLogin_done(true);
//
//                        //Set the details of login into the navigation bar header
//                        //                        View nav_view = View.inflate(getContext(), R.layout.nav_header_main, null);
////                        TextView header = nav_view.findViewById(R.id.nav_header);
////                        TextView sub_header = nav_view.findViewById(R.id.nav_sub_header);
////                        header.setText("Admin");
////                        sub_header.setVisibility(View.VISIBLE);
////                        sub_header.setText("admin123@gmail.com");
//
//                    } else if (tvpass.getText().toString().equals("")) {
//                        Toast.makeText(getContext(), "PLEASE ENTER PIN", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getContext(), "PLEASE ENTER CORRECT PIN", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//    }
//
//    private String admin_pass() {
//        db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("UserAndPassword").document("admin_password");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot doc = task.getResult();
//                    if (doc.exists()) {
//                        pass_admin = doc.getString("pass");
//                    } else {
//                        Toast.makeText(getContext(), "Failed to fetch:" + task.getException(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//        return pass_admin;
//    }
//
//    private String dir_pass() {
//        db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("UserAndPassword").document("director_password");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot doc = task.getResult();
//                    if (doc.exists()) {
//                        pass_dir = doc.getString("pass");
////                                Toast.makeText(getContext(), pass_dir, Toast.LENGTH_SHORT).show();
////                        alertDialog.dismiss();
//                    } else {
//                        Toast.makeText(getContext(), "Failed to fetch:" + task.getException(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//        return pass_dir;
//    }
//
//    //function to register the password to the callback function, when fetched from database (not using anymore)
////    private void admin_pass(final MyCallback callback) {
////        db = FirebaseFirestore.getInstance();
////        DocumentReference docRef = db.collection("UserAndPassword").document("admin_password");
////        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                if (task.isSuccessful()) {
////                    DocumentSnapshot doc = task.getResult();
////                    if (doc.exists()) {
////                        pass_admin = doc.getString("pass");
////                        callback.onAdminData(pass_admin);
////                    } else {
////                        Toast.makeText(getContext(), "Failed to fetch:" + task.getException(), Toast.LENGTH_SHORT).show();
////                    }
////                }
////            }
////        });
////    }
//
//
//
////    public interface MyCallback {
////        String onAdminData(String pass);
////    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.login, container, false);
//
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        getActivity().setTitle("Login");
//    }
    EditText userId, pass;
    Button login;
    String username,password;
    FirebaseFirestore mFirestore;
    AlertDialog loadingDialog;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        userId = view.findViewById(R.id.username);
        pass = view.findViewById(R.id.password);
        pass.setTransformationMethod(new PasswordTransformationMethod());
        login = view.findViewById(R.id.submit);
        loadingDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!userId.getText().toString().isEmpty() &&
                    !pass.getText().toString().isEmpty()) {
                    login.setEnabled(true);
                } else login.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        userId.addTextChangedListener(textWatcher);
        pass.addTextChangedListener(textWatcher);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                username = userId.getText().toString();
                password = pass.getText().toString();
                checkIfValid(username,password);
            }
        });

        //NEW
        //close keypad when user touches anywhere on screen (except keypad), more like when the focus changes from editText
        userId.setOnFocusChangeListener(this);
        pass.setOnFocusChangeListener(this);
    }

    private void checkIfValid(final String name, final String key) {
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("Authorized_People")
                .document(name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()) {
                                loadingDialog.dismiss();
                                if(key.equals(doc.getString("password"))) {
                                    String company = doc.getString("company");
                                    allowBooking(name, company);
                                } else {
                                    Toast.makeText(getContext(), "Incorrect Password! Try again!", Toast.LENGTH_LONG).show();
                                    clearPassField();       //clear the editText for password, and allow user to enter correct credentials
                                }
                            } else {
                                loadingDialog.dismiss();
                                final Snackbar notUser = Snackbar.make(getView(),"You are not authorized! Contact Admin!",Snackbar.LENGTH_SHORT);
                                notUser.setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        notUser.dismiss();
                                    }
                                });
                                notUser.setActionTextColor(ContextCompat.getColor(getContext(),R.color.snackbarColor));
                                notUser.show();
                                clearAllFields();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearAllFields() {
        userId.setText("");
        pass.setText("");
        userId.requestFocus();
    }

    private void clearPassField() {
        pass.setText("");
        pass.requestFocus();
    }

    private void allowBooking(String user, String company) {
        //If control reaches here, it means that the user is authorized to book meetings, so redirect to booking page
        //We need to forward the company name as well, since this user can only book meetings on registered company's name
//        Bundle bundle = new Bundle();
//        bundle.putString("username",user);
//        bundle.putString("company",company);
        ((global_variables) getActivity().getApplication()).setCompany(company);
        ((global_variables) getActivity().getApplication()).setName(user);

        //Also, we set the package-wide global variable, which allows the "Book a hall" option to redirect directly to booking, if the user has already logged in.
        ((global_variables) getActivity().getApplication()).setLogin_done(true);

        //Redirect to fragment
        Fragment newFragment = new Booking();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main,newFragment);
        ft.commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Login");
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
