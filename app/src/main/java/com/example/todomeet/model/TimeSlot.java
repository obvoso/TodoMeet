package com.example.todomeet.model;

public class TimeSlot {
    private String day;
    private String startTime;
    private String endTime;
    private boolean checked;

    public TimeSlot(String day, String startTime, String endTime, boolean checked) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.checked = checked;
    }
    @Override
    public String toString() {
        return "TimeSlot {" +
                "date='" + day + '\'' +
                ", starttime='" + startTime + '\'' +
                ", endtime='" + endTime + '\'' +
                ", checked='" + checked + '\'' +
                '}';
    }

};


