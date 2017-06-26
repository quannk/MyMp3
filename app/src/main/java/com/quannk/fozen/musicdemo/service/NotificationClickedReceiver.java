package com.quannk.fozen.musicdemo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.quannk.fozen.musicdemo.AppConst;

/**
 * Created by QuanNguy on 25/05/2017.
 */

public class NotificationClickedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NCR","aaaaa");
        Bundle bundle=intent.getExtras();
        int flag = bundle.getInt("command",0);
        switch (flag) {
            case 1:
                Intent i = new Intent(AppConst.NOTIFICATION_NEXT);
                context.sendBroadcast(i);
                Log.i("NotificationReceive","clicked");
                break;
            case 2:
                Intent i2 = new Intent(AppConst.NOTIFICATION_PAUSE);
                context.sendBroadcast(i2);
                Log.i("NotificationReceive","clicked");
                break;
            case 3:
                Intent i3 = new Intent(AppConst.NOTIFICATION_PREVIOUS);
                context.sendBroadcast(i3);
                Log.i("NotificationReceive","clicked");
                break;
            default:
                break;
        }
    }
}



