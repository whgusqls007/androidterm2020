package com.example.androidterm2020;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ScheduleUpdateActivity extends AppCompatActivity {
    EditText title;
    EditText scheduleStrDate;
    EditText scheduleEndDate;
    EditText details;
    int period = 0; // 나중에 checkbox와 연동되도록 코드를 추가해주자.
    TextView testLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_registeration_activity);
        Intent intent = getIntent();
        final int ID = intent.getIntExtra("ID", 0);

        Button regBtn = (Button) findViewById(R.id.regBtn);
        regBtn.setText("수정된 정보 업데이트");

        Cursor cursor = getContentResolver().query(ScheduleProvider.CONTENT_URI, DBHelper.ALL_COLUMNS, DBHelper.SCHEDULE_ID + " = " + ID, null, null);

        title = (EditText) findViewById(R.id.editTitle);
        scheduleStrDate = (EditText) findViewById(R.id.editScheduleStrDate);
        scheduleEndDate = (EditText) findViewById(R.id.editScheduleEndDate);
        details = (EditText) findViewById(R.id.editDetails);
        testLog = (TextView) findViewById(R.id.logTxt); // 임시로 이용하는 로그 비슷한 기능담당할 놈.

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            title.setText(cursor.getString(cursor.getColumnIndex(DBHelper.SCHEDULE_TITLE)));
            scheduleStrDate.setText((cursor.getString(cursor.getColumnIndex(DBHelper.SCHEDULE_START_DATE))));
            scheduleEndDate.setText(cursor.getString(cursor.getColumnIndex(DBHelper.SCHEDULE_END_DATE)));
            //details.setText(cursor.getString());
        }

        //title.setText();

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        final CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if(checkBox3.isChecked() == true) {
                    checkBox.setChecked(false);
                    checkBox2.setChecked(false);
                    period = 2;
                }
            }
        });
    }
}
