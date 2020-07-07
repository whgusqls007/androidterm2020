package com.example.androidterm2020;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidterm2020.OPEN_API_Task.AirTask;
import com.example.androidterm2020.OPEN_API_Task.ReceiveWeatherTask;
import com.example.androidterm2020.Receivers.Alarm_Receiver;
import com.example.androidterm2020.Receivers.RefreshDBReceiver;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;
import com.example.androidterm2020.Services.RefreshDBService;
import com.facebook.stetho.Stetho;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    ListView listView = null;
    Toolbar myToolbar;
    ListViewAdapter adapter;
    private GpsTracker gpsTracker;
    SharedPreferences prefs;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    double latitude;
    double longitude;
    String addressString;
    boolean settingInfo;
    private long backKeyPressedTime = 0;
    public static final int REFRESH_DB_CODE = 100000;

    String date;

    ScheduleViewModel scheduleViewModel;
    // 달력
    CalendarView calendar;
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        init();


        // 하루가 바뀌면 해야할 것이 2가지 있다.
        // 1. 지난날의 기록 삭제
        // 2. 오늘의 일정을 알람 매니저에 등록. -> 다중 알람 기능.
        // 그리고 폰을 껐다 킨 경우 해야할 것이 있다.
        // 1. 폰을 다시 키면서 알람을 다시 등록. // 끄고나면 알람이 사라지기 때문...
    }

    private void init() {
        Stetho.initializeWithDefaults(this);
        setScheduleViewModel();
        setGps();
        setToolbar();
        setCalendar();
        setListView(); // 문제의 라인
        RefreshDB();
    }

    private void setScheduleViewModel() {
        date = getTodayDate();
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
    }

    private void setGps() {
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        settingInfo = prefs.getBoolean("notify_on_off", true);
        // gps 관련

        /*SharedPreferences pref = getPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();*/
        gpsTracker = new GpsTracker(MainActivity.this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        addressString = getCurrentAddress(latitude, longitude);


        //Toast.makeText(MainActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();
    }

    private void setListView() {
        String[] AirData = GetAirData();//{"20", "30"};// GetAirData();
        String[] WeatherData = GetWeatherData(latitude, longitude);
        int pm25value = Integer.parseInt(!AirData[0].equals("-") ? AirData[0] : "0");
        int pm10value = Integer.parseInt(!AirData[1].equals("-") ? AirData[1] : "0");

        adapter = new ListViewAdapter();

        listView = (ListView)findViewById(R.id.drawer_menu);
        listView.setAdapter(adapter);

        //날씨 추가.
        adapter.addItem("전체 일정");
        adapter.addItem("환경설정");
        if(WeatherData[0].equals("Rain")) {
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.rainy), "비가 내림");
        }
        else if(WeatherData[0].equals("Snow")) {
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.snow), "눈이 내림");
        }
        else if(WeatherData[0].equals("Clouds")) {
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.cloudy), "흐림");
        }
        else if(WeatherData[0].equals("Clear")) {
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.sunnny), "맑음");
        }
        else if(WeatherData[0].equals("Drizzle")) {
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.drizzle_rain), "이슬비");
        }
        else if(WeatherData[0].equals("none")){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.xxx), "날씨API\n연결 실패");
        }
        else { // 잘 일어나지 않는 기상사건
            if(WeatherData[0].equals("Thunderstorm") || WeatherData[0].equals("Squall")) {
                adapter.addItem(ContextCompat.getDrawable(this, R.drawable.thunder_storm), "폭풍");
            }
            else if(WeatherData[0].equals("Mist")
                    || WeatherData[0].equals("Fog")
                    || WeatherData[0].equals("Haze")) { // 안개
                adapter.addItem(ContextCompat.getDrawable(this, R.drawable.fog), "안개");
            }
            else if(WeatherData[0].equals("Dust") || WeatherData[0].equals("Sand")) { // 먼지
                adapter.addItem(ContextCompat.getDrawable(this, R.drawable.dust), WeatherData[0]);
            }
            else if(WeatherData[0].equals("Smoke")) { // 연기
                adapter.addItem(ContextCompat.getDrawable(this, R.drawable.smog), WeatherData[0]);
            }
            else { // 나머지는 치명적임. 아마 목숨이 위험할 듯. 화산재, 토네이도 이런 부류...
                adapter.addItem(ContextCompat.getDrawable(this, R.drawable.skull), WeatherData[0]);
            }
        }


        if((pm25value > 0 && pm25value <= 15) || (pm25value > 0 && pm10value <= 30)){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.good), "미세먼지 없음");
        }
        else if((pm25value > 15 && pm25value <= 35) || (pm10value > 30 && pm10value < 80)){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.normal), "미세먼지 보통");
        }
        else if((pm25value > 35 && pm25value <= 75) || (pm10value > 80 && pm10value < 150)){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.gasmask), "미세먼지 나쁨");
        }
        else if((pm25value > 75) || (pm10value > 150)){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.skull), "미세먼지 매우 나쁨");
        }
        else if((pm25value == -1) && (pm10value == -1)){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.xxx), "미세먼지API\n연결 실패");
        }

        adapter.addItem("종료");
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                switch (position){
                    case 0 :
                        Intent scheduleIntent = new Intent(getApplicationContext(), ShowAllSchedule.class);
                        startActivity(scheduleIntent);
                        break;
                    case 1 :
                        Intent settingIntent = new Intent(getApplicationContext(), Setting.class);
                        startActivity(settingIntent);
                        break;
                    case 4:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("정말로 종료하시겠습니까?");
                        builder.setTitle("종료 알림창")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("종료 알림창");
                        alert.show();
                        break;
                }

                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
    }

    private void setToolbar() {
        myToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//기본버튼추가
        actionBar.setHomeAsUpIndicator(R.drawable.menu);//이미지 바꾸기, drawable에 이미지 추가 필요
    }

    private void setCalendar() {
        calendar = (CalendarView)findViewById(R.id.calendar);
        calendar.setWeekSeparatorLineColor(Color.BLACK);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int DayOfMonth) {
                date = year  + "-" + (month > 9 ? "" : "0") + (month+1) + "-" + (DayOfMonth > 9 ? "" : "0") + DayOfMonth;
                // 값으로 가져와야함.
                long newDate = getLongDate(date + "0000");
                int count = scheduleViewModel.getDateCountedSchedules(newDate);
                Intent intent = new Intent(getApplicationContext(), ScheduleRegistrationActivity.class);
                if(count > 0) {
                    intent = new Intent(getApplicationContext(), ShowDetail.class);
                }

                intent.putExtra("date", date);
                startActivity(intent);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                else{
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }
        if (addresses == null || addresses.size() == 0) {
            //Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("SetTextI18n")
    protected String[] GetWeatherData(double lan, double lon){
        String[] resultText = new String[5];

        try{
            resultText = new ReceiveWeatherTask(lan, lon).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return resultText;

    }

    protected String[] GetAirData() {
        String[] resultText = new String[2];
        try {
            AirTask task = new AirTask();
            resultText = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultText;
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
    }

    private long getLongDate(String date) {
        date = date.replaceAll("[ :-]", "");
        return Long.parseLong(date);
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();

        int year = cal.get( cal.YEAR );
        int month = cal.get( cal.MONTH ) + 1 ;
        int DayOfMonth = cal.get( cal.DATE ) ;

        String result = year
                + "-" + (month > 9 ? "" : "0") + month
                + "-" + (DayOfMonth > 9 ? "" : "0")
                + DayOfMonth;

        return result;
    }


    private void RefreshDB() { // DB에서 이미 종료일이 오늘보다 빠른 경우 지우고, 있는 친구들은 index를 증가시키는 걸 해야함.
        Calendar calendar = getClearedCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm");

        reserveRefreshDB(calendar);
    }

    private Calendar getClearedCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0); // 2020-06-20 00:00:00 으로 만드는 것이다.
        calendar.add(Calendar.DATE, 1); // 다음날로 설정
        return calendar;
    }

    @SuppressLint("ShortAlarm")
    void reserveRefreshDB(Calendar calendar) {
        Intent alarmIntent = new Intent(this, RefreshDBReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REFRESH_DB_CODE, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        if(alarmManager != null) {
            //long INTERVAL = 1000 * 60 * 60 * 24;
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent); // 화면을 보여줄 필요는 없어서 WAKE는 안함.

                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        // 기존 뒤로가기 버튼의 기능을 막기위해 주석처리 또는 삭제
        // super.onBackPressed();

        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
        // 2000 milliseconds = 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("정말로 종료하시겠습니까?");
            builder.setTitle("종료 알림창")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("종료 알림창");
            alert.show();
            return;
        }
    }
}