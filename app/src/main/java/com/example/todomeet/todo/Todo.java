package com.example.todomeet.todo;

public class Todo {
    private boolean done;
    private String date;
    private String time;
    private String content;

    public Todo(boolean done, String date, String time, String content) {
        this.done = done;
        this.date = date;
        this.time = time;
        this.content = content;
    }

    public boolean isDone() {
        return done;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }
}
