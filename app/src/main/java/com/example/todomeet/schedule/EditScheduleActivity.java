package com.example.todomeet.schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todomeet.MainActivity;
import com.example.todomeet.R;
import com.example.todomeet.api.ApiService;
import com.example.todomeet.api.NetworkClient;
import com.example.todomeet.model.MonthlySchedule;
import com.example.todomeet.model.Schedule;
import com.example.todomeet.model.TimeSlot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditScheduleActivity  extends AppCompatActivity {
    private EditText titleEditText;
    private MaterialCalendarView calendarView;
    private NumberPicker startTimePicker, endTimePicker;
    private Button createButton, cancelButton, submitButton;
    private EditText memoEditText;
    private ArrayList<CalendarDay> selectedDates = new ArrayList<>();
    private List<SelectableCell> selectedCells = new ArrayList<>();
    private List<String> formattingDates = new ArrayList<>();
    private List<TimeSlot> timeSlots = new ArrayList<>();
    List<MonthlySchedule> dates;

    private int start = 23, end = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        titleEditText = findViewById(R.id.title_editText);
        calendarView = findViewById(R.id.chooseDates);
        startTimePicker = findViewById(R.id.startTimePicker);
        endTimePicker = findViewById(R.id.endTimePicker);
        createButton = findViewById(R.id.createButton);
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);
        memoEditText = findViewById(R.id.memo_editText);

        dates = (List<MonthlySchedule>) getIntent().getSerializableExtra("dates");

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        startTimePicker.setMaxValue(23);
        endTimePicker.setMaxValue(23);

        for (MonthlySchedule item : dates) {
            Date date = convertStringToDate(item.getDay());
            calendarView.setDateSelected(date, true);
            selectedDates.add(new CalendarDay(date));
            if (start >= item.getCenvertStartTime())
                start = item.getCenvertStartTime();
            if (end < item.getCenvertEndTime())
                end = item.getCenvertEndTime();
        }

        titleEditText.setText(dates.get(0).getEventName());
        memoEditText.setText(dates.get(0).getMemo());
        startTimePicker.setValue(start);
        endTimePicker.setValue(end);

        createTimetable(true);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    if (selectedDates.size() >= 7) {
                        Toast.makeText(EditScheduleActivity.this,
                                "최대 7일까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        widget.setDateSelected(date, false);
                    }
                    selectedDates.add(date);
                } else {
                    selectedDates.remove(date);
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimetable(false);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSchedule();
                postSchedule(dates.get(0).getProjectId());
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTaskRoot()) {
                    Intent intent = new Intent(EditScheduleActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    finish();
                }
            }
        });
    }

    private Date convertStringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private  GridLayout.LayoutParams setParams(int row, int column) {
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();

        params.width = width;
        params.height = height;
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(column);
        params.setGravity(Gravity.CENTER);

        return params;
    }

    private void createTimetable(boolean isInitial) {
        GridLayout timeTable = findViewById(R.id.timeTable);

        timeTable.removeAllViews();

        int rowCount = endTimePicker.getValue() - startTimePicker.getValue() + 1;
        int columnCount = selectedDates.size() + 1;

        if(rowCount < 1) {
            Toast.makeText(EditScheduleActivity.this,
                    "시간을 올바르게 선택해주세요.", Toast.LENGTH_SHORT).show();
            return ;
        }

        timeTable.setRowCount(rowCount);
        timeTable.setColumnCount(columnCount);

        for (int column = 1; column < columnCount; column++) {
            SelectableCell cell = new SelectableCell(this);
            SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.KOREAN);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd", Locale.KOREAN);

            String dayOfWeek = sdf.format(selectedDates.get(column - 1).getDate());
            String mmddFormat = dateFormat.format(selectedDates.get(column - 1).getDate());
            cell.setText(mmddFormat + " (" + dayOfWeek + ")");
            timeTable.addView(cell, setParams(0, column));
            cell.setCellClickable(false);
        }

        for (int row = 1; row < rowCount; row++) {
            SelectableCell hourCell = new SelectableCell(this);
            hourCell.setText((startTimePicker.getValue() + row - 1) + ":00");
            timeTable.addView(hourCell, setParams(row, 0));
            hourCell.setCellClickable(false);

            for (int column = 1; column < columnCount; column++) {
                CalendarDay calendarDay = selectedDates.get(column - 1);
                Boolean isInitCell = isInitial
                        && (dates.get(column - 1).getCenvertStartTime() <= startTimePicker.getValue() + row - 1)
                        && (startTimePicker.getValue() + row - 1 < dates.get(column - 1).getCenvertEndTime());

                SelectableCell cell = new SelectableCell(this, calendarDay,
                        String.valueOf(startTimePicker.getValue() + row - 1), isInitCell);
                timeTable.addView(cell, setParams(row, column));
                cell.setCellClickable(true);

                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cell.isGreen()) {
                            selectedCells.add(cell);
                        } else {
                            selectedCells.remove(cell);
                        }
                        cell.toggleBackgroundColor();
                    }
                });
                if (isInitCell) {
                    selectedCells.add(cell);
                }
            }
        }
    }
    private void setSchedule() {

        for (CalendarDay date : selectedDates) {
            int year = date.getYear();
            int month = date.getMonth() + 1;
            int day = date.getDay();

            String formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month, day);
            formattingDates.add(formattedDate);

            String firstSelectedTime = null;
            String lastSelectedTime = null;

            for (SelectableCell cell : selectedCells) {
                if (cell.getCalendarDay().equals(date)) {
                    if (firstSelectedTime == null || cell.getTime().compareTo(firstSelectedTime) < 0) {
                        firstSelectedTime = cell.getTime();
                    }
                    if (lastSelectedTime == null || cell.getTime().compareTo(lastSelectedTime) > 0) {
                        lastSelectedTime = cell.getTime();
                    }
                }
            }
            if (firstSelectedTime != null && lastSelectedTime != null) {
                int lastTime = Integer.parseInt(lastSelectedTime) + 1;
                if (firstSelectedTime.length() == 1)
                    firstSelectedTime = "0" + firstSelectedTime;
                if (lastTime < 10)
                    lastSelectedTime = "0" + String.valueOf(lastTime) + ":00";
                else if (lastTime >= 10) {
                    lastSelectedTime = String.valueOf(lastTime) + ":00";
                }
                TimeSlot timeSlot = new TimeSlot(formattedDate, firstSelectedTime + ":00", lastSelectedTime);
                timeSlots.add(timeSlot);
            }
        }
    }


    private void postSchedule(int projectId) {
        SharedPreferences sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String userEmail = sharedPref.getString("userEmail", "");
        Schedule updateSchedule = new Schedule(userEmail,
                titleEditText.getText().toString(),
                memoEditText.getText().toString(),
                formattingDates.get(0),
                formattingDates.get(formattingDates.size() - 1),
                timeSlots );
        Log.d("edit", "edit interface" + updateSchedule.toString());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.api_server))
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getOkHttpClient(this))
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<Void> call = service.updateSchedule(projectId, updateSchedule);


        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println(response);
                Log.d("editSchedule", "a " + response);
                if (response.isSuccessful()) {
                    Toast.makeText(EditScheduleActivity.this,
                            "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditScheduleActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditScheduleActivity.this,
                        "일정 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                System.out.println("post schedule fail: " + t.getMessage());
            }
        });
    }
}