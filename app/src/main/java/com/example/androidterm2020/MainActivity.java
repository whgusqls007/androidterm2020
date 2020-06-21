package com.example.androidterm2020;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean settinginfo = prefs.getBoolean("notify_on_off", true);
        // gps 관련
        final TextView textview_address = (TextView)findViewById(R.id.textview);
        final EditText textview_Latitude = (EditText)findViewById(R.id.Ed1);
        final EditText textview_Longitude = (EditText)findViewById(R.id.Ed2);
        /*SharedPreferences pref = getPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();*/
        gpsTracker = new GpsTracker(MainActivity.this);
        final double latitude = gpsTracker.getLatitude();
        final double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        if(settinginfo) {
            textview_address.setText("True");
        }else{
            textview_address.setText("False");
        }
        textview_Latitude.setText(Double.toString(latitude));
        textview_Longitude.setText(Double.toString(longitude));

        Toast.makeText(MainActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();
        GetWeatherData(latitude, longitude);
        Button ShowLocationButton = (Button) findViewById(R.id.button);
        /*ShowLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetAlarm.class);
                startActivity(intent);
            }*/
            /*@SuppressLint("SetTextI18n")
            @Override
            public void onClick(View arg0)
            {

                gpsTracker = new GpsTracker(MainActivity.this);

                final double latitude = gpsTracker.getLatitude();
                final double longitude = gpsTracker.getLongitude();

                String address = getCurrentAddress(latitude, longitude);
                textview_address.setText(address);
                textview_Latitude.setText(Double.toString(latitude));
                textview_Longitude.setText(Double.toString(longitude));

                Toast.makeText(MainActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();
                GetWeatherData(latitude, longitude);
            }
        });*/

        myToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//기본버튼추가
        actionBar.setHomeAsUpIndicator(R.drawable.menu);//이미지 바꾸기, drawable에 이미지 추가 필요

        CalendarView calendar = (CalendarView)findViewById(R.id.calendar);
        calendar.setWeekSeparatorLineColor(Color.BLACK);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int DayOfMonth) {
                String date = year  + "-" + (month > 9 ? "" : "0") + (month+1) + "-" + (DayOfMonth > 9 ? "" : "0") + DayOfMonth;
                Cursor cursor = getContentResolver().query(ScheduleProvider.CONTENT_URI, DBHelper.ALL_COLUMNS, "date(" + DBHelper.SCHEDULE_START_DATE + ") = ?", new String[] {date}, DBHelper.SCHEDULE_START_DATE + " ASC");
                Intent intent = new Intent(getApplicationContext(), ScheduleRegistrationActivity.class);

                if (cursor.getCount() > 0) {
                    intent = new Intent(getApplicationContext(), ShowDetail.class);
                }

                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

        // 임시버튼들, 나중에 다 지워야함. xml도 수정해야함.
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

        // 사이드 매뉴 담당
        adapter = new ListViewAdapter();

        listView = (ListView)findViewById(R.id.drawer_menu);
        listView.setAdapter(adapter);

        //유동적으로 추가 가능
        adapter.addItem("전체 일정");
        adapter.addItem("환경설정");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.sunnny), "맑음");
        //adapter.addItem(ContextCompat.getDrawable(this, R.drawable.gasmask), "나쁨");
        //먼지에 따라 그림이 바뀐다.
        String[] AirData = GetAirData();
        int pm25value = Integer.parseInt(AirData[0]);
        int pm10value = Integer.parseInt(AirData[1]);

        if(pm25value <= 15 || pm10value <= 30){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.good), "미세먼지 없음");
        }
        else if((pm25value > 15 && pm25value <= 35) || (pm10value > 30 && pm10value < 80)){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.normal), "미세먼지 보통");
        }
        else if((pm25value > 35 && pm25value <= 75) || (pm10value > 80 && pm10value < 150)){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.gasmask), "미세먼지 나쁨");
            MaskNotification();
        }
        else if((pm25value > 75) || (pm10value > 150)){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.skull), "미세먼지 매우 나쁨");
            MaskNotification();
        }

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
                }

                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
        // 하루가 바뀌면 해야할 것이 2가지 있다.
        // 1. 지난날의 기록 삭제
        // 2. 오늘의 일정을 알람 매니저에 등록. -> 다중 알람 기능.
        // 그리고 폰을 껐다 킨 경우 해야할 것이 있다.
        // 1. 폰을 다시 키면서 알람을 다시 등록. // 끄고나면 알람이 사라지기 때문...
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
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
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
    protected void GetWeatherData(double lan, double lon){
        String[] resultText = new String[5];
        TextView weather = (TextView)findViewById(R.id.weather);
        TextView weather2 = (TextView)findViewById(R.id.weather2);
        TextView weather3 = (TextView)findViewById(R.id.weather3);
        try{
            resultText = new ReceiveWeatherTask(lan, lon).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        weather.setText(resultText[0].toString());
        weather2.setText(resultText[1].toString());
        weather3.setText("현재온도 : " + resultText[2] + ", 최고온도 : " + resultText[4] + ", 최저온도 : " + resultText[3]);

    }

    protected String[] GetAirData() {
        String[] resultText = new String[2];
        try {
            resultText = new AirTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultText;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void MaskNotification() {

        Bitmap mLargeIconForNoti = BitmapFactory.decodeResource(getResources(),R.drawable.mask);

        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0,
                new Intent(getApplicationContext(),MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(MainActivity.this, "CHANNEL_ID")
                        .setSmallIcon(R.drawable.mask)
                        .setContentTitle("마스크 챙기세요!")
                        .setContentText("미세먼지가 심해요!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setLargeIcon(mLargeIconForNoti)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(contentIntent);

        NotificationChannel channel = new NotificationChannel("CHANNEL_ID", "name", nm.IMPORTANCE_DEFAULT);
        channel.setDescription("description");
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this

        nm.createNotificationChannel(channel);
        nm.notify(5678, builder.build());
    }
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
    }
}