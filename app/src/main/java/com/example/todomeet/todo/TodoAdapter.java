package com.example.todomeet.todo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todomeet.R;
import com.example.todomeet.api.ApiService;
import com.example.todomeet.api.NetworkClient;
import com.example.todomeet.model.MonthlySchedule;
import com.example.todomeet.ui.home.HomeFragment;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<MonthlySchedule> todos;
    private Context context;
    private List<MonthlySchedule> dates;
    private MaterialCalendarView calendarView;
    private HomeFragment homeFragment;


    public TodoAdapter(List<MonthlySchedule> dates,
                       List<MonthlySchedule> todos,
                       Context context,
                       MaterialCalendarView calendarView,
                       HomeFragment homeFragment) {
        this.dates = dates;
        this.todos = todos;
        this.context = context;
        this.calendarView = calendarView;
        this.homeFragment = homeFragment;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        MonthlySchedule todo = todos.get(position);
        holder.checkBox.setChecked(todo.isCheck());
        holder.date.setText(todo.getDay());
        holder.startTime.setText(todo.getStartTime());
        holder.endTime.setText(todo.getEndTime());
        holder.content.setText(todo.getEventName());


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.checkBox.isChecked();
                todo.setCheck(isChecked);

                updateCheckStatusOnServer(todo);
            }
        });

        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.todo_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                break;
                            case R.id.action_delete:
                                deleteSchedule(todo);
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void deleteSchedule(MonthlySchedule schedule) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.api_server))
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getOkHttpClient(context))
                .build();

        ApiService apiInterface = retrofit.create(ApiService.class);
        Call<Void> call = apiInterface.deleteSchedule(schedule.getProjectId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println(response);
                if (response.isSuccessful()) {
                    Log.d("TodoAdapter", "delete todo successfully.");
                    todos.remove(schedule);
                    dates.remove(schedule);
                    calendarView.removeDecorators();
                    homeFragment.setCalender();
                    notifyDataSetChanged();

                } else {
                    Log.e("TodoAdapter", "Failed to delete todo: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TodoAdapter", "Failed to delete todo.", t);
            }
        });
    }


    private void updateCheckStatusOnServer(MonthlySchedule schedule) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.api_server))
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetworkClient.getOkHttpClient(context))
                .build();

        ApiService apiInterface = retrofit.create(ApiService.class);
        Call<Void> call = apiInterface.todoChecked(schedule.getProjectId(), schedule.getDay(), schedule.isCheck());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println(response);
                if (response.isSuccessful()) {
                    Log.d("TodoAdapter", "Check status updated successfully.");
                } else {
                    Log.e("TodoAdapter", "Failed to update check status: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TodoAdapter", "Failed to update check status.", t);
            }
        });
    }


    @Override
    public int getItemCount() {
        return todos.size();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView date;
        TextView startTime;
        TextView endTime;
        TextView content;
        ImageView moreButton;

        TodoViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.todoCheckBox);
            date = view.findViewById(R.id.date);
            startTime = view.findViewById(R.id.startTime);
            endTime = view.findViewById(R.id.endTime);
            content = view.findViewById(R.id.content);
            moreButton = view.findViewById(R.id.moreButton);
        }
    }
}

