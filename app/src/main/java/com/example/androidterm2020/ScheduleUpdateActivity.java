package com.example.androidterm2020;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleUpdateActivity extends AppCompatActivity {
    EditText title;
    TextView scheduleStrDate;
    TextView scheduleEndDate;
    TextView scheduleStrTime;
    TextView scheduleEndTime;
    EditText details;
    TextView toolbarTitle;
    int period = 0; // 나중에 checkbox와 연동되도록 코드를 추가해주자.
    TextView testLog;
    Schedule schedule;

    static final int TARGET_FRAGMENT_INDEX = 1;

    private ScheduleViewModel scheduleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_registeration_activity);
        final Intent intent = getIntent();
        final int ID = intent.getIntExtra("sid", 0);

        Button regBtn = (Button) findViewById(R.id.regBtn);
        regBtn.setText("수정된 정보 업데이트");
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        title = (EditText) findViewById(R.id.editTitle);
        scheduleStrDate = (TextView) findViewById(R.id.editScheduleStrDate);
        scheduleEndDate = (TextView) findViewById(R.id.editScheduleEndDate);
        scheduleStrTime = (TextView) findViewById(R.id.editScheduleStrTime);
        scheduleEndTime = (TextView) findViewById(R.id.editScheduleEndTime);
        details = (EditText) findViewById(R.id.editDetails);
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        toolbarTitle.setText("일정 수정");

        final Calendar myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        };

        scheduleStrDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ScheduleUpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        scheduleStrDate.setText(year + "-" + ((month+1) > 9 ? "" : "0")+ (month+1) + "-" + ((dayOfMonth) > 9 ? "" : "0")+ dayOfMonth);
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        scheduleEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ScheduleUpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        scheduleEndDate.setText(year + "-" + ((month+1) > 9 ? "" : "0")+ (month+1) + "-" + ((dayOfMonth) > 9 ? "" : "0")+ dayOfMonth);
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        scheduleStrTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ScheduleUpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        scheduleStrTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        scheduleEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ScheduleUpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        scheduleEndTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        SimpleDateFormat dateToString = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        // 값이 있으면 기존의 값을 입력해즌다.

        schedule = scheduleViewModel.getScheduleById(ID);
        title.setText(schedule.getTitle());
        details.setText(schedule.getDetails());
        period = schedule.getPeriod();

        String strDate = getDateTime(schedule.getStrDate());
        String endDate = getDateTime(schedule.getEndDate());
        scheduleStrDate.setText(strDate.split(" ")[0]);
        scheduleEndTime.setText(endDate.split(" ")[1]);
        scheduleStrTime.setText(strDate.split(" ")[1]);
        scheduleEndDate.setText(endDate.split(" ")[0]);


        Button updateBtn = (Button) findViewById(R.id.regBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() { // update버튼.
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                // 업데이트 쿼리문.
                updateSchedule();
                Intent finishIntent = new Intent();
                setResult(TARGET_FRAGMENT_INDEX, finishIntent);
                finish();
            }
        });

    }

    public void updateSchedule() {
        schedule.setAchievementData(updateAchievementData());
        schedule.setTitle(title.getText().toString());
        schedule.setStrDate(getLongDate(scheduleStrDate.getText().toString() + " " + scheduleStrTime.getText().toString()));
        schedule.setEndDate(getLongDate(scheduleEndDate.getText().toString() + " " + scheduleEndTime.getText().toString()));
        schedule.setDetails(details.getText().toString());
        schedule.setPeriod(period);


        schedule.setDateNum(calDate(scheduleStrDate.getText().toString() + " " + scheduleStrTime.getText().toString(), scheduleEndDate.getText().toString() + " " + scheduleEndTime.getText().toString()));

        scheduleViewModel.updateSchedule(schedule);
    }

    private String updateAchievementData() {
        int newDateNum = calDate(scheduleStrDate.getText().toString() + " " + scheduleStrTime.getText().toString(), scheduleEndDate.getText().toString() + " " + scheduleEndTime.getText().toString());
        String data = "";
        if(schedule.getAchievementData().length() < newDateNum) { // 날의 일 수 가 증가하면
            // 1. 시작날짜 그대로일 경우
            int dateNum = schedule.getDateNum();
            if(schedule.getStrDate() == getLongDate(scheduleStrDate.getText().toString() + " " + scheduleStrTime.getText().toString())) { // 시작일 그대로면 바로 뒤에 0을 추가함 증가한 만큼 111 -> 111000
                data = schedule.getAchievementData();
                for (int i = 0; i < (newDateNum-dateNum); ++i) {
                    data += "0";
                }
            }
            else { // 2. 시작일이 변한경우 초기화 하고 DateNum 만큼 만들어준다.
                data = "0";
                for (int i = 0; i < newDateNum -1; ++i) {
                    data += "0";
                }
            }
        }
        else if(schedule.getAchievementData().length() < newDateNum) { // 일수가 같고
            // 시작일이 같거나 -> 기존 값 그대로 적용
            int dateNum = schedule.getDateNum();
            if(schedule.getStrDate() == getLongDate(scheduleStrDate.getText().toString() + " " + scheduleStrTime.getText().toString())) { // 그대로 사용함.
                data = schedule.getAchievementData();
            }
            else { // 초기화 하고 DateNum 만큼 만들어준다.  시작일이 다르거나 -> 의미가 없으므로 초기화함.
                data = "0";
                for (int i = 0; i < newDateNum -1; ++i) {
                    data += "0";
                }
            }
        }
        else { // 일수가 줄어듬.
            // 시작일이 같거나 -> 잘라서 그것만 돌려줌.
            // 시작일이 같거나 -> 기존 값 그대로 적용
            int dateNum = schedule.getDateNum();
            if(schedule.getStrDate() == getLongDate(scheduleStrDate.getText().toString() + " " + scheduleStrTime.getText().toString())) { // 시작일이 같음
                data = schedule.getAchievementData().substring(0, newDateNum); // 012345 0123
            }
            else { // 초기화 하고 DateNum 만큼 만들어준다.  시작일이 다르거나 -> 의미가 없으므로 초기화함.
                data = "0";
                for (int i = 0; i < newDateNum -1; ++i) {
                    data += "0";
                }
            }
            // 시작일이 다르거나 -> 의미가 없으므로 초기화함.
        }

        return data;
    }

    private int calDate(String date1, String date2) {
        int calDateDays = -1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date strDate = null;
        Date endDate = null;
        try {
            strDate = dateFormat.parse(date1);
            endDate = dateFormat.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if( date1 != null && date2 != null)  {
            long calDate = endDate.getTime() - strDate.getTime();
            calDateDays = (int)Math.abs(calDate / (24 * 60 * 60 * 1000));
        }
        return calDateDays + 1;
    }

    private String getDateTime(long strDate) { // 20200623 1122 에서 1122만 가져온다.
        String result = "";

        for(int i=0; i<4; ++i) {
            result += (strDate%10); // 2211
            strDate /= 10;
            if(i == 1) {
                result += ":";
            }
        }
        // 만약 보기 안좋으면 여기에서 뉴라인 추가
        result += " ";
        for(int i=0; i<8; ++i) {
            result += (strDate%10); // 2211
            strDate /= 10;
            if(i%2 == 1 && i < 5) {
                result += "-";
            }
        }
        result = (new StringBuffer(result)).reverse().toString(); //20/06/23 11:22

        return result;
    }

    private long getLongDate(String date) {
        date = date.replaceAll("[ :-]", "");
        return Long.parseLong(date);
    }

    @SuppressLint("SetTextI18n")

    private String getCurrentTime() {
        long now = System.currentTimeMillis(); // 현재시간 가져옴.
        Date mDate = new Date(now); // Date 타입으로 바꿈.
        SimpleDateFormat simpleDate = new SimpleDateFormat("hh:mm"); // yyyy-MM-dd hh:mm:ss가 datetime 타입에 딱 알맞다.
        String getTime = simpleDate.format(mDate);

        return getTime;
    }
}
