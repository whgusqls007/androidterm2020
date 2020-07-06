package com.example.androidterm2020.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.androidterm2020.Services.BootService;

public class BootReceiver extends BroadcastReceiver { // 리시버에 구현.
    @Override
    public void onReceive(Context context, Intent intent) { // 내일하자.~~~
        Intent i = new Intent(context, BootService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i); // Android 8 이상부터
        }
        else {
            context.startService(i);
        }

    }
}
