package com.example.todomeet.schedule;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.MotionEvent;

import androidx.core.content.res.ResourcesCompat;

import com.example.todomeet.R;

public class SelectableCell extends androidx.appcompat.widget.AppCompatTextView {
    private boolean isGreen = false;
    private boolean isClickable;

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

    public void setCellClickable(boolean clickable) {
        isClickable = clickable;
    }

    private void toggleBackgroundColor() {
        isGreen = !isGreen;

        setBackgroundDrawable(createBorderDrawable());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isClickable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    toggleBackgroundColor();
                    break;
            }
        }
        return true;
    }

    private GradientDrawable createBorderDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(isGreen ? ResourcesCompat.getColor(getResources(), R.color.signature, null) : Color.WHITE);
        drawable.setStroke(1, Color.GRAY);

        return drawable;
    }
}
