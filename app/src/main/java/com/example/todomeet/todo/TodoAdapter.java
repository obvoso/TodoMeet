package com.example.todomeet.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todomeet.R;
import com.example.todomeet.model.MonthlySchedule;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<MonthlySchedule> todos;

    public TodoAdapter(List<MonthlySchedule> todos) {
        this.todos = todos;
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

        TodoViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
            date = view.findViewById(R.id.date);
            startTime = view.findViewById(R.id.startTime);
            endTime = view.findViewById(R.id.endTime);
            content = view.findViewById(R.id.content);
        }
    }
}

