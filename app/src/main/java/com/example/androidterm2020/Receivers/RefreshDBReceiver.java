package com.example.androidterm2020.Receivers;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModel;

import com.example.androidterm2020.R;
import com.example.androidterm2020.RoomDB.Alarm;
import com.example.androidterm2020.RoomDB.AlarmViewModel;
import com.example.androidterm2020.RoomDB.RoomDatabase;
import com.example.androidterm2020.RoomDB.RoomDatabaseAccessor;
import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;
import com.example.androidterm2020.Services.RefreshDBService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RefreshDBReceiver extends BroadcastReceiver {
    List<Alarm> alarmList;
    List<Schedule> scheduleList;
    RoomDatabase roomDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent RefreshDBServiceIntent = new Intent(context, RefreshDBService.class);
        roomDatabase = RoomDatabaseAccessor.getInstance(context);
        context.startService(RefreshDBServiceIntent);
    }
}
