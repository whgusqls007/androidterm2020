package com.example.androidterm2020;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;

public class ScheduleRegistrationActivity extends AppCompatActivity {
    EditText title;
    EditText scheduleStrDate;
    EditText scheduleEndDate;
    EditText schedule_time;
    EditText details;
    int period = 1; // 나중에 checkbox와 연동되도록 코드를 추가해주자.
    TextView testLog;

    DBHelper dbHelper;
    SQLiteDatabase database;
    String dbName = "test2.db";
    String tableName = "test_tb";

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_registeration_activity);

        // 달력에서 날짜 누르고 여기 올때 자동으로 날짜 + 시간을 추가해주는 기능.
        Intent intent = getIntent();
        int year1 = intent.getIntExtra("Y", 0);
        int month1 = intent.getIntExtra("M", 0);
        int date1 = intent.getIntExtra("D", 0);
        TextView textView = (TextView) findViewById(R.id.editScheduleStrDate);
        TextView textView2 = (TextView) findViewById(R.id.editScheduleEndDate);

        // 1~9월과 10월 이상의 1자리 2자리 구분을 하기 위함.
        if (month1 >= 9) {
            textView.setText(year1 + "-" + (month1 + 1) + "-" + date1 + ' ' + getCurrentTime());
            textView2.setText(year1 + "-" + (month1 + 1) + "-" + date1 + ' ' + getCurrentTime());
        }
        else {
            textView.setText(year1 + "-" + "0" + (month1 + 1) + "-" + date1 + ' ' + getCurrentTime());
            textView2.setText(year1 + "-" + "0" + (month1 + 1) + "-" + date1 + ' ' + getCurrentTime());
        }

        // 입력장소들을 각각의 대응하는 id로 mapping함.
        title = (EditText) findViewById(R.id.editTitle);
        scheduleStrDate = (EditText) findViewById(R.id.editScheduleStrDate);
        scheduleEndDate = (EditText) findViewById(R.id.editScheduleEndDate);
        details = (EditText) findViewById(R.id.editDetails);
        testLog = (TextView) findViewById(R.id.logTxt); // 임시로 이용하는 로그 비슷한 기능담당할 놈.

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

                // 여기는 만들려는게 이미 있으면 아무일 없다.
                createDatabase();
                createTable(tableName);

                // 데이터를 추가함.
                insertRecord();
            }
        });

        Button selectBtn = (Button) findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // 입력한 데이터 출력해준다.
                    println("출력시도");
                    selectQuery();
            }
        });
    }

    private String getCurrentTime() {
        long now = System.currentTimeMillis(); // 현재시간 가져옴.
        Date mDate = new Date(now); // Date 타입으로 바꿈.
        SimpleDateFormat simpleDate = new SimpleDateFormat("hh:mm"); // yyyy-MM-dd hh:mm:ss가 datetime 타입에 딱 알맞다.

        return simpleDate.format(mDate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101){
            int year2 = data.getIntExtra("Y2", 0);
            int month2 = data.getIntExtra("M2", 0);
            int date2 = data.getIntExtra("D2", 0);
            TextView textView = (TextView)findViewById(R.id.editScheduleEndDate);
            if(month2 >= 9) {
                textView.setText(year2 + "-" + (month2 + 1) + "-" + date2 + ' ' + getCurrentTime());
            }
            else {
                textView.setText(year2 + "-" + "0" + (month2 + 1) + "-" + date2 + ' ' + getCurrentTime());
            }
        }
    }

    // sql상에서 select한거 출력시키는 함수.
    public void selectQuery() {
        println("selectQuery 호출됨.");

        Cursor cursor = database.rawQuery("select * from " + tableName, null);
        int recordCount = cursor.getCount();
        println("레코드 개수: " + recordCount);

        for(int i = 0; i < recordCount; ++i) {
            cursor.moveToNext();

            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String scheduleStrDate = cursor.getString(2);
            String scheduleEndDate = cursor.getString(3);
            String detail = cursor.getString(4);
            int period = cursor.getInt(5);

            println("레코드#" + i + " : "
                    + title + ", "
                    + scheduleStrDate + ", "
                    + scheduleEndDate + ", "
                    + detail + ", "
                    + period);
        }
        cursor.close();
    }

    // 데이터베이스를 만들거나 이미 있다면 가져오는 함수.
    private void createDatabase() {
        println("createDatabase 호출됨.");

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        println("데이터베이스 생성함: " + dbName);
    }

    // 테이블을 만드는 함수 이미 있다면 만들지 않는다.
    private void createTable(String name) {
        println("createTable 호출됨.");

        if (database == null) {
            println("데이터베이스를 먼저 생성하세요.");
            return;
        }

        database.execSQL("create table if not exists " + name + "("
                + "_id integer PRIMARY KEY autoincrement, "
                + " title text, "
                + " str_date datetime, "
                + " end_date datetime, "
                + " details text, "
                + " sch_period integer)");

        println("테이블 생성함: " + name);
    }


    // 테이블에 데이터를 추가함.
    private void insertRecord() {
        println("insertRecord 호출됨.");

        if (database == null) {
            println("데이터베이스를 먼저 생성하세요.");
            return;
        }

        if (tableName == null) {
            println("테이블을 먼저 생성하세요.");
            return;
        }

        String sql = "insert into " + tableName
                + " (title, str_date, end_date, details, sch_period) "
                + " values "
                + "(" + "'" + title.getText().toString() + "'" + ", "
                + "'" + scheduleStrDate.getText().toString() + ":00" + "'" + ", "
                + "'" + scheduleEndDate.getText().toString() + ":00" + "'" + ", "
                + "'" + details.getText().toString() + "'" + ", "
                + period + ")";


        database.execSQL(sql);

        println("레코드 추가함.");
    }

    // 임시화면에 보여주는 용도.
    public void println(String data) {
        testLog.append(data + "\n");
    }
}
