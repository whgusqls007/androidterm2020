package com.example.androidterm2020.Services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;


import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.androidterm2020.Receivers.Alarm_Receiver;
import com.example.androidterm2020.RoomDB.Alarm;
import com.example.androidterm2020.RoomDB.AlarmViewModel;
import com.example.androidterm2020.RoomDB.RoomDatabase;
import com.example.androidterm2020.RoomDB.RoomDatabaseAccessor;
import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BootService extends Service {

    List<Alarm> alarmList;
    List<Schedule> scheduleList;
    RoomDatabase roomDatabase;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 최초의 1회 호출
        roomDatabase = RoomDatabaseAccessor.getInstance(getApplicationContext());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // 주 작업. 서비스의 기능.

        deleteSchedules(); // 본이 껏다 켜진경우에만 이 서비스가 수행되므로 문제없다.
        updateIndex();
        restartAlarmList(getApplicationContext());


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 종료할 때
    }

    private void restartAlarmList(Context context) {
        // alarmList에 있는 값을 다시 Alarm으로 등록한다.
        for(Alarm alarm: alarmList) {
            restartAlarm(alarm, context);
        }
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
                result += "-";
            }
        }
        result = (new StringBuffer(result)).reverse().toString(); //2020/06/23 11:22

        return result;
    }

    private GregorianCalendar createCalendar(int scheduleId) {
        int[] datetimeData = getDatetimeData(scheduleId);

        int start_hour = datetimeData[3];
        int start_min = datetimeData[4];
        int start_year = datetimeData[0];
        int start_month = datetimeData[1];
        int start_day = datetimeData[2];

        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(start_year, start_month - 1, start_day+1, start_hour-2, start_min, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(GregorianCalendar.YEAR, 1);
            calendar.add(GregorianCalendar.DATE, -1);
        }
        calendar.add(GregorianCalendar.DATE, -1);

        return calendar;
    }

    private int[] getDatetimeData(int scheduleId) {
        long scheduleStrTimeLong = 0;
        try {
            scheduleStrTimeLong = new ScheduleViewModel.getScheduleByIdAsyncTask(roomDatabase.scheduleDao()).execute(scheduleId).get().getStrDate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } // 202007031510
        String datetime = getDateTime(scheduleStrTimeLong); // 2020-06-23 11:22
                                                            // 0123456789012345

        int[] datetimeData = new int[5];
        datetimeData[0] = Integer.parseInt(datetime.substring(0,4)); // year
        datetimeData[1] = Integer.parseInt(datetime.substring(5,7)); // month
        datetimeData[2] = Integer.parseInt(datetime.substring(8,10)); // day
        datetimeData[3] = Integer.parseInt(datetime.substring(11,13)); // hour
        datetimeData[4] = Integer.parseInt(datetime.substring(14,16)); // minute

        return datetimeData;
    }

    private void restartAlarm(Alarm alarm, Context context) {
        Calendar calendar = createCalendar(alarm.getScheduleId());
        int requestCode = alarm.getRequestId();
        int period = -1;
        try {
            int sid = new AlarmViewModel.getScheduleIdByAlarmIdAsyncTask(roomDatabase.alarmDao()).execute(requestCode).get();
            period = new ScheduleViewModel.getScheduleByIdAsyncTask(roomDatabase.scheduleDao()).execute(sid).get().getPeriod();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // 알람을 등록하는 함수.
        diaryNotification(calendar, requestCode, period, context);
    }

    @SuppressLint("ShortAlarm")
    void diaryNotification(Calendar calendar, int alarm_requestCode, int period, Context context) {
        Intent alarmIntent = new Intent(context, Alarm_Receiver.class);
        alarmIntent.putExtra("aid", alarm_requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm_requestCode, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences preference = getPreferences(context);
        SharedPreferences.Editor editor = preference.edit();
        SharedPreferences getprefrence = getPreferences(context);
        while(true) {
            int checkcode = getprefrence.getInt(Integer.toString(alarm_requestCode), -1);
            if(checkcode == -1){
                break;
            }else{
                alarm_requestCode = alarm_requestCode + 1;
            }
        }
        editor.putInt(Integer.toString(alarm_requestCode), alarm_requestCode);
        editor.apply();

        if (period == 0) { // 반복없음.
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        } else if (period == 1) { // 매일
            long INTERVAL = (long)1000 * 60 * 60 * 24;
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                }
            }
        } else if (period == 2) { // 격일
            long INTERVAL = (long)1000 * 60 * 60 * 24 * 2;
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                }
            }

        } else if (period == 3) { // 1주
            long INTERVAL = (long)1000 * 60 * 60 * 24 * 7;
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                }
            }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
        }
    }
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
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
