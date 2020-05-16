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
    EditText detail;
    int period;
    TextView testLog;

    DBHelper dbHelper;
    SQLiteDatabase database;
    String dbName = "test.db";
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
        TextView textView = (TextView) findViewById(R.id.scheduleStrDate);
        TextView textView2 = (TextView) findViewById(R.id.scheduleEndDate);
        textView.setText(year1 + "/" + (month1 + 1) + "/" + date1);
        textView2.setText(year1 + "/" + (month1 + 1) + "/" + date1);
        Button button = (Button) findViewById(R.id.aa);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), PickDActivity.class);
                startActivityForResult(intent2, 101);
            }
        });

        Button registrationBtn = (Button) findViewById(R.id.registrationBtn);
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 나중에 메인으로 돌아가능 기능도 추가해야함. 테스트용으로 일단 작성.
                // DB도 일단 텍스트 테이터 등록하는 것만 일단 구현. 추후 체크박스와 연동해서 데이터가 설정되도록 변경예정.
                createDatabase();
                createTable(tableName);
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
            TextView textView = (TextView)findViewById(R.id.editText4);
            textView.setText(year2 + "/" + (month2 + 1) + "/" + date2);
        }
    }

    public void executeQuery() {
        println("executeQuery 호출됨.");

        Cursor cursor = database.rawQuery("select * from emp", null);
        int recordCount = cursor.getCount();
        println("레코드 개수: " + recordCount);

        for(int i = 0; i < recordCount; ++i) {
            cursor.moveToNext();
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int age = cursor.getInt(2);
            String mobile = cursor.getString(3);

            println("레코드#" + i + " : " + id + ", " + name + ", " + age + ", " + mobile);
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
                + " detail text, "
                + " period integer)");

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

        database.execSQL("insert into " + tableName
                + "(name, age, mobile) "
                + " values "
                + "('John', 20, '010-1000-1000')");

        println("레코드 추가함.");
    }

    public void println(String data) {
        testLog.append(data + "\n");
    }
}
