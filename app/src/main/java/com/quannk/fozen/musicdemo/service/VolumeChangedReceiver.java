package com.quannk.fozen.musicdemo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Created by NguyenNgocAnh on 05/15/2017.
 */

public class VolumeChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int volumeLevel = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(2);
        Intent i = new Intent("change seek bar");
        i.putExtra("volume level", volumeLevel);
        context.sendBroadcast(i);
    }
}
