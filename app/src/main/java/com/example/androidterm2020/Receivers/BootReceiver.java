package com.example.androidterm2020.Receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.androidterm2020.MainActivity;
import com.example.androidterm2020.Services.BootService;


public class BootReceiver extends BroadcastReceiver { // 리시버에 구현.

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootReceiver", "실행준비");
//        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
//            Intent it = new Intent(context, MainActivity.class);
//            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(it);
//        }
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, BootService.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(i); // Android 8 이상부터
            }
            else {
                context.startService(i);
            }
        }
    }
}
