package com.example.todomeet.model;

public class TimeSlot {
    private String day;
    private String startTime;
    private String endTime;

    public TimeSlot(String day, String startTime, String endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    @Override
    public String toString() {
        return "{" +
                "day='" + day + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }

};


