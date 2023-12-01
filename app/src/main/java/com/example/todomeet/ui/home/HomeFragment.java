package com.example.todomeet.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MaterialCalendarView calendarView = binding.calendarView;
        calendarView.setSelectedDate(CalendarDay.today());

        FloatingActionButton floatingActionButton = binding.floatingActionButton;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(intent);

            }
        });


        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1;

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
                }
            }

            @Override
            public void onFailure(Call<List<MonthlySchedule>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {

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

            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                List<MonthlySchedule> schedules = getSchedulesForDate(date);

                RecyclerView recyclerView = binding.recyclerView;
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new TodoAdapter(schedules, getContext()));
            }
        });
        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}