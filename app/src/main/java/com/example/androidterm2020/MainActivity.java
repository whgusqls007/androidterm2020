package com.example.androidterm2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    ListView listView = null;
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
        Button buttonOpen = (Button)findViewById(R.id.side);
        buttonOpen.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
                if(!drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                else{
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }
        });

        final String[] items = {"전체 일정", "날씨 확인", "환경 설정"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);

        listView = (ListView)findViewById(R.id.drawer_menu);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Button button2 = (Button) findViewById(R.id.side);
                switch (position){
                    case 0 :
                        button2.setTextColor(Color.rgb(0x00,0xFF, 0x00));
                        break;
                    case 1 :
                        button2.setTextColor(Color.rgb(0xFF,0x00, 0x00));
                        break;
                    case 2 :
                        button2.setTextColor(Color.rgb(0x00,0x00, 0xFF));
                        break;
                }

                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });

    }

}