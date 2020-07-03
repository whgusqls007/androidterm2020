package com.example.androidterm2020.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.androidterm2020.Services.RefreshDBService;

public class RefreshDBReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, RefreshDBService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 매일 자정이 지나면 작업.
            context.startForegroundService(i); // Android 8 이상부터
        }
        else {
            context.startService(i);
        }

    }
}
