package com.example.androidterm2020.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.lang.UCharacter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidterm2020.Achieve;
import com.example.androidterm2020.R;
import com.example.androidterm2020.RoomDB.RoomDatabase;
import com.example.androidterm2020.RoomDB.RoomDatabaseAccessor;
import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleDao;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AchieveListFragment extends Fragment {
    LinearLayout baseLayout = null;
    ScrollView scrollList;
    LinearLayout targetLayout;

    private ScheduleViewModel scheduleViewModel;
    List<Schedule> mSchedules;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = (View) inflater.inflate(R.layout.acheive_list_fragment, container, false);
        baseLayout = rootView.findViewById(R.id.base);
        scrollList = new ScrollView(getContext());
        targetLayout = new LinearLayout(getContext());
        targetLayout.setOrientation(LinearLayout.VERTICAL);

        String date = getArguments().getString("date");
        setScheduleViewModel(date); // 번들로 가져오자.
        mSchedules = scheduleViewModel.getDateSchedules(getLongDate(date));
        setScheduleList(getActivity());

        scrollList.addView(targetLayout);
        baseLayout.addView(scrollList);

        return rootView;
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

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 50, getResources().getDisplayMetrics());
        int titleWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 310, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics());
        TextView timeTextView = createTitleTextView(context, "시간");
        timeTextView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        TextView titleTextView = createTitleTextView(context, "일정 제목"); // 여기짜지 집어넣을 변수선언.
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(titleWidth, height));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        linearLayout.addView(timeTextView);
        linearLayout.addView(titleTextView); // View를 단위 레이아웃에 넣는다.

        targetLayout.addView(linearLayout); // 모든 설정이 끝난 레이아웃을 스크롤뷰 내부의 레이아웃에 넣는다.
    }


    private void insertSchedule(final Context context, Schedule schedule) {
        final LinearLayout linearLayout = createLinearLayout(context); // 단위 레이아웃 생성.

        TextView timeTextView = createTimeTextView(context, schedule.getStrDate());
        timeTextView.setTextColor(Color.BLACK);
        TextView titleTextView = createTitleTextView(context, schedule.getTitle()); // 여기짜지 집어넣을 변수선언.
        titleTextView.setTextColor(Color.BLACK);
        linearLayout.setId(schedule.getSid()); // checkBox가 없을때.

        linearLayout.addView(timeTextView);
        linearLayout.addView(titleTextView); // View를 단위 레이아웃에 넣는다.
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "레이아웃 클릭", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), Achieve.class);
                intent.putExtra("sid", linearLayout.getId());
                startActivityForResult(intent, 111);
            }
        });

        targetLayout.addView(linearLayout); // 모든 설정이 끝난 레이아웃을 스크롤뷰 내부의 레이아웃에 넣는다.
    }

    private void setScheduleViewModel(String date) {
        scheduleViewModel = new ViewModelProvider(getActivity()).get(ScheduleViewModel.class);
    }

    private LinearLayout createLinearLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);


        return linearLayout;
    }

    private TextView createTimeTextView(Context context, long strDate) {
        TextView textView = new TextView(context);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 50, getResources().getDisplayMetrics());
        textView.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setText(getTime(strDate));
        textView.setGravity(Gravity.CENTER);
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
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 310, getResources().getDisplayMetrics());
        textView.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(title);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        textView.setBackgroundResource(R.drawable.border);
        return textView;
    }

    private long getLongDate(String date) {
        date = date.replaceAll("[ :-]", "");
        return Long.parseLong(date);
    }

}
