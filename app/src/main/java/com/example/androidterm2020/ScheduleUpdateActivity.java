package com.example.androidterm2020;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleUpdateActivity extends AppCompatActivity {
    EditText title;
    TextView scheduleStrDate;
    TextView scheduleEndDate;
    EditText details;
    int period = 0; // 나중에 checkbox와 연동되도록 코드를 추가해주자.
    TextView testLog;
    Schedule schedule;

    private ScheduleViewModel scheduleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_registeration_activity);
        Intent intent = getIntent();
        final int ID = intent.getIntExtra("sid", 0);

        Button regBtn = (Button) findViewById(R.id.regBtn);
        regBtn.setText("수정된 정보 업데이트");
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        title = (EditText) findViewById(R.id.editTitle);
        scheduleStrDate = (TextView) findViewById(R.id.editScheduleStrDate);
        scheduleEndDate = (TextView) findViewById(R.id.editScheduleEndDate);
        details = (EditText) findViewById(R.id.editDetails);
        testLog = (TextView) findViewById(R.id.logTxt); // 임시로 이용하는 로그 비슷한 기능담당할 놈.

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        final CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);

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

        SimpleDateFormat dateToString = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        // 값이 있으면 기존의 값을 입력해즌다.

        schedule = scheduleViewModel.getScheduleById(ID);
        title.setText(schedule.getTitle());
        details.setText(schedule.getDetails());
        period = schedule.getPeriod();

        String strDate = getDateTime(schedule.getStrDate());
        String endDate = getDateTime(schedule.getEndDate());
        scheduleStrDate.setText(strDate);
        scheduleEndDate.setText(endDate);

        if (period == 0) {
            checkBox.setChecked(true);
        }
        else if (period == 1) {
            checkBox2.setChecked(true);
        }
        else {
            checkBox3.setChecked(true);
        }


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

        Button updateBtn = (Button) findViewById(R.id.regBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() { // update버튼.
            @Override
            public void onClick(View v) {
                // 업데이트 쿼리문.
                updateSchedule();
                finish();
            }
        });

        Button button = (Button) findViewById(R.id.aa);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), PickDActivity.class);
                intent2.putExtra("cur_date", getDateTime(schedule.getStrDate()));
                startActivityForResult(intent2, 101);
            }
        });
    }

    public void updateSchedule() {
        schedule.setAchievementData(updateAchievementData());
        schedule.setTitle(title.getText().toString());
        schedule.setStrDate(getLongDate(scheduleStrDate.getText().toString()));
        schedule.setEndDate(getLongDate(scheduleEndDate.getText().toString()));
        schedule.setDetails(details.getText().toString());
        schedule.setPeriod(period);


        schedule.setDateNum(calDate(scheduleStrDate.getText().toString(), scheduleEndDate.getText().toString()));

        scheduleViewModel.updateSchedule(schedule);
    }

    private String updateAchievementData() {
        int newDateNum = calDate(scheduleStrDate.getText().toString(), scheduleEndDate.getText().toString());
        String data = "";
        if(schedule.getAchievementData().length() < newDateNum) { // 날의 일 수 가 증가하면
            // 1. 시작날짜 그대로일 경우
            // 2. 시작일이 변한경우
            data = schedule.getAchievementData();
            int dateNum = schedule.getDateNum();
            if(schedule.getStrDate() == getLongDate(scheduleStrDate.getText().toString())) { // 시작일 그대로면 바로 뒤에 0을 추가함 증가한 만큼 111 -> 111000
                for (int i = 0; i < (newDateNum-dateNum); ++i) {
                    data += "0";
                }
            }
            else { // 초기화 하고 DateNum 만큼 만들어준다.
                data = "0";
                for (int i = 0; i < newDateNum -1; ++i) {
                    data += "0";
                }
            }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {

            String date = data.getStringExtra("date");
            TextView textView = (TextView) findViewById(R.id.editScheduleEndDate);
            textView.setText(date + ' ' + getCurrentTime());
        }
    }

    private String getCurrentTime() {
        long now = System.currentTimeMillis(); // 현재시간 가져옴.
        Date mDate = new Date(now); // Date 타입으로 바꿈.
        SimpleDateFormat simpleDate = new SimpleDateFormat("hh:mm"); // yyyy-MM-dd hh:mm:ss가 datetime 타입에 딱 알맞다.
        String getTime = simpleDate.format(mDate);

        return getTime;
    }
}
