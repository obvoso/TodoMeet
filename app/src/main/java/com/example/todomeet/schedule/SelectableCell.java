package com.example.todomeet.schedule;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.MotionEvent;

import androidx.core.content.res.ResourcesCompat;

import com.example.todomeet.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class SelectableCell extends androidx.appcompat.widget.AppCompatTextView {
    private boolean isGreen = true;
    private boolean isClickable;
    private String time;
    private CalendarDay calendarDay;
    private  boolean isInitial;

    public SelectableCell(Context context) {
        super(context);

        setBackgroundDrawable(createBorderDrawable());

        setGravity(Gravity.CENTER);
        isClickable = true;

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickable) {
                    toggleBackgroundColor();
                }
            }
        });
    }
    public SelectableCell(Context context, CalendarDay calendarDay, String time,  boolean isInitial) {
        super(context);
        this.calendarDay = calendarDay;
        this.time = time;
        this.isInitial = isInitial;
        isGreen = !isInitial;

        setBackgroundDrawable(createBorderDrawable());

        setGravity(Gravity.CENTER);
        isClickable = true;

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickable) {
                    toggleBackgroundColor();
                }
            }
        });
    }

    public CalendarDay getCalendarDay() {
        return calendarDay;
    }

    public boolean isGreen() {
        return this.isGreen;
    }

    public void setCellClickable(boolean clickable) {
        isClickable = clickable;

        setBackgroundDrawable(createBorderDrawable());
    }

    public void toggleBackgroundColor() {
        isGreen = !isGreen;

        setBackgroundDrawable(createBorderDrawable());
    }

    private GradientDrawable createBorderDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);

        if (isClickable) {
            drawable.setColor(isGreen ? Color.LTGRAY : ResourcesCompat.getColor(getResources(), R.color.signature, null)); // isGreen이 true일 때 배경색을 흰색으로 설정합니다.
            drawable.setStroke(1, Color.GRAY);
        }
        return drawable;
    }
    public String getTime() {
        return time;
    }
}
