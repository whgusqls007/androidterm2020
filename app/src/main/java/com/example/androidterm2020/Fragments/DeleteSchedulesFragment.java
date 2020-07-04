package com.example.androidterm2020.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidterm2020.R;
import com.example.androidterm2020.Receivers.Alarm_Receiver;
import com.example.androidterm2020.RoomDB.AlarmViewModel;
import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;
import com.example.androidterm2020.ShowDetail;

import java.util.ArrayList;
import java.util.List;

public class DeleteSchedulesFragment extends Fragment {
    LinearLayout baseLayout = null;
    ScrollView scrollList;
    LinearLayout targetLayout;

    private ScheduleViewModel scheduleViewModel;
    private AlarmViewModel alarmViewModel;
    List<Schedule> mSchedules;
    ArrayList<CheckBox> checkBoxList;

    int checkedNum;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.delete_schedules_fragment, container, false); // 수정
        checkBoxList = new ArrayList<CheckBox>();
        checkedNum = 0;
        baseLayout = rootView.findViewById(R.id.base);
        scrollList = new ScrollView(getContext());
        targetLayout = new LinearLayout(getContext());
        targetLayout.setOrientation(LinearLayout.VERTICAL);

        String date = getArguments().getString("date");
        setViewModels(date); // 번들로 가져오자.
        mSchedules = scheduleViewModel.getDateSchedules(getLongDate(date));
        setScheduleList(getActivity());

        scrollList.addView(targetLayout);
        baseLayout.addView(scrollList);

        if(mSchedules.size() == 0) {
            ((ShowDetail)getActivity()).setFloatingActionButtonImgPlus();
        }

        int targetId = -1;
        targetId = getArguments().getInt("sid") > 0 ? getArguments().getInt("sid") : -1;


        if(targetId != -1) {
            for(CheckBox checkBox: checkBoxList) {
                if(checkBox.getId() == targetId) {
                    checkBox.setChecked(true);
                    break;
                }
            }
        }

        return rootView;
    }

    public int getScheduleNum() {
        return mSchedules.size();
    }

    private void setScheduleList(Context context) {
        int size = mSchedules.size();
        insertTargetLayout(context);
        for(Schedule schedule: mSchedules) { // 여기서는 checkBox 지워라.
            insertSchedule(context, schedule);
        }
    }

    private void insertTargetLayout(Context context) {
        LinearLayout linearLayout = createLinearLayout(context); // 단위 레이아웃 생성.
        linearLayout.setBackgroundColor(Color.GRAY);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 33, getResources().getDisplayMetrics());
        int endDayWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 70, getResources().getDisplayMetrics());
        int titleWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 270, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics());
        TextView cbSpace = new TextView(context);
        cbSpace.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
        cbSpace.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0);
        cbSpace.setBackgroundResource(R.drawable.border);

        TextView endTimeView = createTitleTextView(context, "시작시간");
        endTimeView.setLayoutParams(new LinearLayout.LayoutParams(endDayWidth, height));
        endTimeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        TextView titleTextView = createTitleTextView(context, "일정 제목"); // 여기짜지 집어넣을 변수선언.
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(titleWidth, height));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        linearLayout.addView(cbSpace);
        linearLayout.addView(endTimeView);
        linearLayout.addView(titleTextView); // View를 단위 레이아웃에 넣는다.

        targetLayout.addView(linearLayout); // 모든 설정이 끝난 레이아웃을 스크롤뷰 내부의 레이아웃에 넣는다.
    }



    private void insertSchedule(Context context, Schedule schedule) {
        LinearLayout linearLayout = createLinearLayout(context); // 단위 레이아웃 생성.

        CheckBox checkBox = setCheckBox(context);
        TextView timeTextView = createTimeTextView(context, schedule.getStrDate());
        timeTextView.setTextColor(Color.BLACK);
        TextView titleTextView = createTitleTextView(context, schedule.getTitle()); // 여기짜지 집어넣을 변수선언.
        titleTextView.setTextColor(Color.BLACK);
        checkBox.setId(schedule.getSid()); // checkBox가 있을때.

        checkBoxList.add(checkBox);

        linearLayout.addView(checkBox);
        linearLayout.addView(timeTextView);
        linearLayout.addView(titleTextView); // View를 단위 레이아웃에 넣는다.

        targetLayout.addView(linearLayout); // 모든 설정이 끝난 레이아웃을 스크롤뷰 내부의 레이아웃에 넣는다.

    }

    private CheckBox setCheckBox(Context context) {
        final CheckBox checkBox = new CheckBox(context);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked() == true) {
                    checkedNum += 1;
                }
                else {
                    checkedNum -= 1;
                }
            }
        });
        return checkBox;
    }

    private void setViewModels(String date) {
        scheduleViewModel = new ViewModelProvider(getActivity()).get(ScheduleViewModel.class);
        alarmViewModel = new ViewModelProvider(getActivity()).get(AlarmViewModel.class);
    }

    private LinearLayout createLinearLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        return linearLayout;
    }

    private TextView createTimeTextView(Context context, long strDate) {
        TextView textView = new TextView(context);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 71, getResources().getDisplayMetrics());
        textView.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setText(getTime(strDate));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setBackgroundResource(R.drawable.border);
        return textView;
    }

    private String getTime(long strDate) { // 20200623 1122 에서 1122만 가져온다.
        String result = "";

        for(int i=0; i<4; ++i) {
            result += (strDate%10); // 2211
            strDate /= 10;
            if(i == 1) {
                result += ":";
            }
        }
        result = (new StringBuffer(result)).reverse().toString(); // 11:22

        return result;
    }

    private TextView createTitleTextView(Context context, String title) {
        TextView textView = new TextView(context);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 269, getResources().getDisplayMetrics());
        textView.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setText(title);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setBackgroundResource(R.drawable.border);
        return textView;
    }

    private long getLongDate(String date) {
        date = date.replaceAll("[ :-]", "");
        return Long.parseLong(date);
    }

    public void deleteSchedules() {
        for(CheckBox cb : checkBoxList) {
            if(cb.isChecked() == true) {
                AlarmManager myAlarm = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
                int requestCode = alarmViewModel.getAlarmIdByScheduleId(cb.getId());
                Intent intent = new Intent(getActivity(), Alarm_Receiver.class);
                PendingIntent sender = PendingIntent.getBroadcast(getActivity(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (sender != null) {
                    myAlarm.cancel(sender);
                    sender.cancel();
                }
                alarmViewModel.deleteAlarmByScheduleId(cb.getId());
                scheduleViewModel.deleteScheduleById(cb.getId());
            }
        }
    }
}
