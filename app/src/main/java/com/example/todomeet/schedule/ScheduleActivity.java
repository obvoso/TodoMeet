package com.example.todomeet.schedule;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todomeet.MainActivity;
import com.example.todomeet.R;
import com.example.todomeet.api.ApiService;
import com.example.todomeet.api.NetworkClient;
import com.example.todomeet.login.LoginActivity;
import com.example.todomeet.model.Schedule;
import com.example.todomeet.model.TimeSlot;
import com.kakao.sdk.user.UserApiClient;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

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

public class ScheduleActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private ArrayList<CalendarDay> selectedDates = new ArrayList<>();
    private List<String> formattingDates = new ArrayList<>();
    private List<SelectableCell> selectedCells = new ArrayList<>();
    private List<TimeSlot> timeSlots = new ArrayList<>();

    private NumberPicker startTimePicker, endTimePicker;
    private EditText title;
    private EditText memo;

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
                    if (selectedDates.size() >= 7) {
                        Toast.makeText(ScheduleActivity.this,
                                "날짜는 최대 7일까지 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        widget.setDateSelected(date, false);
                    }
                    selectedDates.add(date);
                } else {
                    selectedDates.remove(date);
                }
            }
        });

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        Button createButton = findViewById(R.id.createButton);
        Button submitButton = findViewById(R.id.submitButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimetable();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSchedule();
                postSchedule();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTaskRoot()) {
                    Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    finish();
                }
            }
        });

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

    private void createTimetable() {
        GridLayout timeTable = findViewById(R.id.timeTable);

        timeTable.removeAllViews();

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
                SelectableCell cell = new SelectableCell(this, calendarDay,
                        String.valueOf(startTimePicker.getValue() + row - 1));
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
            }
        }
    }

    private void setSchedule() {
        title = (EditText) findViewById(R.id.title_editText);
        memo = (EditText) findViewById(R.id.memo_editText);

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


    private void postSchedule() {
            SharedPreferences sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
            String userEmail = sharedPref.getString("userEmail", "");
            Schedule schedule = new Schedule(userEmail,
                    title.getText().toString(),
                    memo.getText().toString(),
                    formattingDates.get(0),
                    formattingDates.get(formattingDates.size() - 1),
                    timeSlots );
            Log.d("user", "user interface" + schedule.toString());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.api_server))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(NetworkClient.getOkHttpClient(this))
                    .build();

            ApiService service = retrofit.create(ApiService.class);
            Call<Void> call = service.addSchedule(schedule);


            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    System.out.println(response);
                    Log.d("postSchedule", "a " + response);
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                    System.out.println("post schedule fail: " + t.getMessage());
                }
            });
        }
}
