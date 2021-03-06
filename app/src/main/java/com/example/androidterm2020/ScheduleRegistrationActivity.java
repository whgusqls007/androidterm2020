package com.example.androidterm2020;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.androidterm2020.Receivers.Alarm_Receiver;
import com.example.androidterm2020.RoomDB.Alarm;
import com.example.androidterm2020.RoomDB.AlarmViewModel;
import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

// 모든 값을 입력하지 않았을 경우 스넥바 보여주기,
public class ScheduleRegistrationActivity extends AppCompatActivity {
    EditText title;
    TextView scheduleStrDate;
    TextView scheduleEndDate;
    TextView scheduleStrTime;
    TextView scheduleEndTime;
    EditText details;
    CheckBox checkBox;
    CheckBox checkBox2;
    CheckBox checkBox3;
    Button button;
    Button registrationBtn;
    Button selectBtn;
    int period = 0; // 나중에 checkbox와 연동되도록 코드를 추가해주자.
    TextView testLog;
    // 달력에서 날짜 누르고 여기 올때 자동으로 날짜 + 시간을 추가해주는 기능.
    String start_date;
    boolean allow_alarm = true;
    Switch switchButton;


    private ScheduleViewModel scheduleViewModel;
    private AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_registeration_activity);


        Intent intent = getIntent();
        init(intent.getStringExtra("date"));


    }

    private void init(String datetime) {
        initUI(datetime);
        setCheckBoxes();
        initViewModels();
        initButtons(datetime);
        initSwitch();
    }

    private boolean is_registrationSafe(){
        int result = title.getText().toString().length() * scheduleEndTime.getText().toString().length()
                * scheduleStrTime.getText().toString().length() * scheduleEndDate.getText().toString().length()
                * scheduleStrDate.getText().toString().length() * details.getText().toString().length();
        return result > 0 ? true : false;
    }
    private void initButtons ( final String datetime){

        registrationBtn = (Button) findViewById(R.id.regBtn);
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 나중에 메인으로 돌아가도록 기능도 추가해야함. 테스트용으로 일단 작성.
                // DB도 일단 텍스트 테이터 등록하는 것만 일단 구현. 추후 체크박스와 연동해서 데이터가 설정되도록 변경예정. -> 연동완료

                // 데이터를 추가함.

                if(is_registrationSafe()){
                    int id = insertSchedule();
                    Intent finishIntent = new Intent();
                    setResult(getIntent().getIntExtra("position", 0), finishIntent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"값이 다 입력되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initViewModels() {
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
    }

    private void initUI(String datetime) {
        title = (EditText) findViewById(R.id.editTitle);
        scheduleStrDate = (TextView) findViewById(R.id.editScheduleStrDate);
        scheduleEndDate = (TextView) findViewById(R.id.editScheduleEndDate);
        scheduleStrTime = (TextView) findViewById(R.id.editScheduleStrTime);
        scheduleEndTime = (TextView) findViewById(R.id.editScheduleEndTime);
        details = (EditText) findViewById(R.id.editDetails);

        start_date = scheduleStrDate.getText().toString();


        final Calendar myCalendar = Calendar.getInstance();

        scheduleStrDate.setText(datetime);

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
                new DatePickerDialog(ScheduleRegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                new DatePickerDialog(ScheduleRegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        scheduleEndDate.setText(year + "-" + ((month+1) > 9 ? "" : "0")+ (month+1) + "-" + ((dayOfMonth) > 9 ? "" : "0")+ dayOfMonth);
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TextView start_time = (TextView) findViewById(R.id.editScheduleStrTime);
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ScheduleRegistrationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        start_time.setText(((selectedHour) > 9 ? "" : "0")+ selectedHour + ":" + (selectedMinute > 9 ? "" : "0")+ selectedMinute);
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        final TextView end_time = (TextView) findViewById(R.id.editScheduleEndTime);
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ScheduleRegistrationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        end_time.setText(((selectedHour) > 9 ? "" : "0")+ selectedHour + ":" + (selectedMinute > 9 ? "" : "0")+ selectedMinute);
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }


    private void setCheckBoxes() {
        LinearLayout select_linear = (LinearLayout)findViewById(R.id.select);
        select_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
    }


    private String getCurrentTime() {
        long now = System.currentTimeMillis(); // 현재시간 가져옴.
        Date mDate = new Date(now); // Date 타입으로 바꿈.
        SimpleDateFormat simpleDate = new SimpleDateFormat("hh:mm"); // yyyy-MM-dd hh:mm:ss가 datetime 타입에 딱 알맞다.
        String getTime = simpleDate.format(mDate);

        return getTime;
    }

    @SuppressLint("SetTextI18n")
    // 하나라도 안적혀있으면 등록이 안된다고 알리는 알림을 호출하는 기능을 추가해야함.
    private int insertSchedule() {

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        String titleName = title.getText().toString();
        long strDate = getLongDate(scheduleStrDate.getText().toString() + " " + scheduleStrTime.getText().toString());
        long endDate = getLongDate(scheduleEndDate.getText().toString() + " " + scheduleEndTime.getText().toString());

//        getDatetimeData(scheduleStrDate.getText().toString() + " " + scheduleStrTime.getText().toString());

        String detailData = details.getText().toString();
        int periodData = period;
        int dateNum = calDate(scheduleStrDate.getText().toString() + " " + scheduleStrTime.getText().toString(), scheduleEndDate.getText().toString() + " " + scheduleEndTime.getText().toString());
        int index = 0;
        String data = "0";

        for (int i = 0; i < dateNum - 1; ++i) {
            data += "0";
        }

        Schedule schedule = new Schedule(titleName, strDate, endDate, detailData, periodData, dateNum, index, data);
        int id = scheduleViewModel.insertSchedule(schedule);

        Alarm alarm = new Alarm(id);
        if(allow_alarm) {
            int start_hour = Integer.parseInt(scheduleStrTime.getText().toString().substring(0, 2));
            int start_min = Integer.parseInt(scheduleStrTime.getText().toString().substring(3, 5));
            int start_year = Integer.parseInt(scheduleStrDate.getText().toString().substring(0, 4));
            int start_month = Integer.parseInt(scheduleStrDate.getText().toString().substring(5, 7));
            int start_day = Integer.parseInt(scheduleStrDate.getText().toString().substring(8, 10));
            GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(start_year, start_month - 1, start_day + 1, start_hour - 2, start_min, 0);
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(GregorianCalendar.YEAR, 1);
                calendar.add(GregorianCalendar.DATE, -1);
                // start_day + 1 된거랑 start_day - 1 된거 나중에 지워야함..
                Toast.makeText(ScheduleRegistrationActivity.this, "내년으로 설정", Toast.LENGTH_LONG).show();
            }
            calendar.add(GregorianCalendar.DATE, -1);
            diaryNotification(calendar, alarmViewModel.insertAlarm(alarm));
        }



        return id;
    }
   // 시험용 잘 동작함.
//    private void getDatetimeData(String datetime) {
//        // 0123456789012345
//
//        int[] datetimeData = new int[5];
//        datetimeData[0] = Integer.parseInt(datetime.substring(0,4)); // year
//        datetimeData[1] = Integer.parseInt(datetime.substring(5,7)); // month
//        datetimeData[2] = Integer.parseInt(datetime.substring(8,10)); // day
//        datetimeData[3] = Integer.parseInt(datetime.substring(11,13)); // hour
//        datetimeData[4] = Integer.parseInt(datetime.substring(14,16)); // minute
//    }

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
    @SuppressLint("ShortAlarm")
    void diaryNotification(Calendar calendar, int alarm_requestCode) {
        Intent alarmIntent = new Intent(this, Alarm_Receiver.class);
        alarmIntent.putExtra("aid", alarm_requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm_requestCode, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SharedPreferences preference = getPreferences(this);
        SharedPreferences.Editor editor = preference.edit();
        SharedPreferences getprefrence = getPreferences(this);
        while (true) {
            int checkcode = getprefrence.getInt(Integer.toString(alarm_requestCode), -1);
            if (checkcode == -1) {
                break;
            } else {
                alarm_requestCode = alarm_requestCode + 1;
            }
        }
        editor.putInt(Integer.toString(alarm_requestCode), alarm_requestCode);
        editor.apply();

        if (period == 0) { // 반복없음.
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        } else if (period == 1) { // 매일
            long INTERVAL = (long)1000 * 60 * 60 * 24;
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                }
            }
        } else if (period == 2) { // 격일
            long INTERVAL = (long)1000 * 60 * 60 * 24 * 2;
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                }
            }

        } else if (period == 3) { // 1주
            long INTERVAL = (long)1000 * 60 * 60 * 24 * 7;
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, pendingIntent);
                }
            }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
        }
    }
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item ){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void show()
    {
        final CharSequence[] oItems = {"없음", "매일", "격일", "매주"};
        int position = period;


        AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

        oDialog.setTitle("원하는 주기를 선택하세요")
                .setSingleChoiceItems(oItems, position, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        TextView cycle = (TextView) findViewById(R.id.select_view);
                        cycle.setText(oItems[which]);
                        period = which;
                    }
                })
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();

    }

    private void initSwitch(){
        switchButton = (Switch)findViewById(R.id.notify_switch);
        final LinearLayout noti_select_linear = (LinearLayout)findViewById(R.id.select);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    switchButton.setText("알람 사용");
                    noti_select_linear.setEnabled(true);
                    allow_alarm = true;
                }
                else{
                    switchButton.setText("알람 사용 안함");
                    noti_select_linear.setEnabled(false);
                    allow_alarm = false;
                }
            }
        });
    }
}
