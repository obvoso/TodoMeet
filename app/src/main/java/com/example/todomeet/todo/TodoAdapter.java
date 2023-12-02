package com.example.todomeet.todo;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todomeet.MainActivity;
import com.example.todomeet.R;
import com.example.todomeet.api.ApiService;
import com.example.todomeet.api.NetworkClient;
import com.example.todomeet.model.MonthlySchedule;
import com.example.todomeet.schedule.EditScheduleActivity;
import com.example.todomeet.ui.home.HomeFragment;
import com.kakao.sdk.common.util.KakaoCustomTabsClient;
import com.kakao.sdk.share.ShareClient;
import com.kakao.sdk.share.WebSharerClient;
import com.kakao.sdk.template.model.Content;
import com.kakao.sdk.template.model.FeedTemplate;
import com.kakao.sdk.template.model.ItemContent;
import com.kakao.sdk.template.model.ItemInfo;
import com.kakao.sdk.template.model.Link;
import com.kakao.sdk.template.model.Social;
import com.kakao.sdk.user.UserApiClient;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    public List<MonthlySchedule> getSameIdObjects(int targetId) {
        List<MonthlySchedule> result = new ArrayList<>();

        for (MonthlySchedule schedule : dates) {
            if (schedule.getProjectId() == targetId) {
                result.add(schedule);
            }
        }

        return result;
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

                                Intent intent = new Intent(context, EditScheduleActivity.class);
                                intent.putExtra("dates", (Serializable) getSameIdObjects(todo.getProjectId()));
                                context.startActivity(intent);
                                break;
                            case R.id.action_delete:
                                deleteSchedule(todo);
                                break;
                            case R.id.action_share:
                                    FeedTemplate feedTemplate = new FeedTemplate(
                                            new Content("공유된 일정을 확인해보세요.",
                                                    "https://d34u8crftukxnk.cloudfront.net/slackpress/prod/sites/6/share_with_your_team1.ko-KR.jpg",
                                                    new Link("https://developers.kakao.com",
                                                            "https://developers.kakao.com"),
                                                    "TodoMeet"
                                            ),
                                            new ItemContent("TodoMeet",
                                                    "https://d34u8crftukxnk.cloudfront.net/slackpress/prod/sites/6/share_with_your_team1.ko-KR.jpg",
                                                    todo.getEventName(),
                                                    "https://d34u8crftukxnk.cloudfront.net/slackpress/prod/sites/6/share_with_your_team1.ko-KR.jpg",
                                                    todo.getMemo(),
                                                    Arrays.asList(new ItemInfo("일정", todo.getDay()),
                                                            new ItemInfo("일정 시작", todo.getStartTime()),
                                                            new ItemInfo("일정 종료", todo.getEndTime()))
                                            ),
                                            new Social(286, 45, 845),
                                            Arrays.asList(new com.kakao.sdk.template.model.Button("웹으로 보기",
                                                    new Link("https://developers.kakao.com",
                                                            "https://developers.kakao.com")))
                                    );

                                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(context)) {
                                    String TAG = "kakaoLink()";

                                    ShareClient.getInstance().shareDefault(context, feedTemplate, null, (linkResult, error) -> {
                                        if (error != null) {
                                            Log.e("TAG", "카카오링크 보내기 실패", error);
                                        } else if (linkResult != null) {
                                            Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}");
                                            context.startActivity(linkResult.getIntent());

                                            Log.w("TAG", "Warning Msg: " + linkResult.getWarningMsg());
                                            Log.w("TAG", "Argument Msg: " + linkResult.getArgumentMsg());
                                        }
                                        return null;
                                    });
                                } else {
                                    String TAG = "webKakaoLink()";

                                    Uri sharerUrl = WebSharerClient.getInstance().makeDefaultUrl(feedTemplate);

                                    try {
                                        KakaoCustomTabsClient.INSTANCE.openWithDefault(context, sharerUrl);
                                    } catch (UnsupportedOperationException e) {
                                    }
                                    try {
                                        KakaoCustomTabsClient.INSTANCE.open(context, sharerUrl);
                                    } catch (ActivityNotFoundException e) {
                                    }
                                }
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
                    Toast.makeText(context,
                            "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
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
                    if (schedule.isCheck())
                        Toast.makeText(context, "일정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "일정 완료가 취소되었습니다.", Toast.LENGTH_SHORT).show();
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

