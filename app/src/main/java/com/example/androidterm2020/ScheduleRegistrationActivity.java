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
import java.time.Year;

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
    String dbName = "test1.db";
    String tableName = "test_tb";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_registeration_activity);

        Intent intent = getIntent();
        int year1 = intent.getIntExtra("Y", 0);
        int month1 = intent.getIntExtra("M", 0);
        int date1 = intent.getIntExtra("D", 0);
        TextView textView = (TextView) findViewById(R.id.editScheduleStrDate);
        TextView textView2 = (TextView) findViewById(R.id.editScheduleEndDate);

        if (month1 >= 9) {
            textView.setText(year1 + "-" + (month1 + 1) + "-" + date1);
            textView2.setText(year1 + "-" + (month1 + 1) + "-" + date1);
        }
        else {
            textView.setText(year1 + "-" + "0" + (month1 + 1) + "-" + date1);
            textView2.setText(year1 + "-" + "0" + (month1 + 1) + "-" + date1);
        }

        title = (EditText) findViewById(R.id.editTitle);
        scheduleStrDate = (EditText) findViewById(R.id.editScheduleStrDate);
        scheduleEndDate = (EditText) findViewById(R.id.editScheduleEndDate);
        schedule_time = (EditText) findViewById(R.id.editTime);
        details = (EditText) findViewById(R.id.editDetails);
        testLog = (TextView) findViewById(R.id.logTxt);
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
                // 나중에 메인으로 돌아가능 기능도 추가해야함. 테스트용으로 일단 작성.
                // DB도 일단 텍스트 테이터 등록하는 것만 일단 구현. 추후 체크박스와 연동해서 데이터가 설정되도록 변경예정.
                println("등록시도");
                createDatabase();
                createTable(tableName);
                insertRecord(); // 문제의 코드.
            }
        });

        Button selectBtn = (Button) findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // 입력한 데이터 출력해준다.
                    println("출력시도");
                    selectQuery(); // 문제의 코드.
            }
        });
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
                textView.setText(year2 + "-" + (month2 + 1) + "-" + date2);
            }
            else {
                textView.setText(year2 + "-" + "0" + (month2 + 1) + "-" + date2);
            }
        }
    }

    public void selectQuery() {
        println("selectQuery 호출됨.");

        Cursor cursor = database.rawQuery("select * from " + tableName, null);
        int recordCount = cursor.getCount();
        println("레코드 개수: " + recordCount);

        for(int i = 0; i < recordCount; ++i) {
            cursor.moveToNext();

            String title = cursor.getString(0);
            String scheduleStrDate = cursor.getString(1);
            String scheduleEndDate = cursor.getString(2);
            String schedule_time = cursor.getString(3);
            String detail = cursor.getString(4);
            int period = cursor.getInt(5);

            println("레코드#" + i + " : "
                    + title + ", "
                    + scheduleStrDate + ", "
                    + scheduleEndDate + ", "
                    + schedule_time + ", "
                    + detail + ", "
                    + period);
        }
        cursor.close();
    }

    private void createDatabase() {
        println("createDatabase 호출됨.");

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        println("데이터베이스 생성함: " + dbName);
    }

    private void createTable(String name) {
        println("createTable 호출됨.");

        if (database == null) {
            println("데이터베이스를 먼저 생성하세요.");
            return;
        }

        database.execSQL("create table if not exists " + name + "("
                + "_id integer PRIMARY KEY autoincrement, "
                + " title text, "
                + " str_date date, "
                + " end_date date, "
                + " schedule_time time, "
                + " details text, "
                + " sch_period integer)");

        println("테이블 생성함: " + name);
    }

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
                + " (title, str_date, end_date, schedule_time,  details, sch_period) "
                + " values "
                + "(" + "'" + title.getText().toString() + "'" + ", "
                + "'" + scheduleStrDate.getText().toString() + "'" + ", "
                + "'" + scheduleEndDate.getText().toString() + "'" + ", "
                + "'" + schedule_time.getText().toString() + "'" + ", "
                + "'" + details.getText().toString() + "'" + ", "
                + period + ")";


        database.execSQL(sql);

        println("레코드 추가함.");
    }

    public void println(String data) {
        testLog.append(data + "\n");
    }
}
