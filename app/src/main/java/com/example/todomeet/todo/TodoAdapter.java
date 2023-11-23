package com.example.todomeet.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todomeet.R;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<Todo> todos;

    public TodoAdapter(List<Todo> todos) {
        this.todos = todos;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.checkBox.setChecked(todo.isDone());
        holder.date.setText(todo.getDate());
        holder.time.setText(todo.getTime());
        holder.content.setText(todo.getContent());
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView date;
        TextView time;
        TextView content;

        TodoViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);
            content = view.findViewById(R.id.content);
        }
    }
}

