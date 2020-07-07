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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.FragmentTransitionSupport;

import com.example.androidterm2020.R;
import com.example.androidterm2020.Receivers.Alarm_Receiver;
import com.example.androidterm2020.RoomDB.AlarmViewModel;
import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;
import com.example.androidterm2020.ShowAllSchedule;

import java.util.ArrayList;
import java.util.List;

public class DeleteAllSchedulesFragment extends Fragment {
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
        Bundle bundle = getArguments();
        setScheduleViewModel(); // 번들로 가져오자.
        mSchedules = scheduleViewModel.getAllSchedules();

        setScheduleList(getActivity());

        scrollList.addView(targetLayout);
        baseLayout.addView(scrollList);

        if(mSchedules.size() == 0) {
            ((ShowAllSchedule)getActivity()).setFloatingActionButtonImgPlus();
        }


        int targetId = -1;
        if(getArguments() != null) {
            targetId = getArguments().getInt("sid");
        }

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

    public void refresh() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.commit();
    }

    public int getScheduleNum() {
        return mSchedules.size();
    }

    private void setScheduleList(Context context) {
        insertTargetLayout(context);
        for(Schedule schedule: mSchedules) { // 여기서는 checkBox 지워라.
            insertSchedule(context, schedule);
        }
    }

    private void insertTargetLayout(Context context) {
        LinearLayout linearLayout = createLinearLayout(context); // 단위 레이아웃 생성.
        linearLayout.setBackgroundColor(Color.GRAY);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 33, getResources().getDisplayMetrics());
        int endDayWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 105, getResources().getDisplayMetrics());
        int titleWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 280, getResources().getDisplayMetrics());
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

    private void setScheduleViewModel() {
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
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 106, getResources().getDisplayMetrics());
        textView.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setText(getDateTime(strDate));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setBackgroundResource(R.drawable.border);
        return textView;
    }

    private String getDateTime(long strDate) { // 20200623 1122 에서 1122만 가져온다.
        String result = "";

        for(int i=0; i<4; ++i) {
            result += (strDate%10); // 2211
            strDate /= 10;
            if(i == 1) {
                result += ":";
            }
        }
        // 만약 보기 안좋으면 여기에서 뉴라인 추가
        result += " ";
        for(int i=0; i<6; ++i) {
            result += (strDate%10); // 2211
            strDate /= 10;
            if(i%2 == 1 && i != 5) {
                result += "/";
            }
        }
        result = (new StringBuffer(result)).reverse().toString(); //20/06/23 11:22

        return result;
    }

    private TextView createTitleTextView(Context context, String title) {
        TextView textView = new TextView(context);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 280, getResources().getDisplayMetrics());
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
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, DeleteAllSchedulesFragment.class, null, "delete").commit();
        }
    }
}

