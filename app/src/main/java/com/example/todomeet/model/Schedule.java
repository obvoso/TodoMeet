package com.example.todomeet.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Schedule {
    private String userEmail;
    private String eventName;
    private String startDay;
    private String endDay;
    private String memo;
    private List<TimeSlot> timeSlots;

    public Schedule(String userEmail, String eventName, String memo,
                    String startDay, String endDay, List<TimeSlot> timeSlots) {
       this.userEmail = userEmail;
       this.eventName = eventName;
       this.memo = memo;
       this.startDay = startDay;
       this.endDay = endDay;
       this.timeSlots = timeSlots;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Schedule {");
        sb.append("userEmail='").append(userEmail).append('\'');
        sb.append(", title='").append(eventName).append('\'');
        sb.append(", memo='").append(memo).append('\'');
        sb.append(", startDate='").append(startDay).append('\'');
        sb.append(", endDate='").append(endDay).append('\'');
        sb.append(", timeSlots=[");
        for (TimeSlot timeSlot : timeSlots) {
            sb.append(timeSlot.toString()).append(", ");
        }
        if (!timeSlots.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(']');
        sb.append('}');
        return sb.toString();
    }
}
