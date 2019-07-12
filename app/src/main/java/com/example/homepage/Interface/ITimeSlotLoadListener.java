package com.example.homepage.Interface;

import com.example.homepage.TimeSlot_Data;

import java.sql.Time;
import java.util.List;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimeSlot_Data> timeSlotList);       //can pose error later (coz name is timeSlotList)
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();

}
