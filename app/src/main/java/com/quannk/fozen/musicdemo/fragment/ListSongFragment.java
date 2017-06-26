package com.quannk.fozen.musicdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.quannk.fozen.musicdemo.AppConst;
import com.quannk.fozen.musicdemo.activity.MainActivity;
import com.quannk.fozen.musicdemo.activity.Myapplication;
import com.quannk.fozen.musicdemo.R;
import com.quannk.fozen.musicdemo.database.DatabaseHelper;
import com.quannk.fozen.musicdemo.item.ItemSong;
import com.quannk.fozen.musicdemo.adapter.SongAdapter;
import com.quannk.fozen.musicdemo.service.MusicService;

import java.util.ArrayList;

/**
 * Created by QuanNguy on 23/05/2017.
 */

public class ListSongFragment extends Fragment {
    private SongAdapter adapter;
    private RecyclerView rcv_listSong;
    private View view;
    private ArrayList<ItemSong> listSong;
    private DatabaseHelper databaseHelper;
    private Myapplication myapplication;
    private MusicService musicService;
    private EditText edt_seach;
    private boolean isRunningService = false;
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_ALBUM = "KEY_ALBUM";
    private TabLayout tab;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_listsong, container, false);
        myapplication = (Myapplication) getActivity().getApplication();
        databaseHelper = myapplication.getDB();
        initView();
        tab= (TabLayout) view.findViewById(R.id.tabs);
        return view;
    }


    private void initView() {
        rcv_listSong = (RecyclerView) view.findViewById(R.id.rcv_listsong);
        edt_seach= (EditText) view.findViewById(R.id.edt_seach);
        initSeachSong();
        rcv_listSong.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        listSong = databaseHelper.getAllSongs();
        view.findViewById(R.id.iv_playsong);

        adapter = new SongAdapter(this.getActivity(), listSong);
        rcv_listSong.setAdapter(adapter);
        Log.i("List FRG", "set Adapter");

        adapter.setSongClick(new SongAdapter.SongClick() {
            @Override
            public void click(int position) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_NAME, listSong.get(position).getName());
                bundle.putString(KEY_ALBUM, listSong.get(position).getAlbum());
                ((MainActivity) getActivity()).getPlaySOngFRG().setArguments(bundle);
                ((MainActivity) getActivity()).showFragment(((MainActivity) getActivity()).getPlaySOngFRG());
                ((MainActivity) getActivity()).hideAppBar();
                if (!isRunningService) {
                    Intent intent = new Intent(getActivity(), MusicService.class);
                    intent.putExtra("POSITION", position);
                    getActivity().startService(intent);
                    isRunningService = true;
                } else {
                    Intent i = new Intent(AppConst.PLAY_OTHER_MUSIC);
                    i.putExtra(AppConst.NEW_POSITION, position);
                    getActivity().sendBroadcast(i);
                }
            }
        });
    }

    private void initSeachSong() {
    edt_seach.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });
    }


}

