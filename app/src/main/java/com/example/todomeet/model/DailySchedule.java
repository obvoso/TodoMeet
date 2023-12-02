package com.example.todomeet.model;

public class DailySchedule {
    private int projectId;
    private String startTime;
    private String endTime;
    private String eventName;
    private String memo;
    private boolean check;

    public DailySchedule(int projectId, String startTime, String endTime, String eventName,
                         String memo, boolean check) {
        this.projectId = projectId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventName = eventName;
        this.memo = memo;
        this.check = check;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getMemo() {
        return memo;
    }

    public boolean isCheck() {
        return check;
    }
}
