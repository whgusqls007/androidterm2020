package com.example.androidterm2020;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PickDActivity extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalendarView calendar = (CalendarView)findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int DayOfMonth) {
                Intent intent2 = new Intent(getApplicationContext(), ScheduleRegistrationActivity.class);
                intent2.putExtra("Y2", year);
                intent2.putExtra("M2", month);
                intent2.putExtra("D2", DayOfMonth);
                setResult(ScheduleRegistrationActivity.RESULT_OK, intent2);
                finish();
            }
        });
    }
}