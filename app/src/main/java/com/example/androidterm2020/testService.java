package com.example.androidterm2020;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class testService extends Service {
    public testService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 아래 추가 기능들 넣기. 서비스 하기전에 할 것.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "서비스 시작합니다.", Toast.LENGTH_SHORT).show();
        String strDate = intent.getStringExtra("strDate");
        String result = querySchedule(strDate);
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "서비스 종료합니다.", Toast.LENGTH_SHORT).show();
    }

    public String querySchedule(String selectionDate) {
        String result = "";
        try {
            Uri uri = ScheduleProvider.CONTENT_URI;

            String[] columns = DBHelper.ALL_COLUMNS;
            Cursor cursor = getContentResolver().query(uri, columns, DBHelper.SCHEDULE_START_DATE + " = " + "'" +selectionDate + "'", null, DBHelper.SCHEDULE_START_DATE + " ASC");

            int index = 0;
            while(cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(columns[0]));
                String title = cursor.getString(cursor.getColumnIndex(columns[1]));
                String strDate = cursor.getString(cursor.getColumnIndex(columns[2]));
                String endDate = cursor.getString(cursor.getColumnIndex(columns[3]));
                String details = cursor.getString(cursor.getColumnIndex(columns[4]));
                int period = cursor.getInt(cursor.getColumnIndex(columns[5]));
                int dateNum = cursor.getInt(cursor.getColumnIndex(columns[6]));
                int achIndex = cursor.getInt(cursor.getColumnIndex(columns[7]));
                String achData = cursor.getString(cursor.getColumnIndex(columns[8]));

                result = "id : " + id + ", " + " title : " + title + ", " + " strDate : "  + strDate + ", " + " endDate : " + endDate;
                index += 1;
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
