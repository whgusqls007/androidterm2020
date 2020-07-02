package com.example.androidterm2020;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidterm2020.Fragments.AchieveAfterFragment;
import com.example.androidterm2020.Fragments.AchieveBeforeFragment;
import com.example.androidterm2020.Fragments.FragmentCallback;
import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;


public class Achieve extends AppCompatActivity implements FragmentCallback {
    AchieveBeforeFragment achieveBeforeFragment;
    AchieveAfterFragment achieveAfterFragment;
    ScheduleViewModel scheduleViewModel;
    Schedule schedule;

    int SID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achieve_main);
        SID = getIntent().getIntExtra("sid", 0);

        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        achieveBeforeFragment = new AchieveBeforeFragment();
        achieveAfterFragment = new AchieveAfterFragment();

        setFragment();
    }

    @Override
    public void onFragmentSelected(int position, Bundle bundle) {
        Fragment curFragment = null;
        String tag = null;
        if(position == 0) {
            curFragment = achieveBeforeFragment;
            tag = "before";
        }
        else if(position == 1) {
            curFragment = achieveAfterFragment;
            tag = "after";
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFragment.getClass(), bundle, tag).commit();
    }

    private double getAchieveValue(int dateNum, int index, String data) {
        int count = 0;
        if (dateNum > index) { // 종료일이 지나지 않음
            for(int i=0; i<dateNum; ++i) {
                if(data.charAt(i) == '1') {
                    count++;
                }
            }
        }
        double result = (double)count/(double)(dateNum) * 100;
        return result;
    }

    private int getPosition(int index, String data) {
        int position = 0;
        if(data.charAt(index) == '1') {
            position = 1;
        }

        return position;
    }

    public void updateSchedule() {
        char[] newData = schedule.getAchievementData().toCharArray();
        int index = schedule.getIndex();
        int dateNum = schedule.getDateNum();

        if(dateNum > index) { // 작업함.
            newData[index] = '1';
        }
        schedule.setAchievementData(new String(newData));

        scheduleViewModel.updateSchedule(schedule);
    }

    public void setFragment() {
        schedule = scheduleViewModel.getScheduleById(SID);
        double achievementValue = getAchieveValue(schedule.getDateNum(), schedule.getIndex(), schedule.getAchievementData());

        Bundle bundle = new Bundle();
        bundle.putDouble("achievementValue", achievementValue);

        int position = getPosition(schedule.getIndex(), schedule.getAchievementData());
        onFragmentSelected(position, bundle);
    }
}
