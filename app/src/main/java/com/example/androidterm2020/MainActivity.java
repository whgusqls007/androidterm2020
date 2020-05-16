package com.example.androidterm2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalendarView calendar = (CalendarView)findViewById(R.id.calendar);
        calendar.setWeekSeparatorLineColor(Color.BLACK);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int DayOfMonth) {
                Intent intent = new Intent(getApplicationContext(), ScheduleRegistrationActivity.class);
                intent.putExtra("Y", year);
                intent.putExtra("M", month);
                intent.putExtra("D", DayOfMonth);
                startActivity(intent);
            }
        });
        Button button = (Button) findViewById(R.id.tempB);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), ShowDetail.class);
                startActivity(intent2);
            }
        });
        Button button1 = (Button) findViewById(R.id.tempB1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getApplicationContext(), Achieve.class);
                startActivity(intent3);
            }
        });

    }

}