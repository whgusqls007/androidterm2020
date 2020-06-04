package com.example.androidterm2020;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleRegistrationActivity extends AppCompatActivity {
    EditText title;
    EditText scheduleStrDate;
    EditText scheduleEndDate;
    EditText schedule_time;
    EditText details;
    int period = 0; // 나중에 checkbox와 연동되도록 코드를 추가해주자.
    TextView testLog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_registeration_activity);

        // 달력에서 날짜 누르고 여기 올때 자동으로 날짜 + 시간을 추가해주는 기능.
        Intent intent = getIntent();
        String datetime = intent.getStringExtra("date");
        TextView textView = (TextView) findViewById(R.id.editScheduleStrDate);
        TextView textView2 = (TextView) findViewById(R.id.editScheduleEndDate);

        textView.setText(datetime +' ' + getCurrentTime());
        textView2.setText(datetime + ' ' + getCurrentTime());

        // 입력장소들을 각각의 대응하는 id로 mapping함.
        title = (EditText) findViewById(R.id.editTitle);
        scheduleStrDate = (EditText) findViewById(R.id.editScheduleStrDate);
        scheduleEndDate = (EditText) findViewById(R.id.editScheduleEndDate);
        details = (EditText) findViewById(R.id.editDetails);
        testLog = (TextView) findViewById(R.id.logTxt); // 임시로 이용하는 로그 비슷한 기능담당할 놈.
        println("로그 창 생성완료.");

        Button button = (Button) findViewById(R.id.aa);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), PickDActivity.class);
                startActivityForResult(intent2, 101);
            }
        });

        Button registrationBtn = (Button) findViewById(R.id.regBtn);
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 나중에 메인으로 돌아가도록 기능도 추가해야함. 테스트용으로 일단 작성.
                // DB도 일단 텍스트 테이터 등록하는 것만 일단 구현. 추후 체크박스와 연동해서 데이터가 설정되도록 변경예정.
                println("등록시도");

                // 데이터를 추가함.
                insertSchedule();
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
                println("period는 0");
                if(checkBox.isChecked() == true) {
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    period = 0;
                }
            }
        });


        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                println("period는 1");
                if(checkBox2.isChecked() == true) {
                    checkBox.setChecked(false);
                    checkBox3.setChecked(false);
                    period = 1;
                }
            }
        });

        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                println("period는 2");
                if(checkBox3.isChecked() == true) {
                    checkBox.setChecked(false);
                    checkBox2.setChecked(false);
                    period = 2;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101){

            String date = data.getStringExtra("date");
            TextView textView = (TextView)findViewById(R.id.editScheduleEndDate);
            textView.setText(date + ' ' + getCurrentTime());
        }
    }

    public void insertSchedule() {
        println("insertPerson 호출됨");
        Uri uri = ScheduleProvider.CONTENT_URI;

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        String[] columns = cursor.getColumnNames(); // 문제의 line;
        println("columns count -> " + columns.length);
        for (int i = 0; i < columns.length; i++) {
            println("#" + i + " : " + columns[i]);
        }

        ContentValues values = new ContentValues();
        String strDate = scheduleStrDate.getText().toString();
        String endDate = scheduleEndDate.getText().toString();
        long dateNum = calDate(strDate, endDate);
        String data = "";

        for(int i=0; i<dateNum; ++i) {
            data += '0';
        }

        values.put(DBHelper.SCHEDULE_TITLE, title.getText().toString());
        values.put(DBHelper.SCHEDULE_START_DATE, strDate);
        values.put(DBHelper.SCHEDULE_END_DATE, endDate);
        values.put(DBHelper.SCHEDULE_DETAILS, details.getText().toString());
        values.put(DBHelper.SCHEDULE_PERIOD, period);
        values.put(DBHelper.SCHEDULE_DATE_NUM, dateNum);
        values.put(DBHelper.SCHEDULE_ACHIEVEMENT_INDEX, 0);
        values.put(DBHelper.SCHEDULE_ACHIEVEMENT_DATA, data);

        uri = getContentResolver().insert(uri, values); //
        println("insert 결과 -> " + uri.toString());
    }

    public void querySchedule() {
        try {
            Uri uri = ScheduleProvider.CONTENT_URI;

            String[] columns = DBHelper.ALL_COLUMNS;
            Cursor cursor = getContentResolver().query(uri, columns, null, null, DBHelper.SCHEDULE_START_DATE + " ASC");
            println("query 결과 : " + cursor.getCount());

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

                println("#" + index + " -> " + id + ", " + title + ", " + strDate + ", " + endDate + ", " + details + ", " + period + ", " + dateNum + ", " + achIndex + ", " + achData);
                index += 1;
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 나중에 수정예정
    public void updateSchedule() {
        Uri uri = ScheduleProvider.CONTENT_URI;

        String selection = "mobile = ?";
        String[] selectionArgs = new String[] {"010-1000-1000"};
        ContentValues updateValue = new ContentValues();
        updateValue.put("mobile", "010-2000-2000");

        int count = getContentResolver().update(uri, updateValue, selection, selectionArgs);
        println("update 결과 : " + count);
    }

    // 나중에 수정예정, 일정 수정하러 가는 기능에서 사용예쩡.
    public void deleteSchedule() {
        Uri uri = ScheduleProvider.CONTENT_URI;

        String selection = "name = ?";
        String[] selectionArgs = new String[] {"john"};

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

            calDateDays = Math.abs(calDate / (24*60*60*1000));
        }
        catch(ParseException e) {
            println("date 계산 예외 발생 : " + e);
        }

        return calDateDays;
    }

    // 임시화면에 보여주는 용도.
    public void println(String data) {
        testLog.append(data + "\n");
    }
}
