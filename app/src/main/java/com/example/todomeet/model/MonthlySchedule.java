package com.example.todomeet.model;

import java.io.Serializable;

public class MonthlySchedule implements Serializable {
    private int projectId;
    private String startTime;
    private String endTime;
    private String eventName;
    private String day;
    private String memo;
    private boolean check;

    public MonthlySchedule(int projectId, String startTime, String endTime, String eventName,
                           String day, String memo, boolean check) {
        this.projectId = projectId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventName = eventName;
        this.day = day;
        this.memo = memo;
        this.check = check;
    }
    @Override
    public String toString() {
        return "MonthlySchedule{" +
                "id='" + projectId + '\'' +
                "start='" + startTime + '\'' +
                "end='" + endTime + '\'' +
                "event='" + eventName + '\'' +
                "memo='" + memo + '\'' +
                "day='" + day + '\'' +
                "check='" + check + '\'' +
                '}';
    }

    public String getDay() {
        return day;
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

    public int getProjectId() {
        return projectId;
    }
    public void setCheck(boolean checked) {
        this.check = checked;
    }

    public int getCenvertStartTime() {
        String tmp[];

        tmp = startTime.split(":", 2);
        return Integer.parseInt(tmp[0]);
    }
    public int getCenvertEndTime() {
        String tmp[];

        tmp = endTime.split(":", 2);
        return Integer.parseInt(tmp[0]);
    }

}
