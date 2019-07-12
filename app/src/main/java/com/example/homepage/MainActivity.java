package com.example.homepage;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.example.homepage.Adapter.data_adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.log4j.chainsaw.Main;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseFirestore mFirestore;

    int STORAGE_PERMISSION_CODE = 1;
    Sheet sheet1;
    List<data_adapter> excelData1 = new ArrayList<>();
    List<data_adapter> excelData2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
        else {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            //Refresh the booking limit for "Orahi" and "DesignCut", on month change
            refreshLimit();


            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);

            Fragment newFragment = new Viewing();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, newFragment);
            ft.commit();

            //If you want to display the booking screen (by default) when the app opens
//        displaySelectedScreen(R.id.nav_view);
        }
    }

    private void refreshLimit() {
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("Bookings")
                .document("room2")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        checkForLimit(doc.getString("month"));

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkForLimit(String month) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = simpleDateFormat.format(cal.getTime());
        String currentMonth = currentDate.substring(3,5);
        if(!currentMonth.equals(month)){            //resets every month
            mFirestore.collection("Bookings")
                    .document("room2")
                    .update(
                            "limit_orahi",0,
                            "limit_designcut",0,    //1. Update limit to zero
                            "month",currentMonth                      //2. Update month to current month
                    );
//            Toast.makeText(this, "Limit refreshed!", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "Limit not refreshed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displaySelectedScreen(int id) {
        Fragment fragment = null;

        switch(id) {
            case R.id.nav_book:
                // Check if the user has logged in already,
                //if yes, redirect directly to the booking page
                //if not, redirect to login page

                boolean check = ((global_variables) this.getApplication()).isLogin_done();
                //switch fragments
                if(!check)
                    fragment = new Login();
                else
                    fragment = new Booking();
                break;
            case R.id.nav_view:
                fragment = new Viewing();
                break;
            case R.id.nav_register:
                fragment = new RegisterAccount();
                break;
        }

        if(fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void fetchBookingData(MenuItem item) {
//        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestForPermission();
        } else {            //if permission already granted
//                writeToExcel(excel.this);
            write(MainActivity.this);
        }
    }

    private void requestForPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted!!!!", Toast.LENGTH_SHORT).show();
//                writeToExcel(excel.this);
                write(MainActivity.this);
            } else {
                Toast.makeText(this, "Permission Denied!! T^T", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void write(Context context) {
        mFirestore = FirebaseFirestore.getInstance();
//        DocumentReference docRef = mFirestore.collection("Bookings")
//                .document("room1");
        final List<String> dates = dataList();
        List<CollectionReference> colRef1 = new ArrayList<>();
        List<CollectionReference> colRef2 = new ArrayList<>();

        for(String d: dates) {
            colRef1.add(mFirestore.collection("Bookings")
                    .document("room1")
                    .collection(d));
        }

        for(String d:dates) {
            colRef2.add(mFirestore.collection("Bookings")
                    .document("room2")
                    .collection(d));
        }

        for(int k=0;k<colRef1.size();k++) {
            final int finalK = k;
            colRef1.get(k)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                String temp = "";

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                data_adapter data = new data_adapter();
                                data.setName(doc.getString("name"));
                                data.setPurpose(doc.getString("purpose"));
                                data.setCompany(doc.getString("company"));
                                data.setTime(doc.getString("time"));
                                data.setMobile(doc.getString("mobile"));
                                data.setSlot(doc.getLong("slot").intValue());
                                data.setDate(dates.get(finalK));
                                excelData1.add(data);
                                temp += data.getName() + "   ";
                            }
                            intoExcel1(excelData1, MainActivity.this);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        for(int k=0;k<colRef2.size();k++) {
            final int finalK = k;
            colRef2.get(k)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                String temp = "";

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                data_adapter data = new data_adapter();
                                data.setName(doc.getString("name"));
                                data.setPurpose(doc.getString("purpose"));
                                data.setCompany(doc.getString("company"));
                                data.setTime(doc.getString("time"));
                                data.setMobile(doc.getString("mobile"));
                                data.setSlot(doc.getLong("slot").intValue());
                                data.setDate(dates.get(finalK));
                                excelData2.add(data);
                                temp += data.getName() + "   ";
                            }
                            intoExcel2(excelData2, MainActivity.this);
//                            showText();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        excelData1.clear();
        excelData2.clear();

    }


    private void intoExcel2(List<data_adapter> excelData, Context context) {
        Workbook wb = new HSSFWorkbook();
        Cell c = null;

        sheet1 = null;
        sheet1 = wb.createSheet("New sheet");

        //Generate column headings
        Row row = sheet1.createRow(0);
        c = row.createCell(0);
        c.setCellValue("Name");
//        c.setCellStyle(cs);
        c = row.createCell(1);
        c.setCellValue("Purpose");
//        c.setCellStyle(cs);
        c = row.createCell(2);
        c.setCellValue("Company");
        c = row.createCell(3);
        c.setCellValue("Time");
        c = row.createCell(4);
        c.setCellValue("Mobile");
        c = row.createCell(5);
        c.setCellValue("Slot");
        c = row.createCell(6);
        c.setCellValue("Date");

        int i = 1;
        int j = 0;
        for (data_adapter data : excelData) {
            row = sheet1.createRow(i);
            c = row.createCell(j);
            c.setCellValue(data.getName());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getPurpose());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getCompany());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getTime());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getMobile());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getSlot());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getDate());
            j = 0;
            i++;
        }

//        File file = new File(this.getExternalFilesDir(null), "BookingData-Room2.xls");
        //NEW
        File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)+"/BookingData", "BookingData-Room2.xls");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Toast.makeText(context, Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/BookingData", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error writing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to save file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
    }

    private void intoExcel1(List<data_adapter> excelData, Context context) {

        Workbook wb = new HSSFWorkbook();
        Cell c = null;

        sheet1 = null;
        sheet1 = wb.createSheet("New sheet");

        //Generate column headings
        Row row = sheet1.createRow(0);
        c = row.createCell(0);
        c.setCellValue("Name");
//        c.setCellStyle(cs);
        c = row.createCell(1);
        c.setCellValue("Purpose");
//        c.setCellStyle(cs);
        c = row.createCell(2);
        c.setCellValue("Company");
        c = row.createCell(3);
        c.setCellValue("Time");
        c = row.createCell(4);
        c.setCellValue("Mobile");
        c = row.createCell(5);
        c.setCellValue("Slot");
        c = row.createCell(6);
        c.setCellValue("Date");

        int i = 1;
        int j = 0;
        for (data_adapter data : excelData) {
            row = sheet1.createRow(i);
            c = row.createCell(j);
            c.setCellValue(data.getName());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getPurpose());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getCompany());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getTime());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getMobile());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getSlot());
            j++;
            c = row.createCell(j);
            c.setCellValue(data.getDate());
            j = 0;
            i++;
        }

        //NEW
        File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)+"/BookingData", "BookingData-Room1.xls");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
//            Toast.makeText(context, Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/BookingData", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error writing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to save file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
    }

    private List<String> dataList() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        Date today = null;
        try {
            today = simpleDateFormat.parse(simpleDateFormat.format(cal.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> dates = new ArrayList<>();
        String startDay = "17_06_2019";

        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(startDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = today.getTime() - startDate.getTime();
        int numberOfDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);


//        for(int i=0;i<32;i++) {
//            cal.add(Calendar.DAY_OF_MONTH,(-1));
//            dates.add(simpleDateFormat.format(cal.getTime()));
//        }
        Calendar dateCount = Calendar.getInstance();
        for(int i=0;i<numberOfDays;i++) {
            dateCount.add(Calendar.DAY_OF_MONTH,-1);
            dates.add(simpleDateFormat.format(dateCount.getTime()));
        }

        return dates;
    }

    public void logoutUser(MenuItem item) {
        //reset global variable, which defines whether a user has logged in to book, or not
        ((global_variables) this.getApplication()).setLogin_done(false);

        //show a message the user has been logged out
        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();

        //redirect back to viewing
        Fragment newFragment = new Viewing();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main,newFragment);
        ft.commit();
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }
}
