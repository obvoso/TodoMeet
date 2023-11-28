package com.example.todomeet.schedule;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todomeet.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private ArrayList<CalendarDay> selectedDates = new ArrayList<>();

    private NumberPicker startTimePicker, endTimePicker;
    private RecyclerView timeTableView;
    private ArrayList<TimeTable> timeTableList;
    private TimeTableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        calendarView = findViewById(R.id.chooseDates);
        startTimePicker = findViewById(R.id.startTimePicker);
        endTimePicker = findViewById(R.id.endTimePicker);

        startTimePicker.setMinValue(0);
        startTimePicker.setMaxValue(23);
        endTimePicker.setMinValue(0);
        endTimePicker.setMaxValue(23);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    selectedDates.add(date);
                } else {
                    selectedDates.remove(date);
                }
            }
        });

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        Button createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimetable();
            }
        });
    }

    private void createTimetable() {
        GridLayout timeTable = findViewById(R.id.timeTable);

        timeTable.removeAllViews();

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

        int rowCount = endTimePicker.getValue() - startTimePicker.getValue() + 1;
        int columnCount = selectedDates.size() + 1;

        timeTable.setRowCount(rowCount);
        timeTable.setColumnCount(columnCount);

        for (int column = 1; column < columnCount; column++) {
            SelectableCell cell = new SelectableCell(this);
            SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.KOREAN);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd", Locale.KOREAN);
            String dayOfWeek = sdf.format(selectedDates.get(column - 1).getDate());
            String mmddFormat = dateFormat.format(selectedDates.get(column - 1).getDate());
            cell.setText(mmddFormat + " (" + dayOfWeek + ")");
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = width;
            params.height = height;
            params.rowSpec = GridLayout.spec(0);
            params.setGravity(Gravity.CENTER);
            params.columnSpec = GridLayout.spec(column);
            timeTable.addView(cell, params);

            cell.setCellClickable(false);
        }

        for (int row = 1; row < rowCount; row++) {
            SelectableCell hourCell = new SelectableCell(this);
            hourCell.setText((startTimePicker.getValue() + row - 1) + "시");
            GridLayout.LayoutParams hourParams = new GridLayout.LayoutParams();
            hourParams.width = width;
            hourParams.height = height;
            hourParams.rowSpec = GridLayout.spec(row);
            hourParams.columnSpec = GridLayout.spec(0);
            timeTable.addView(hourCell, hourParams);

            hourCell.setCellClickable(false);

            for (int column = 1; column < columnCount; column++) {
                SelectableCell cell = new SelectableCell(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = width;
                params.height = height;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(column);
                timeTable.addView(cell, params);
            }
        }

    }
}
