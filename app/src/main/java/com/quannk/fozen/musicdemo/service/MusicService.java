package com.quannk.fozen.musicdemo.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.quannk.fozen.musicdemo.AppConst;
import com.quannk.fozen.musicdemo.activity.Myapplication;
import com.quannk.fozen.musicdemo.R;
import com.quannk.fozen.musicdemo.database.DatabaseHelper;
import com.quannk.fozen.musicdemo.fragment.PlaySongFragment;
import com.quannk.fozen.musicdemo.item.ItemSong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Integer.parseInt;

/**
 * Create
 * by Foze on 26/04/2017.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, Runnable {
    public static final int SONG_UPDATE = 100;
    public static final int SONG_FINISH = 10;
    private static final int NOTIFICATION_ID = 1234;
    public static MediaPlayer mediaPlayer;
    private ArrayList<ItemSong> list;
    private Myapplication myapplication;
    private DatabaseHelper databaseHelper;
    private int position = 0;
    private BroadcastReceiver broadcastReceiver;
    private boolean isServiceRunning = false;
    private int state;
    private Handler handler;
    private NotificationCompat.Builder builder;
    private Random rd;
    private Thread thread;
    private int currentPosition;




    @Override
    public boolean onUnbind(Intent intent) {
//        mediaPlayer.stop();
//        mediaPlayer.release();
//        Log.i("UNBIND", "Unbind");
        return super.onUnbind(intent);
    }


    public void onCreate() {
        this.myapplication = (Myapplication) getApplication();
        databaseHelper = myapplication.getDB();
        list = new ArrayList<>();
        list = databaseHelper.getAllSongs();
        initMedia();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConst.NEXT);
        intentFilter.addAction(AppConst.PAUSE);
        intentFilter.addAction(AppConst.PREVIOUS);
        intentFilter.addAction(AppConst.RESUME);
        intentFilter.addAction(AppConst.RANDOM);
        intentFilter.addAction(AppConst.PLAY_OTHER_MUSIC);
        intentFilter.addAction(AppConst.NOTIFICATION_NEXT);
        intentFilter.addAction(AppConst.NOTIFICATION_PAUSE);
        intentFilter.addAction(AppConst.NOTIFICATION_PREVIOUS);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case AppConst.PAUSE:
                        if (state == AppConst.STATE_PLAYING) {
                            pauseMusic();
                            state = AppConst.STATE_PAUSED;
                        } else {
                            resumeSong();
                            state = AppConst.STATE_PLAYING;
                        }
                        break;
                    case AppConst.PREVIOUS:
                        previousSong();
                        break;
                    case AppConst.NEXT:
                        nextSong();
                        break;
                    case AppConst.RESUME:
                        resumeSong();
                        break;
                    case AppConst.RANDOM:
                        randomSong();
                        break;
                    case AppConst.PLAY_OTHER_MUSIC:
                        int newPosition = intent.getIntExtra(AppConst.NEW_POSITION, -1);
                        if (position != newPosition) {
                            stopSong();
                            playSong(newPosition);
                            Intent i = new Intent(AppConst.NEW_SONG);
                            i.putExtra("new", newPosition);
                            context.sendBroadcast(i);
                            mediaPlayer.setOnPreparedListener(MusicService.this);
                            position = newPosition;
                        } else {
                            resumeSong();
                        }
                        break;
                    case AppConst.NOTIFICATION_NEXT:
                        nextSong();
                        break;
                    case AppConst.NOTIFICATION_PAUSE:
                        if (state == AppConst.STATE_PLAYING) {
                            pauseMusic();
                            state = AppConst.STATE_PAUSED;
                        } else {
                            resumeSong();
                            state = AppConst.STATE_PLAYING;
                        }
                        break;
                    case AppConst.NOTIFICATION_PREVIOUS:
                        previousSong();
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void resumeSong() {
        mediaPlayer.start();
    }

    private void previousSong() {
        mediaPlayer.reset();
        if (position == 0) {
            position = list.size() - 1;
        } else {
            position--;
        }

        playSong(position);
        Log.i("PRE", position + "");
        Intent i = new Intent(AppConst.OTHER_PREVIOUS_POSITION);
        i.putExtra(AppConst.PREVIOUS_POSITON, position);
        sendBroadcast(i);
    }


    public void playSong(int position) {
        mediaPlayer = new MediaPlayer();
        pushNotification();
        try {
            mediaPlayer.setDataSource(list.get(position).getPath().toString());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SONG_UPDATE:
                        int currentPosition = msg.arg1;
                        PlaySongFragment.durationSeekbar.setProgress(currentPosition);
                        break;
                    case SONG_FINISH:
                        handler.removeCallbacksAndMessages(null);
                        nextSong();
                        break;
                }
            }
        };
        thread = new Thread(this);
        thread.start();
        state = AppConst.STATE_PLAYING;
        Log.i("PLAY", position + "");
    }

    @Override
    public void run() {
        currentPosition = 0;
        int total = parseInt(list.get(position).getDuration()) / 1000;
        Intent intent = new Intent(AppConst.DURATION);
        intent.putExtra(AppConst.TOTAL_TIME, total);
        this.sendBroadcast(intent);
        PlaySongFragment.durationSeekbar.setMax(total);
        while (mediaPlayer != null && currentPosition < total) {
            try {
                Message msg = new Message();
                msg.what = SONG_UPDATE;
                msg.arg1 = currentPosition;
                handler.sendMessage(msg);
                currentPosition = mediaPlayer.getCurrentPosition() / 1000;

                Intent in = new Intent(AppConst.CURRENT);
                in.putExtra(AppConst.CURRENT_TIME, currentPosition);
                this.sendBroadcast(in);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
        }
        handler.postDelayed(this, 100);
        handler.sendEmptyMessage(SONG_FINISH);
    }

    private void pushNotification() {
        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_girl);
        builder.setAutoCancel(false);

        RemoteViews mContentView = new
                RemoteViews(getPackageName(), R.layout.music_notifycation);
        mContentView.setImageViewResource(R.id.image, R.drawable.ic_girl);
        mContentView.setTextViewText(R.id.txt_songs_title_control, list.get(position).getName());
        mContentView.setTextViewText(R.id.txt_songs_artist_control, list.get(position).getAlbum());
        builder.setContent(mContentView);
        builder.setOngoing(true);

        Intent nextButton = new Intent("Song_Control");
        Bundle nextBundle = new Bundle();
        nextBundle.putInt("command", 1);
        nextButton.putExtras(nextBundle);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, nextButton, 0);

        Intent pauseButton = new Intent("Song_Control2");
        Bundle pauseBundle = new Bundle();
        pauseBundle.putInt("command", 2);
        pauseButton.putExtras(pauseBundle);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 2, pauseButton, 0);

        Intent previousButton = new Intent("Song_Control3");
        Bundle previousBundle = new Bundle();
        previousBundle.putInt("command", 3);
        previousButton.putExtras(previousBundle);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 3, previousButton, 0);

        mContentView.setOnClickPendingIntent(R.id.iv_next_notification, nextPendingIntent);
        mContentView.setOnClickPendingIntent(R.id.iv_pause_play_notification, pausePendingIntent);
        mContentView.setOnClickPendingIntent(R.id.iv_previous_notification, previousPendingIntent);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void initMedia() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(MusicService.this, "I'm Finished", Toast.LENGTH_SHORT).show();
                Log.e("LOGTAG","zzzzz");
            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isServiceRunning)
            position = intent.getIntExtra("POSITION", -1);
        else position = intent.getIntExtra(AppConst.OTHER_POSITION, -1);
        playSong(position);
        isServiceRunning = true;
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopSong() {
        mediaPlayer.stop();
        mediaPlayer.release();
        state = AppConst.STATE_IDLE;
        Log.i("STOP", "Stop");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }


    public void nextSong() {
        mediaPlayer.reset();
        if (position == list.size() - 1) {
            position = 0;
        } else position++;

        Intent i = new Intent(AppConst.OTHER_POSITION);
        i.putExtra(AppConst.NEXT_POSITON, position);
        sendBroadcast(i);

        Intent i2 = new Intent(AppConst.CURRENT);
        i2.putExtra(AppConst.CURRENT_TIME, 0);
        sendBroadcast(i2);

        playSong(position);
        Log.i("NEXT", position + "");
    }

    public void randomSong() {
        mediaPlayer.reset();
        rd = new Random();
        position = rd.nextInt(list.size() - 1);
        playSong(position);
        Intent i = new Intent(AppConst.OTHER_RANDOM_POSITION);
        i.putExtra(AppConst.RANDOM_POSITON, position);
        sendBroadcast(i);
        Log.i("TAG", position + "");
    }

    public void pauseMusic() {
        mediaPlayer.pause();
        state = AppConst.STATE_PAUSED;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // stopSong();
    }
}
