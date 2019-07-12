package com.example.homepage;

public class TimeSlot_Data {

    //old code
//    private Long slot;
//
//    public Long getSlot() {
//        return slot;
//    }
//
//    public void setSlot(Long slot) {
//        this.slot = slot;
//    }
//
//    public TimeSlot_Data() {
//    }

    public TimeSlot_Data(int slot) {
        this.slot = slot;
    }

    public TimeSlot_Data() {

    }

    private int slot;
    public int getSlot() {
        return slot;
    }



    private boolean isSelected = false;
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    private static final Object DISABLE_TAG= "DISABLE";
}
