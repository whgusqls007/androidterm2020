package com.example.androidterm2020.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.androidterm2020.RoomDB.Alarm;
import com.example.androidterm2020.RoomDB.AlarmViewModel;
import com.example.androidterm2020.RoomDB.RoomDatabase;
import com.example.androidterm2020.RoomDB.RoomDatabaseAccessor;
import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RefreshDBService extends Service {
    List<Schedule> scheduleList;
    RoomDatabase roomDatabase;
    public static final long START_DATETIME = 1010000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        android.os.Debug.waitForDebugger();
        // 최초의 1회 호출
        roomDatabase = RoomDatabaseAccessor.getInstance(getApplicationContext());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // 매일매일 DB 업데이트
        // 주 작업. 서비스의 기능.
        deleteSchedules(); // 지난거를 지움.
        updateIndex(); // 달성도를 위한 index를 1위치 증가시킴.

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 종료할 때
    }

    private void updateIndex() {
        int index = -1;
        try {
            scheduleList = new ScheduleViewModel.getAllScheduleAsyncTask(roomDatabase.scheduleDao()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        for(Schedule schedule: scheduleList) {
            index = schedule.getIndex()+1;
            schedule.setIndex(index);
            try {
                new ScheduleViewModel.updateScheduleAsyncTask(roomDatabase.scheduleDao()).execute(schedule).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteSchedules() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        long date = (getLongDate(simpleDateFormat.format(calendar.getTime()))/10000) * 10000;
        try {
            scheduleList = new ScheduleViewModel.getFromToEndDateScheduleAsyncTask(roomDatabase.scheduleDao()).execute(RefreshDBService.START_DATETIME, date).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for(Schedule schedule: scheduleList) {
            try {
                new AlarmViewModel.deleteAlarmByScheduleIdAsyncTask(roomDatabase.alarmDao()).execute(schedule.getSid()).get();
                new ScheduleViewModel.delScheduleByIdAsyncTask(roomDatabase.scheduleDao()).execute(schedule.getSid()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private long getLongDate(String date) {
        date = date.replaceAll("[ :-]", "");
        return Long.parseLong(date);
    }
}
