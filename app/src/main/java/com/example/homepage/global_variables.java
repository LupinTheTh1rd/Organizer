package com.example.homepage;

import android.app.Application;
import android.content.SharedPreferences;

public class global_variables extends Application {

    public static final Object DISABLE_TAG = "DISABLE";

    public boolean isLogin_done() {
        return login_done;
    }

    public void setLogin_done(boolean login_done) {
        this.login_done = login_done;
    }

    private boolean login_done=false;

    private String name;
    private String mobile;
    private String company;
    private String purpose;
    private String room;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    //for checking if user has selected on one of the time slots for booking
    public boolean isCheckSlot() {
        return checkSlot;
    }

    public void setCheckSlot(boolean checkSlot) {
        this.checkSlot = checkSlot;
    }

    private boolean checkSlot = false;

    //make an integer arraylist, which contains positions of the slots selected
    private int[] selectedSlots;

    public int[] getSelectedSlots() {
        return selectedSlots;
    }

    public void setSelectedSlots(int[] selectedSlots) {
        this.selectedSlots = selectedSlots;
    }
}
