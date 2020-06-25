package com.example.androidterm2020;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// 모든 값을 입력하지 않았을 경우 스넥바 보여주기,
public class ScheduleRegistrationActivity extends AppCompatActivity {
    EditText title;
    TextView scheduleStrDate;
    TextView scheduleEndDate;
    TextView test_view;
    EditText details;
    CheckBox checkBox;
    CheckBox checkBox2;
    CheckBox checkBox3;
    Button button;
    Button registrationBtn;
    Button selectBtn;
    int period = -1; // 나중에 checkbox와 연동되도록 코드를 추가해주자.
    TextView testLog;
    // 달력에서 날짜 누르고 여기 올때 자동으로 날짜 + 시간을 추가해주는 기능.
    String start_date;
    boolean allow_alarm = true;


    private ScheduleViewModel scheduleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_registeration_activity);
        test_view = (TextView) findViewById(R.id.testText);
        Intent intent = getIntent();

        init(intent.getStringExtra("date"));


    }

    private void init(String datetime) {
        initUI(datetime);
        setCheckBoxes();
        initScheduleViewModel();
        initButtons(datetime);
    }

    private void initButtons ( final String datetime){
        // 종료일 날짜 받아오는 버튼
        button = (Button) findViewById(R.id.aa);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), PickDActivity.class);
                intent2.putExtra("cur_date", datetime);
                startActivityForResult(intent2, 101);
            }
        });

        registrationBtn = (Button) findViewById(R.id.regBtn);
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 나중에 메인으로 돌아가도록 기능도 추가해야함. 테스트용으로 일단 작성.
                // DB도 일단 텍스트 테이터 등록하는 것만 일단 구현. 추후 체크박스와 연동해서 데이터가 설정되도록 변경예정. -> 연동완료
                println("등록시도");
                // 데이터를 추가함.
                int id = insertSchedule();
                println("받은  id값 : " + id);
                //registerAlarm( "schAlarm", queryData);
                finish();
            }
        });

        Button selectBtn = (Button) findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력한 데이터 출력해준다.
                println("출력시도");
                //querySchedule();
            }
        });
    }

    private void initScheduleViewModel () {
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        scheduleViewModel.getAllSchedules().observe(this, new Observer<List<Schedule>>() {
            @Override
            public void onChanged(@Nullable final List<Schedule> schedules) {
                println("총 스케쥴의 수 : " + schedules.size());
                for (Schedule schedule : schedules) {
                    println("id : " + schedule.getSid() + ", title : " + schedule.getTitle()
                            + ", strDate : " + schedule.getStrDate()
                            + ", endDate : " + schedule.getEndDate()
                            + ", details :" + schedule.getDetails()
                            + ", period : " + schedule.getPeriod()
                            + ", dateNum : " + schedule.getDateNum()
                            + ", index : " + schedule.getIndex()
                            + ", achiveData : " + schedule.getAchievementData()
                    );
                }
            }
        });
    }

    private void initUI(String datetime) {
        title = (EditText) findViewById(R.id.editTitle);
        scheduleStrDate = (TextView) findViewById(R.id.editScheduleStrDate);
        scheduleEndDate = (TextView) findViewById(R.id.editScheduleEndDate);
        details = (EditText) findViewById(R.id.editDetails);
        testLog = (TextView) findViewById(R.id.logTxt); // 임시로 이용하는 로그 비슷한 기능담당할 놈.
        println("로그 창 생성완료.");
        start_date = scheduleStrDate.getText().toString();
        test_view.setText(start_date);

        scheduleStrDate.setText(datetime + ' ' + getCurrentTime());
        scheduleEndDate.setText(datetime + ' ' + getCurrentTime());

        scheduleStrDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                Bundle bundle = new Bundle();
                // sql의 datetime에서 뒷부분인 시간을 bundle에 넣음.
                bundle.putString("time", scheduleStrDate.getText().toString().split(" ")[1]);
                bundle.putBoolean("is_start", true);
                timePickerFragment.setArguments(bundle);
                timePickerFragment.show(getSupportFragmentManager(), "TimePicker");
            }
        });
        scheduleEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                Bundle bundle = new Bundle();
                // sql의 datetime에서 뒷부분인 시간을 bundle에 넣음.
                bundle.putString("time", scheduleEndDate.getText().toString().split(" ")[1]);
                bundle.putBoolean("is_start", false);
                timePickerFragment.setArguments(bundle);
                timePickerFragment.show(getSupportFragmentManager(), "TimePicker");
            }
        });
    }

    private void println(String s) {

    }

    private void setCheckBoxes() {
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    period = 0;
                    println("period는 0");
                }
                if (!checkBox.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()) {
                    period = -1;
                    println("period는 -1");
                }
            }
        });


        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox2.isChecked()) {
                    checkBox.setChecked(false);
                    checkBox3.setChecked(false);
                    period = 1;
                    println("period는 1");
                }
                if (!checkBox.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()) {
                    period = -1;
                    println("period는 -1");
                }
            }
        });

        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox3.isChecked()) {
                    checkBox.setChecked(false);
                    checkBox2.setChecked(false);
                    period = 2;
                    println("period는 2");
                }
                if (!checkBox.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()) {
                    period = -1;
                    println("period는 -1");
                }
            }
        });
    }

    private void printAll (List < Schedule > allSchList) {
        int index = 0;
        if (allSchList == null) {
            println("내용이 없음.");
            return;
        }
        for (Schedule sch : allSchList) {
            int id = sch.getSid();
            String title = sch.getTitle();
            long strDate = sch.getStrDate();
            long endDate = sch.getEndDate();
            String details = sch.getDetails();
            int period = sch.getPeriod();
            int dateNum = sch.getDateNum();
            int achIndex = sch.getIndex();
            String achData = sch.getAchievementData();

            println("#" + index + " -> " + id + ", " + title + ", " + strDate + ", " + endDate + ", " + details + ", " + period + ", " + dateNum + ", " + achIndex + ", " + achData);
            println("id : " + id + ", title : " + title
                    + ", strDate : " + strDate
                    + ", endDate : " + endDate
                    + ", details :" + details
                    + ", period : " + period
                    + ", dateNum : " + dateNum
                    + ", index : " + achIndex
                    + ", achiveData : " + achData
            );
            index += 1;
        }
    }

    private String getCurrentTime() {
        long now = System.currentTimeMillis(); // 현재시간 가져옴.
        Date mDate = new Date(now); // Date 타입으로 바꿈.
        SimpleDateFormat simpleDate = new SimpleDateFormat("hh:mm"); // yyyy-MM-dd hh:mm:ss가 datetime 타입에 딱 알맞다.
        String getTime = simpleDate.format(mDate);

        return getTime;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {

            String date = data.getStringExtra("date");
            TextView textView = (TextView) findViewById(R.id.editScheduleEndDate);
            textView.setText(date + ' ' + getCurrentTime());
        }
    }

    // 하나라도 안적혀있으면 등록이 안된다고 알리는 알림을 호출하는 기능을 추가해야함.
    private int insertSchedule() {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm:ss");
        String titleName = title.getText().toString();
        long strDate = getLongDate(scheduleStrDate.getText().toString());
        long endDate = getLongDate(scheduleEndDate.getText().toString());

        String detailData = details.getText().toString();
        int periodData = period;
        int dateNum = calDate(scheduleStrDate.getText().toString(), scheduleEndDate.getText().toString());
        int index = 0;
        String data = "0";

        for (int i = 0; i < dateNum - 1; ++i) {
            data += "0";
        }

        Schedule schedule = new Schedule(titleName, strDate, endDate, detailData, periodData, dateNum, index, data);
        int id = scheduleViewModel.insertSchedule(schedule);
        println("등록과정 완료");
        return id;
    }

    private long getLongDate(String date) {
        date = date.replaceAll("[ :-]", "");
        return Long.parseLong(date);
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
}
