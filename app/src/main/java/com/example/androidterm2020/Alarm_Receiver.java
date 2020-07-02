package com.example.androidterm2020;

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

import com.example.androidterm2020.API_Task.AirTask;
import com.example.androidterm2020.API_Task.ReceiveWeatherTask;

import java.util.concurrent.ExecutionException;

public class Alarm_Receiver extends BroadcastReceiver {
    GpsTracker gpsTracker;
    String[] Weather = new String[5];
    String[] Air = new String[2];
    Alarm_Receiver context = this;
    @Override
    public void onReceive(Context context, Intent intent) {
        // 날씨가 마스크 알림에 덮혀 씹힌다. 2개의 알림을 동시에 볼 수 있게 해야함.
        NotifyWeather(context);
        //NotifyMask(context);
        //showCustomLayoutNotification(context);
    }

    protected void NotifyWeather(Context context) {
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
        Weather = GetWeatherData(lan, lon);
        Air = GetAirData();

        int pm25value = Integer.parseInt(Air[0]);
        int pm10value = Integer.parseInt(Air[1]);

        // 레이아웃의 값을 설정한다.
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_notification);
        remoteViews.setImageViewResource(R.id.weatherImg, R.drawable.sunnny); // 강우확률에 따라서 이미지를 바꿀 필요가 있다.
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
        remoteViews.setTextViewText(R.id.minDegree, Weather[3]);
        remoteViews.setTextViewText(R.id.maxDegree, Weather[4]);
        remoteViews.setTextViewText(R.id.pm25, Air[0]);
        remoteViews.setTextViewText(R.id.pm10, Air[1]);
        builder.setAutoCancel(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteViews) // 나중에 기본형을 줄이고 여기 사용한 것을 확장형에 넣을 필요가 있다.
                .setContentIntent(pendingI)
                .build();
//        builder.setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setTicker("{Time to watch some cool stuff!}")
//                .setContentTitle("오늘의 날씨 : " + Weather[0]) // 제목
//                .setContentText("최고온도 : " + Weather[4] + ", 최저온도 : " + Weather[3] ) // 내용
//                .setStyle(new NotificationCompat.BigTextStyle().bigText("대기상태 : " + getAirQuality() + "\n + pm25 : " + Air[0] + ", pm10 : " + Air[1]))
//                .setContentInfo("INFO")
//                .setContentIntent(pendingI);
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

    protected void NotifyMask(Context context) {
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

            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", "name", notificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        // 여기에 air정보도 같이 표시하도록 만들기.
        Air = GetAirData();
        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())

                .setTicker("{Time to watch some cool stuff!}")
                .setSmallIcon(R.drawable.mask)
                .setContentTitle("마스크를 착용하는 것이 권장됩니다.") // 제목
                .setContentText("초미세먼지 : " + Air[0] + ", 미세먼지 : " + Air[1]) // 내용
                .setContentInfo("INFO")
                .setContentIntent(pendingI);

        if (notificationManager != null) {

            // 노티피케이션 동작시킴
            //notificationManager.notify(1234, builder.build());

//            Calendar nextNotifyTime = Calendar.getInstance();
//
//            // 내일 같은 시간으로 알람시간 결정
//            nextNotifyTime.add(Calendar.DATE, 1);
//
//            //  Preference에 설정한 값 저장
//            SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
//            editor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
//            editor.apply();
//
//            Date currentDateTime = nextNotifyTime.getTime();
//            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
//            Toast.makeText(context.getApplicationContext(),"다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
        }
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

    protected String getAirQuality() {
        int pm25value = Integer.parseInt(Air[0]);
        int pm10value = Integer.parseInt(Air[1]);

        String result = "";
        if((pm25value > 35) || (pm10value > 80)) {
            result = "경고";
        }
        else {
            result = "양호";
        }

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
