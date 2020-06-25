package com.example.androidterm2020;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PickDActivity extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalendarView calendar = (CalendarView)findViewById(R.id.calendar);
        Intent intent = getIntent();
        final String cur_date = intent.getStringExtra("cur_date");
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int DayOfMonth) {
                Intent intent2 = new Intent(getApplicationContext(), ScheduleRegistrationActivity.class);
                String date = year + "-" + (month > 9 ? "" : "0") + (month + 1) + "-" + (DayOfMonth > 9 ? "" : "0") + DayOfMonth;
                if((Integer.parseInt(date.substring(0, 4)) < Integer.parseInt(cur_date.substring(0,4)))
                            ||
                            (Integer.parseInt(date.substring(0, 4)) == Integer.parseInt(cur_date.substring(0,4)) &&
                                    Integer.parseInt(date.substring(5, 7)) < Integer.parseInt(cur_date.substring(5,7)))
                            ||
                            (Integer.parseInt(date.substring(0, 4)) == Integer.parseInt(cur_date.substring(0,4)) &&
                                    Integer.parseInt(date.substring(5, 7)) == Integer.parseInt(cur_date.substring(5,7)) &&
                                    Integer.parseInt(date.substring(8,10)) < Integer.parseInt(cur_date.substring(8, 10)))){
                        intent2.putExtra("date", cur_date);
                        Toast.makeText(PickDActivity.this, "종료날짜가 시작날짜보다 빠름.", Toast.LENGTH_LONG).show();
                        setResult(ScheduleRegistrationActivity.RESULT_CANCELED, intent2);
                        finish();
                }
                intent2.putExtra("date", date);
                setResult(ScheduleRegistrationActivity.RESULT_OK, intent2);
                finish();
            }
        });
    }
}
