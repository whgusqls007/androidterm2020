package com.example.androidterm2020;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;

import java.util.List;

public class ScheduleAllModificationFragment extends Fragment {
    LinearLayout baseLayout = null;
    ScrollView scrollList;
    LinearLayout targetLayout;

    private ScheduleViewModel scheduleViewModel;
    List<Schedule> mSchedules;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.schedule_modification_fragment, container, false); // 수정
        baseLayout = rootView.findViewById(R.id.base);
        scrollList = new ScrollView(getContext());
        targetLayout = new LinearLayout(getContext());
        targetLayout.setOrientation(LinearLayout.VERTICAL);

        setScheduleViewModel(); // 번들로 가져오자.
        mSchedules = scheduleViewModel.getAllSchedules();
        setScheduleList(getActivity());

        scrollList.addView(targetLayout);
        baseLayout.addView(scrollList);

        return rootView;
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

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 105, getResources().getDisplayMetrics());
        int endDayWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 105, getResources().getDisplayMetrics());
        int titleWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 150, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics());
        TextView strTimeView = createTitleTextView(context, "시작시간");
        strTimeView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        strTimeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        TextView endTimeView = createTitleTextView(context, "종료일시");
        endTimeView.setLayoutParams(new LinearLayout.LayoutParams(endDayWidth, height));
        endTimeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        TextView titleTextView = createTitleTextView(context, "일정 제목"); // 여기짜지 집어넣을 변수선언.
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(titleWidth, height));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        linearLayout.addView(strTimeView);
        linearLayout.addView(endTimeView);
        linearLayout.addView(titleTextView); // View를 단위 레이아웃에 넣는다.

        targetLayout.addView(linearLayout); // 모든 설정이 끝난 레이아웃을 스크롤뷰 내부의 레이아웃에 넣는다.
    }


    private void insertSchedule(final Context context, Schedule schedule) {
        final LinearLayout linearLayout = createLinearLayout(context); // 단위 레이아웃 생성.

        TextView strTimeTextView = createTimeTextView(context, schedule.getStrDate());
        strTimeTextView.setTextColor(Color.BLACK);
        TextView endTimeTextView = createEndTimeTextView(context, schedule.getEndDate());
        endTimeTextView.setTextColor(Color.BLACK);
        TextView titleTextView = createTitleTextView(context, schedule.getTitle()); // 여기짜지 집어넣을 변수선언.
        titleTextView.setTextColor(Color.BLACK);
        linearLayout.setId(schedule.getSid()); // checkBox가 없을때.

        linearLayout.addView(strTimeTextView);
        linearLayout.addView(endTimeTextView);
        linearLayout.addView(titleTextView); // View를 단위 레이아웃에 넣는다.
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "수정창으로 갑니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ScheduleUpdateActivity.class);
                intent.putExtra("sid", linearLayout.getId());
                startActivityForResult(intent, 111);
            }
        });

        targetLayout.addView(linearLayout); // 모든 설정이 끝난 레이아웃을 스크롤뷰 내부의 레이아웃에 넣는다.
    }

    private void setScheduleViewModel() {
        scheduleViewModel = new ViewModelProvider(getActivity()).get(ScheduleViewModel.class);
    }

    private LinearLayout createLinearLayout(final Context context) {
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        return linearLayout;
    }

    private TextView createTimeTextView(Context context, long strDate) {
        TextView textView = new TextView(context);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 105, getResources().getDisplayMetrics());
        textView.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setText(getDateTime(strDate));
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.border);
        return textView;
    }

    private TextView createEndTimeTextView(Context context, long endDate) {
        TextView textView = new TextView(context);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 105, getResources().getDisplayMetrics());
        textView.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setText(getDateTime(endDate));
        textView.setGravity(Gravity.CENTER);
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
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 150, getResources().getDisplayMetrics());
        textView.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        textView.setBackgroundResource(R.drawable.border);
        return textView;
    }

    private long getLongDate(String date) {
        date = date.replaceAll("[ :-]", "");
        return Long.parseLong(date);
    }
}

