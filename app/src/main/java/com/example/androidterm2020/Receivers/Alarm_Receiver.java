package com.example.androidterm2020.Receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.androidterm2020.OPEN_API_Task.AirTask;
import com.example.androidterm2020.OPEN_API_Task.ReceiveWeatherTask;
import com.example.androidterm2020.GpsTracker;
import com.example.androidterm2020.MainActivity;
import com.example.androidterm2020.R;
import com.example.androidterm2020.RoomDB.AlarmViewModel;
import com.example.androidterm2020.RoomDB.RoomDatabase;
import com.example.androidterm2020.RoomDB.RoomDatabaseAccessor;
import com.example.androidterm2020.RoomDB.Schedule;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;

import java.util.concurrent.ExecutionException;

public class Alarm_Receiver extends BroadcastReceiver {
    GpsTracker gpsTracker;
    String[] WeatherData = new String[5];
    String[] Air = new String[2];
    Alarm_Receiver context = this;
    AlarmViewModel alarmViewModel;
    ScheduleViewModel scheduleViewModel;
    RoomDatabase roomDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 날씨가 마스크 알림에 덮혀 씹힌다. 2개의 알림을 동시에 볼 수 있게 해야함.
        roomDatabase = RoomDatabaseAccessor.getInstance(context);
        Schedule schedule = getTargetSchedule(intent);
        NotifyWeather(context, schedule.getTitle(), getTime(schedule.getStrDate()));
        //NotifyMask(context);
        //showCustomLayoutNotification(context);
    }

    private Schedule getTargetSchedule(Intent intent) {
        int aid = intent.getIntExtra("aid", 0);
        Schedule result = null;
        try {
            int sid = new AlarmViewModel.getScheduleIdByAlarmIdAsyncTask(roomDatabase.alarmDao()).execute(aid).get();
            result = new ScheduleViewModel.getScheduleByIdAsyncTask(roomDatabase.scheduleDao()).execute(sid).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected void NotifyWeather(Context context, String title, String time) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");


        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남


            String channelName ="매일 알람 채널";
            String description = "매일 정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        // 여기에 air정보도 같이 표시하도록 만들기.
        gpsTracker = new GpsTracker(context);
        double lan = gpsTracker.getLatitude();
        double lon = gpsTracker.getLongitude();
        WeatherData = GetWeatherData(lan, lon);
        Air = GetAirData();

        int pm25value = Integer.parseInt(Air[0]);
        int pm10value = Integer.parseInt(Air[1]);

        // 레이아웃의 값을 설정한다.
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_notification);
        remoteViews.setTextViewText(R.id.scheduleTime, time); // 실험 대상.
        remoteViews.setTextViewText(R.id.ScheduleTitle, title); // 실험 대상.
        remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.sunnny); // 강우확률에 따라서 이미지를 바꿀 필요가 있다.
        if(WeatherData[0].equals("Rain")) {
            remoteViews.setImageViewResource(R.id.weatherImg,  R.drawable.rainy);
        }
        else if(WeatherData[0].equals("Snow")) {
            remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.snow);
        }
        else if(WeatherData[0].equals("Clouds")) {
            remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.cloudy);
        }
        else if(WeatherData[0].equals("Clear")) {
            remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.sunnny);
        }
        else if(WeatherData[0].equals("Drizzle")) {
            remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.drizzle_rain);
        }
        else if(WeatherData[0].equals("none")){
            remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.xxx);
        }
        else { // 잘 일어나지 않는 기상사건
            if(WeatherData[0].equals("Thunderstorm") || WeatherData[0].equals("Squall")) {
                remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.thunder_storm);
            }
            else if(WeatherData[0].equals("Mist")
                    || WeatherData[0].equals("Fog")
                    || WeatherData[0].equals("Haze")) { // 안개
                remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.fog);
            }
            else if(WeatherData[0].equals("Dust") || WeatherData[0].equals("Sand")) { // 먼지
                remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.dust);
            }
            else if(WeatherData[0].equals("Smoke")) { // 연기
                remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.smog);
            }
            else { // 나머지는 치명적임. 아마 목숨이 위험할 듯. 화산재, 토네이도 이런 부류...
                remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.skull);
            }
        }
        if(pm25value <= 15 || pm10value <= 30){ // 코드를 깔끔하게 만들 필요가 있다.
            remoteViews.setImageViewResource(R.id.airImg, R.drawable.good);
        }
        else if((pm25value > 15 && pm25value <= 35) || (pm10value > 30 && pm10value < 80)){
            remoteViews.setImageViewResource(R.id.airImg, R.drawable.normal);
        }
        else if((pm25value > 35 && pm25value <= 75) || (pm10value > 80 && pm10value < 150)){
            remoteViews.setImageViewResource(R.id.airImg, R.drawable.gasmask);
        }
        else if((pm25value > 75) || (pm10value > 150)){
            remoteViews.setImageViewResource(R.id.airImg, R.drawable.skull);
        }
        else if((pm25value == -1) && (pm10value == -1)){
            remoteViews.setImageViewResource(R.id.airImg, R.drawable.xxx);
        }
        remoteViews.setTextViewText(R.id.minDegree, WeatherData[3]);
        remoteViews.setTextViewText(R.id.maxDegree, WeatherData[4]);
        remoteViews.setTextViewText(R.id.pm25, Air[0]);
        remoteViews.setTextViewText(R.id.pm10, Air[1]);
        builder.setAutoCancel(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteViews) // 나중에 기본형을 줄이고 여기 사용한 것을 확장형에 넣을 필요가 있다.
                .setContentIntent(pendingI)
                .build();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean allow_alarm = prefs.getBoolean("notify_on_off", false);
        if (notificationManager != null && allow_alarm) {
            // 노티피케이션 동작시킴
            notificationManager.notify(1234, builder.build());
        }
    }

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

    protected String[] GetAirData(){
        String[] resultText = new String[2];
        try{
            resultText = new AirTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultText;

    }

    private String getTime(long strDate) { // 20200623 1122 에서 1122만 가져온다.
        String result = "";

        for(int i=0; i<4; ++i) {
            result += (strDate%10); // 2211
            strDate /= 10;
            if(i == 1) {
                result += ":";
            }
        }
        result = (new StringBuffer(result)).reverse().toString(); // 11:22

        return result;
    }

}
/*public class Alarm_Receiver extends BroadcastReceiver {
    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";
    NotificationManager manager;
    NotificationCompat.Builder builder;
    GpsTracker gpsTracker;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // intent로부터 전달받은 string
        String get_yout_string = intent.getExtras().getString("state");
        // RingtonePlayingService 서비스 intent 생성
        //Intent service_intent = new Intent(context, RingtonePlayingService.class);
        // RingtonePlayinService로 extra string값 보내기
        //service_intent.putExtra("state", get_yout_string);
        // start the ringtone service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this.context, MainActivity.class);
        PendingIntent pendingI = PendingIntent.getActivity(this.context, 0,
                notificationIntent, 0);
        gpsTracker = new GpsTracker(this.context);
        double Lan = gpsTracker.getLatitude();
        double Lon = gpsTracker.getLongitude();
        String[] Weather_data = new String[5];
        Weather_data = GetWeatherData(Lan, Lon);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남


            String channelName ="매일 알람 채널";
            String description = "매일 정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
        Intent intent2 = new Intent(this.context, MainActivity.class);
        //intent.putExtra("name",name);
        PendingIntent pending = PendingIntent.getActivity(this.context, 101, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //알림창 제목
        //알림창 메시지
        builder.setContentTitle("오늘의 날씨 : " + Weather_data[0])
                .setContentText("최고온도 : " + Weather_data[4] + ", 최저온도 : " + Weather_data[3])
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pending)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSound(soundUri);
        manager.notify(0, builder.build());
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.startForegroundService(service_intent);
        }else{
            this.context.startService(service_intent);
        }*/
    /*}
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
}*/
