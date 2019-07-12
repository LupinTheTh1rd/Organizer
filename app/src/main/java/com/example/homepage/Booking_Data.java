package com.example.homepage;

public class Booking_Data {
    private String name;
    private String mobile;
    private String company;
    private String purpose;
    private String room;

    //empty construtor (for initializing)
    public Booking_Data() {

    }

    //variable assignment constructor
    public Booking_Data(String name, String mobile, String company, String purpose, String room) {
        this.name = name;
        this.mobile = mobile;
        this.company = company;
        this.purpose = purpose;
        this.room = room;
    }

    //details entered by the user while booking
    //accessed through getters and setters

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
}
