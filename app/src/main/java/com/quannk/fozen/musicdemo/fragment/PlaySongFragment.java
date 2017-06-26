package com.quannk.fozen.musicdemo.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.quannk.fozen.musicdemo.AppConst;
import com.quannk.fozen.musicdemo.activity.MainActivity;
import com.quannk.fozen.musicdemo.activity.Myapplication;
import com.quannk.fozen.musicdemo.R;
import com.quannk.fozen.musicdemo.database.DatabaseHelper;
import com.quannk.fozen.musicdemo.item.ItemSong;
import com.quannk.fozen.musicdemo.service.MusicService;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by QuanNguy on 23/05/2017.
 */

public class PlaySongFragment extends Fragment implements View.OnClickListener {
    private ImageView iv_back;
    private View view;
    private TextView tv_name, tv_album, tv_time, tv_timeend;
    private String name, album;
    private ImageView iv_next, iv_pre, iv_pause, iv_shuffle;

    private BroadcastReceiver broadcastReceiver;
    private ArrayList<ItemSong> list;
    private Myapplication myapplication;
    private DatabaseHelper databaseHelper;
    public static boolean isPause = false;
    private int curent_position;
    private SeekBar volumeSeekbar;
    public static SeekBar durationSeekbar;
    private AudioManager audioManager;
    private Random rd;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_playsong, container, false);
        this.myapplication = (Myapplication) getActivity().getApplication();
        databaseHelper = myapplication.getDB();
        list = new ArrayList<>();
        list = databaseHelper.getAllSongs();

        Bundle bundle = getArguments();
        if (bundle != null) {
            name = bundle.getString(ListSongFragment.KEY_NAME);
            album = bundle.getString(ListSongFragment.KEY_ALBUM);
        }

        initView();
        initVolumeSeekbar();
        initDurationSeerBar();
        return view;
    }

    private void initDurationSeerBar() {
        durationSeekbar.setMax(60);
        durationSeekbar.setProgress(0);
        durationSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(MusicService.mediaPlayer != null && fromUser){
                    MusicService.mediaPlayer.seekTo(progress * 1000);
                    durationSeekbar.setProgress(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private void initVolumeSeekbar() {
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        volumeSeekbar.setMax(audioManager.getStreamMaxVolume(3));
        volumeSeekbar.setProgress(audioManager.getStreamVolume(3));
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(3, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final IntentFilter filter = new IntentFilter();
        filter.addAction(AppConst.OTHER_POSITION);
        filter.addAction(AppConst.OTHER_PREVIOUS_POSITION);
        filter.addAction(AppConst.OTHER_RANDOM_POSITION);
        filter.addAction(AppConst.DURATION);
        filter.addAction(AppConst.CURRENT);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case AppConst.OTHER_POSITION:
                        durationSeekbar.setProgress(1);
                        curent_position = intent.getIntExtra(AppConst.NEXT_POSITON, -1);
                        tv_name.setText(list.get(curent_position).getName());
                        tv_album.setText(list.get(curent_position).getAlbum());
                        break;
                    case AppConst.OTHER_PREVIOUS_POSITION:
                        durationSeekbar.setProgress(0);
                        curent_position = intent.getIntExtra(AppConst.PREVIOUS_POSITON, -1);
                        tv_name.setText(list.get(curent_position).getName());
                        tv_album.setText(list.get(curent_position).getAlbum());
                        break;
                    case AppConst.OTHER_RANDOM_POSITION:
                        durationSeekbar.setProgress(0);
                        curent_position = intent.getIntExtra(AppConst.RANDOM_POSITON, -1);
                        Log.i("RANDOM", curent_position + "");
                        tv_name.setText(list.get(curent_position).getName());
                        tv_album.setText(list.get(curent_position).getAlbum());
                        break;
                    case AppConst.DURATION:
                        int total = intent.getIntExtra(AppConst.TOTAL_TIME, -1);
                        tv_timeend.setText(total / 60 + ":" + total % 60);
                        break;
                    case AppConst.CURRENT:
                        int currentTime = intent.getIntExtra(AppConst.CURRENT_TIME, -1);
                        if (currentTime < 60 && currentTime > 9) {
                            tv_time.setText("0:" + currentTime);
                        } else {
                            if (currentTime%60 < 10) {
                                tv_time.setText(currentTime / 60+":0" + currentTime%60);
                            } else
                                tv_time.setText(currentTime / 60 + ":" + currentTime % 60);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    private void initView() {
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_album = (TextView) view.findViewById(R.id.tv_album);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        iv_next = (ImageView) view.findViewById(R.id.iv_next);
        iv_pause = (ImageView) view.findViewById(R.id.iv_pause);
        iv_pre = (ImageView) view.findViewById(R.id.iv_pre);
        volumeSeekbar = (SeekBar) view.findViewById(R.id.seekbar_volume);
        durationSeekbar = (SeekBar) view.findViewById(R.id.seek_duration);
        tv_time = (TextView) view.findViewById(R.id.tv_time_play);
        tv_timeend = (TextView) view.findViewById(R.id.tv_timemax);
        iv_shuffle = (ImageView) view.findViewById(R.id.iv_shuffle);
        tv_name.setText(name);
        tv_album.setText(album);

        iv_back.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_pre.setOnClickListener(this);
        iv_pause.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_shuffle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getFragmentManager().popBackStack();
                ((MainActivity) getActivity()).showAppBar();
                ((MainActivity) getActivity()).hideFragment();
                break;
            case R.id.iv_next:
                Intent next_intent = new Intent(AppConst.NEXT);
                getActivity().sendBroadcast(next_intent);
                Log.i("PLAY_FRG", "next");
                iv_pause.setBackgroundResource(R.drawable.ic_pause);
                isPause = false;
                break;
            case R.id.iv_pre:
                Intent previous_intent = new Intent(AppConst.PREVIOUS);
                getActivity().sendBroadcast(previous_intent);
                Log.i("PLAY_FRG", "previous");
                iv_pause.setBackgroundResource(R.drawable.ic_pause);
                isPause = false;
                break;
            case R.id.iv_pause:
                Intent pause_intent = new Intent(AppConst.PAUSE);
                getActivity().sendBroadcast(pause_intent);
                if (!isPause) {
                    iv_pause.setBackgroundResource(R.drawable.ic_play);
                    isPause = true;
                } else {
                    iv_pause.setBackgroundResource(R.drawable.ic_pause);
                    isPause = false;
                }
                break;
            case R.id.iv_shuffle:
                Intent random_intent = new Intent(AppConst.RANDOM);
                getActivity().sendBroadcast(random_intent);
                isPause = false;
                Log.e("RANDOM","random");
                break;
            default:
                break;
        }
    }


    public String getName() {
        return name;
    }
}
