package com.example.androidterm2020;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.security.Provider;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

// 모든 값을 입력하지 않았을 경우 스넥바 보여주기,
public class ScheduleRegistrationActivity extends AppCompatActivity {
    EditText title;
    TextView scheduleStrDate;
    TextView scheduleEndDate;
    TextView test_view;
    EditText details;
    int period = -1; // 나중에 checkbox와 연동되도록 코드를 추가해주자.
    TextView testLog;
    // 달력에서 날짜 누르고 여기 올때 자동으로 날짜 + 시간을 추가해주는 기능.
    String start_date;
    int requestCode = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_registeration_activity);
        test_view = (TextView) findViewById(R.id.testText);
        Intent intent = getIntent();
        final String datetime = intent.getStringExtra("date");
        //compare_datetime = datetime.split("-");
        final TextView textView = (TextView) findViewById(R.id.editScheduleStrDate);
        final TextView textView2 = (TextView) findViewById(R.id.editScheduleEndDate);
        textView.setText(datetime + ' ' + getCurrentTime());
        textView2.setText(datetime + ' ' + getCurrentTime());

        // 입력장소들을 각각의 대응하는 id로 mapping함.
        title = (EditText) findViewById(R.id.editTitle);
        scheduleStrDate = (TextView) findViewById(R.id.editScheduleStrDate);
        scheduleEndDate = (TextView) findViewById(R.id.editScheduleEndDate);
        details = (EditText) findViewById(R.id.editDetails);
        testLog = (TextView) findViewById(R.id.logTxt); // 임시로 이용하는 로그 비슷한 기능담당할 놈.
        println("로그 창 생성완료.");
        start_date = scheduleStrDate.getText().toString();
        test_view.setText(start_date);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                Bundle bundle = new Bundle();
                // sql의 datetime에서 뒷부분인 시간을 bundle에 넣음.
                bundle.putString("time", textView.getText().toString().split(" ")[1]);
                bundle.putBoolean("is_start", true);
                timePickerFragment.setArguments(bundle);
                timePickerFragment.show(getSupportFragmentManager(), "TimePicker");
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                Bundle bundle = new Bundle();
                // sql의 datetime에서 뒷부분인 시간을 bundle에 넣음.
                bundle.putString("time", textView2.getText().toString().split(" ")[1]);
                bundle.putBoolean("is_start", false);
                timePickerFragment.setArguments(bundle);
                timePickerFragment.show(getSupportFragmentManager(), "TimePicker");
            }
        });
        Button button = (Button) findViewById(R.id.aa);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), PickDActivity.class);
                intent2.putExtra("cur_date", datetime);
                startActivityForResult(intent2, 101);
            }
        });

        final Button registrationBtn = (Button) findViewById(R.id.regBtn);
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 나중에 메인으로 돌아가도록 기능도 추가해야함. 테스트용으로 일단 작성.
                // DB도 일단 텍스트 테이터 등록하는 것만 일단 구현. 추후 체크박스와 연동해서 데이터가 설정되도록 변경예정. -> 연동완료
                println("등록시도");
                // 데이터를 추가함.
                String queryData = insertSchedule();

                //registerAlarm( "schAlarm", queryData);
                //finish();
            }
        });

        Button selectBtn = (Button) findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력한 데이터 출력해준다.
                println("출력시도");
                querySchedule();
            }
        });

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        final CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    period = 0;
                    println("period는 0");
                }
                if(!checkBox.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()){
                    period = -1;
                    println("period는 -1");
                }
            }
        });


        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox2.isChecked()) {
                    checkBox.setChecked(false);
                    checkBox3.setChecked(false);
                    period = 1;
                    println("period는 1");
                }
                if(!checkBox.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()){
                    period = -1;
                    println("period는 -1");
                }
            }
        });

        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox3.isChecked()) {
                    checkBox.setChecked(false);
                    checkBox2.setChecked(false);
                    period = 2;
                    println("period는 2");
                }
                if(!checkBox.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()){
                    period = -1;
                    println("period는 -1");
                }
            }
        });
    }

    private String getCurrentTime() {
        long now = System.currentTimeMillis(); // 현재시간 가져옴.
        Date mDate = new Date(now); // Date 타입으로 바꿈.
        SimpleDateFormat simpleDate = new SimpleDateFormat("hh:mm"); // yyyy-MM-dd hh:mm:ss가 datetime 타입에 딱 알맞다.
        String getTime = simpleDate.format(mDate);

        return getTime;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {

            String date = data.getStringExtra("date");
            TextView textView = (TextView) findViewById(R.id.editScheduleEndDate);
            textView.setText(date + ' ' + getCurrentTime());
        }
    }

    // 하나라도 안적혀있으면 등록이 안된다고 알리는 알림을 호출하는 기능을 추가해야함.
    public String insertSchedule() {
        int start_hour = Integer.parseInt(scheduleStrDate.getText().toString().substring(11, 13));
        int start_min = Integer.parseInt(scheduleStrDate.getText().toString().substring(14, 16));
        int start_year = Integer.parseInt(scheduleStrDate.getText().toString().substring(0, 4));
        int start_month = Integer.parseInt(scheduleStrDate.getText().toString().substring(5, 7));
        int start_day = Integer.parseInt(scheduleStrDate.getText().toString().substring(8, 10));
        GregorianCalendar calendar = (GregorianCalendar)GregorianCalendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(start_year, start_month - 1, start_day, start_hour, start_min, 0);
        if(calendar.before(Calendar.getInstance())){
            calendar.add(GregorianCalendar.YEAR, 1);
            Toast.makeText(ScheduleRegistrationActivity.this, "내년으로 설정", Toast.LENGTH_LONG).show();
        }
        diaryNotification(calendar);
        println("requestCode : " + Integer.toString(requestCode));
        println("insertPerson 호출됨");
        Uri uri = ScheduleProvider.CONTENT_URI;

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        String[] columns = cursor.getColumnNames(); // 문제의 line;
        println("columns count -> " + columns.length);
        for (int i = 0; i < columns.length; i++) {
            println("#" + i + " : " + columns[i]);
        }

        ContentValues values = new ContentValues();
        String strDate = scheduleStrDate.getText().toString() + ":00";
        String endDate = scheduleEndDate.getText().toString() + ":00";
        long dateNum = calDate(strDate, endDate);
        String data = "";

        for (int i = 0; i < dateNum; ++i) {
            data += '0';
        }

        values.put(DBHelper.SCHEDULE_TITLE, title.getText().toString());
        values.put(DBHelper.SCHEDULE_START_DATE, strDate);
        values.put(DBHelper.SCHEDULE_END_DATE, endDate);
        values.put(DBHelper.SCHEDULE_DETAILS, details.getText().toString());
        values.put(DBHelper.SCHEDULE_PERIOD, period);
        values.put(DBHelper.SCHEDULE_DATE_NUM, dateNum);
        values.put(DBHelper.SCHEDULE_ACHIEVEMENT_INDEX, 0);

        // 일단 임시 데이터, 7일을 기간으로 잡고 "011100100"
        values.put(DBHelper.SCHEDULE_ACHIEVEMENT_DATA, data); // data

        uri = getContentResolver().insert(uri, values); //
        println("insert 결과 -> " + uri.toString());

        String queryData = title.getText().toString() + "/" + strDate;
        return queryData;
    }

    public void querySchedule() {
        try {
            Uri uri = ScheduleProvider.CONTENT_URI;

            String[] columns = DBHelper.ALL_COLUMNS;
            Cursor cursor = getContentResolver().query(uri, columns, null, null, DBHelper.SCHEDULE_START_DATE + " ASC");
            println("query 결과 : " + cursor.getCount());

            int index = 0;
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(columns[0]));
                String title = cursor.getString(cursor.getColumnIndex(columns[1]));
                String strDate = cursor.getString(cursor.getColumnIndex(columns[2]));
                String endDate = cursor.getString(cursor.getColumnIndex(columns[3]));
                String details = cursor.getString(cursor.getColumnIndex(columns[4]));
                int period = cursor.getInt(cursor.getColumnIndex(columns[5]));
                int dateNum = cursor.getInt(cursor.getColumnIndex(columns[6]));
                int achIndex = cursor.getInt(cursor.getColumnIndex(columns[7]));
                String achData = cursor.getString(cursor.getColumnIndex(columns[8]));

                println("#" + index + " -> " + id + ", " + title + ", " + strDate + ", " + endDate + ", " + details + ", " + period + ", " + dateNum + ", " + achIndex + ", " + achData);
                index += 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSchId(String strDate, String title) {
        int id = -1;
        try {
            Uri uri = ScheduleProvider.CONTENT_URI;

            String[] columns = DBHelper.ALL_COLUMNS;
            String selection = DBHelper.SCHEDULE_START_DATE + " = ? and " + DBHelper.SCHEDULE_TITLE + " = ?";
            Cursor cursor = getContentResolver().query(uri, columns, selection, new String[]{strDate, title}, DBHelper.SCHEDULE_START_DATE + " ASC");

            if (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(columns[0]));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    // 나중에 수정예정
    public void updateSchedule() {
        Uri uri = ScheduleProvider.CONTENT_URI;

        String selection = "mobile = ?";
        String[] selectionArgs = new String[]{"010-1000-1000"};
        ContentValues updateValue = new ContentValues();
        updateValue.put("mobile", "010-2000-2000");

        int count = getContentResolver().update(uri, updateValue, selection, selectionArgs);
        println("update 결과 : " + count);
    }

    // 나중에 수정예정, 일정 수정하러 가는 기능에서 사용예쩡.
    public void deleteSchedule() {
        Uri uri = ScheduleProvider.CONTENT_URI;

        String selection = "name = ?";
        String[] selectionArgs = new String[]{"john"};

        int count = getContentResolver().delete(uri, selection, selectionArgs);
        println("delete 결과 : " + count);
    }

    // 시작일부터 끝나는 날까지 날의 수를 계산하기위한 함수.
    public long calDate(String date1, String date2) {
        long calDateDays = -1;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date BeginDate = format.parse(date1);
            Date EndDate = format.parse(date2);

            long calDate = BeginDate.getTime() - EndDate.getTime();

            calDateDays = Math.abs(calDate / (24 * 60 * 60 * 1000));
        } catch (ParseException e) {
            println("date 계산 예외 발생 : " + e);
        }

        return calDateDays;
    }

    // 임시화면에 보여주는 용도.
    public void println(String data) {
        testLog.append(data + "\n");
    }

    @SuppressLint("ShortAlarm")
    void diaryNotification(Calendar calendar) {
        Intent alarmIntent = new Intent(this, Alarm_Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmIntent, 0);
        //requestCode = requestCode + 1;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(period == -1){
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }else{
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        }
        else if (period == 2) {
            long INTERVAL = 1000 * 60;
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                }else{
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                }
            }
        }
        else if (period == 1) {
            long INTERVAL = 1000 * 30;
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                }else{
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
}
    // 이 함수를 service로 옮기기.
//    private void registerAlarm(String requestType, String queryData) {
//        // 알람메니저를 시스템에서 요청해서 가져오기.
//        String time = queryData.split("/")[0];
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        intent.putExtra("requestType", requestType);
//        intent.putExtra("queryData", queryData);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
//
//        String[] timeData = time.split(" ")[1].split(":");
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeData[0]));
//        calendar.set(Calendar.MINUTE, Integer.parseInt(timeData[1]));
//        calendar.set(Calendar.SECOND, 0);
//
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//        insertAlarm(requestType, queryData);
//    }

////////////////알람 삭제////////////
/*
public void unregist(View view) {
    Intent intent = new Intent(this, Alarm.class);
    PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
    alarmManager.cancel(pIntent);
}// unregist()..
*/
// 부팅 후 실행되는 리시버 사용가능하게 설정
            /*pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);*/