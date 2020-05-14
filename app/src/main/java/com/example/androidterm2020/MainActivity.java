//커밋테스트입니다
//클론테스트입니다
package com.example.androidterm2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;

public class MainActivity extends AppCompatActivity {
    @Override

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

        Button tempButton = (Button)findViewById(R.id.tempButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), TodayScheduleListActivity.class);
                startActivity(intent);
            }
        });
    }

}
        /*Button button = (Button) findViewById(R.id.nextActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScheduleRegistrationActivity.class);
                //intent.putExtra("message", "반갑습니다.");
                startActivity(intent);
            }
        });*/