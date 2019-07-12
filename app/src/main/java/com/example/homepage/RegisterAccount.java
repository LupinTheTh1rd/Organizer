package com.example.homepage;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class RegisterAccount extends Fragment implements View.OnFocusChangeListener{

    EditText name, pass, confirmPass;
    AutoCompleteTextView companyName;
    Button submit;
    String userId, userPass, userConfirmPass, userCompany;
    FirebaseFirestore mFirestore;
    AlertDialog loadingDialog;

    private static final String[] COMPANIES = new String[] {
            "Orahi", "Design Cut", "PDS"
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        name = view.findViewById(R.id.username);
        pass = view.findViewById(R.id.password);
        pass.setTransformationMethod(new PasswordTransformationMethod());
        confirmPass = view.findViewById(R.id.confirmPassword);
        confirmPass.setTransformationMethod(new PasswordTransformationMethod());
        companyName = view.findViewById(R.id.companyInfo);
        setCompanyNames();

        submit = view.findViewById(R.id.submit);
        loadingDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( !name.getText().toString().isEmpty() &&
                    !pass.getText().toString().isEmpty() &&
                    !confirmPass.getText().toString().isEmpty() &&
                    !companyName.getText().toString().isEmpty())
                {
                    submit.setEnabled(true);
                } else {
                    submit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        name.addTextChangedListener(textWatcher);
        pass.addTextChangedListener(textWatcher);
        confirmPass.addTextChangedListener(textWatcher);
        companyName.addTextChangedListener(textWatcher);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                userId = name.getText().toString();
                userPass = pass.getText().toString();
                userConfirmPass = confirmPass.getText().toString();
                userCompany = companyName.getText().toString();

                if(!userConfirmPass.equals(userPass)) {
                    loadingDialog.dismiss();
                    final Snackbar alert = Snackbar.make(getView(),"Passwords do not match!", Snackbar.LENGTH_LONG);
                    alert.setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });
                    alert.setActionTextColor(ContextCompat.getColor(getContext(),R.color.snackbarColor));
                    alert.show();
                    //if this happens, also clear out the text field so user can enter it again..
                    pass.setText("");
                    confirmPass.setText("");
                    pass.requestFocus();
                } else {
                    registerAccountToDatabase(userId, userPass, userCompany);
                }
            }
        });

        //NEW
        //close keypad when user touches anywhere on screen (except keypad), more like when the focus changes from editText
        name.setOnFocusChangeListener(this);
        pass.setOnFocusChangeListener(this);
        confirmPass.setOnFocusChangeListener(this);
        companyName.setOnFocusChangeListener(this);

    }

    private void setCompanyNames() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, COMPANIES);
        companyName.setAdapter(adapter);
    }

    private void registerAccountToDatabase(String userId, String userPass, String userCompany) {
        mFirestore = FirebaseFirestore.getInstance();
        Map<String,String> newUser = new HashMap<>();
        newUser.put("username",userId);
        newUser.put("password",userPass);
        newUser.put("company",userCompany);
        mFirestore.collection("Authorized_People")
                .document(userId)
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showConfirmation();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showConfirmation() {
        loadingDialog.dismiss();
        final Snackbar registered = Snackbar.make(getView(),"New User registered Successfully!",Snackbar.LENGTH_LONG);
        registered.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registered.dismiss();
            }
        });
        registered.setActionTextColor(ContextCompat.getColor(getContext(), R.color.snackbarColor));
        registered.show();
        //after that redirect to Viewing page (i.e where the booking slots are displayed)
        Fragment newFragment = new Viewing();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main,newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Register an Account!");
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
