package com.example.androidterm2020.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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
        Log.d("RefreshDBService : ", "onCreate() 호출됨.");
        //Toast.makeText(getApplicationContext(), "RFDB서비스 실행됨.", Toast.LENGTH_SHORT).show();
        // 최초의 1회 호출
        roomDatabase = RoomDatabaseAccessor.getInstance(getApplicationContext());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // 매일매일 DB 업데이트
        // 주 작업. 서비스의 기능.
        deleteSchedules(); // 지난거를 지움.
        updateIndex(); // 달성도를 위한 index를 1위치 증가시킴.
        Log.d("RefreshDBService : ", "onStartCommand 호출됨.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RefreshDBService : ", "onDestroy 호출됨.");
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
            index = getRefreshedIndex(schedule);
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

    private long getDaysFromTo(Calendar from, Calendar to) { // 두 날짜의 일수의 차이를 얻는 함수. 차이가 적을수록 정확한 값을 얻을 수 있다.
        long days =(to.getTimeInMillis() -from.getTimeInMillis())/(1000*60*60*24); // 같은 날 같은 시간으로 등록하면 안됨...
        return days;
    }

    private int getRefreshedIndex(Schedule targetSchedule) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        long date = (getLongDate(simpleDateFormat.format(calendar.getTime()))/10000) * 10000; // 지금시간 일까지. 그 이하는 치움.

        int result = targetSchedule.getIndex();
        int count = 0;
        // 현재시간이 필요함. 일 이하의 시간은 다 초기화 함.
        count = (int)getDaysFromTo(getCalendarDate(targetSchedule.getStrDate()), getCalendarDate(date)); // 값을 얻음. 시작일에서 오늘까지.
        if(count > targetSchedule.getIndex()) { // 인덱스가 오늘을 가르키는지 확인하자. 5/6 시작 index 3-> 5/9일의 index이다. 오늘은 5/11 -> count == 5
            result += (count - targetSchedule.getIndex());
        }

        return result;
    }

    private void deleteSchedules() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        long date = (getLongDate(simpleDateFormat.format(calendar.getTime()))/10000) * 10000;

        setDeleteScheduleList(date);

        for(Schedule schedule: scheduleList) {
            try {
                new AlarmViewModel.deleteAlarmByScheduleIdAsyncTask(roomDatabase.alarmDao()).execute(schedule.getSid()).get(); // 알람이 등록된 상태에 있다면 등록을 취소하는 기능도 추가해야함.
                new ScheduleViewModel.delScheduleByIdAsyncTask(roomDatabase.scheduleDao()).execute(schedule.getSid()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void setDeleteScheduleList(long date) {
        try {
            scheduleList = new ScheduleViewModel.getFromToEndDateScheduleAsyncTask(roomDatabase.scheduleDao()).execute(RefreshDBService.START_DATETIME, date).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private Calendar getCalendarDate(long date) { // Calendar로 일수를 계산하는데 사용됨. day이하의 단위는 다 0으로 초기화.
        String dateTimeString = getDateTime(date);
        String[] dateValues = dateTimeString.split(" ")[0].split("/"); // 2020/06/23 -> 2020, 06, 23
        //String[] timeValues = dateTimeString.split(" ")[1].split(":"); // 13:40 -> 13, 40

        Calendar result = Calendar.getInstance();
        result.set(Calendar.YEAR, Integer.parseInt(dateValues[0]));
        result.set(Calendar.MONTH, Integer.parseInt(dateValues[1])+1);
        result.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateValues[2]));
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);

        return result;
    }

    private long getLongDate(String date) {
        date = date.replaceAll("[ :-]", "");
        return Long.parseLong(date);
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
        for(int i=0; i<8; ++i) {
            result += (strDate%10); // 2211
            strDate /= 10;
            if(i%2 == 1 && i < 5) {
                result += "/";
            }
        }
        result = (new StringBuffer(result)).reverse().toString(); //20/06/23 11:22

        return result;
    }
}
