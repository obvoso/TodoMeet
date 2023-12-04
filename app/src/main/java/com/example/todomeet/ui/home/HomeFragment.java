package com.example.todomeet.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todomeet.R;
import com.example.todomeet.api.ApiService;
import com.example.todomeet.api.NetworkClient;
import com.example.todomeet.databinding.FragmentHomeBinding;
import com.example.todomeet.model.MonthlySchedule;
import com.example.todomeet.schedule.EventDecorator;
import com.example.todomeet.schedule.ScheduleActivity;
import com.example.todomeet.todo.TodoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FloatingActionButton floatingActionButton;
    List<MonthlySchedule> dates = new ArrayList<>();
    Calendar today = Calendar.getInstance();
    int year = today.get(Calendar.YEAR);
    int month = today.get(Calendar.MONTH) + 1;
    MaterialCalendarView calendarView;

    private List<MonthlySchedule> getSchedulesForDate(CalendarDay date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(date.getDate());

        List<MonthlySchedule> schedules = new ArrayList<>();
        for (MonthlySchedule schedule : dates) {
            if (schedule.getDay().equals(dateString)) {
                schedules.add(schedule);
            }
        }
        return schedules;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        calendarView = binding.calendarView;
        calendarView.setSelectedDate(CalendarDay.today());
        setOnMonthChangedListener();

        FloatingActionButton floatingActionButton = binding.floatingActionButton;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(intent);

            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.api_server))
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getOkHttpClient(getContext()))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<MonthlySchedule>> call = apiService.getMonthlySchedule(year, month);
        call.enqueue(new Callback<List<MonthlySchedule>>() {
            @Override
            public void onResponse(Call<List<MonthlySchedule>> call, Response<List<MonthlySchedule>> response) {
                if (response.isSuccessful()) {
                    dates = response.body();
                    setCalender();


                    CalendarDay today = CalendarDay.today();
                    calendarView.setDateSelected(today, true);


                    List<MonthlySchedule> schedules = getSchedulesForDate(today);


                    RecyclerView recyclerView = binding.recyclerView;
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(new TodoAdapter(dates, schedules, getContext(), calendarView, HomeFragment.this));
                }
            }

            @Override
            public void onFailure(Call<List<MonthlySchedule>> call, Throwable t) {
                t.printStackTrace();
            }
        });



        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                List<MonthlySchedule> schedules = getSchedulesForDate(date);

                RecyclerView recyclerView = binding.recyclerView;
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new TodoAdapter(dates, schedules, getContext(), calendarView, HomeFragment.this));
            }
        });
        return root;
    }

    public void setOnMonthChangedListener() {
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                int year = date.getYear();
                int month = date.getMonth() + 1;

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.api_server))
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(NetworkClient.getOkHttpClient(getContext()))
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);
                Call<List<MonthlySchedule>> call = apiService.getMonthlySchedule(year, month);
                call.enqueue(new Callback<List<MonthlySchedule>>() {
                    @Override
                    public void onResponse(Call<List<MonthlySchedule>> call, Response<List<MonthlySchedule>> response) {
                        if (response.isSuccessful()) {
                            dates = response.body();
                            setCalender();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MonthlySchedule>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void setCalender() {
        List<CalendarDay> calendarDays = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (MonthlySchedule dateData : dates) {
            System.out.println(dateData.toString());
            try {
                Date date = sdf.parse(dateData.getDay());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                calendarDays.add(CalendarDay.from(year, month, day));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        calendarView.addDecorator(new EventDecorator(Color.BLUE, calendarDays));
    };
}