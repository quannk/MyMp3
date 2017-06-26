package com.quannk.fozen.musicdemo.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.quannk.fozen.musicdemo.R;
import com.quannk.fozen.musicdemo.adapter.MusicViewPagerAdapter;
import com.quannk.fozen.musicdemo.database.*;
import com.quannk.fozen.musicdemo.fragment.AlbumFagment;
import com.quannk.fozen.musicdemo.fragment.ArtistFragment;
import com.quannk.fozen.musicdemo.fragment.ListSongFragment;
import com.quannk.fozen.musicdemo.fragment.PlaySongFragment;
import com.quannk.fozen.musicdemo.item.ItemSong;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static ListSongFragment listSongFRG;
    private static PlaySongFragment playSOngFRG;
    private ArrayList<ItemSong> listSong;
    private DatabaseHelper database;
    private Myapplication myapplication;
    private ViewPager viewPager;
    private MusicViewPagerAdapter musicViewPagerAdapter;
    private ArrayList<android.support.v4.app.Fragment> listFragment;
    private AlbumFagment albumFagment;
    private ArtistFragment artistFragment;
    private FrameLayout fragmentContent;
    private TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentContent= (FrameLayout) findViewById(R.id.fragment_content);
        myapplication = (Myapplication) getApplication();
        database = myapplication.getDB();
        saveListSong();
        initViews();
        initFragment();
        listFragment = new ArrayList();
        listFragment.add(listSongFRG);
        listFragment.add(albumFagment);
        listFragment.add(artistFragment);
        //listFragment.add(playSOngFRG);

        viewPager = (ViewPager) findViewById(R.id.pager);
        if (Build.VERSION.SDK_INT < 23) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            return;
        }
        musicViewPagerAdapter = new MusicViewPagerAdapter(getSupportFragmentManager(), listFragment);
        viewPager.setAdapter(musicViewPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        this.viewPager.setCurrentItem(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initViews() {
    tab = (TabLayout) findViewById(R.id.tabs);
    }

    private void saveListSong() {
        listSong = new ArrayList<ItemSong>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projects = new String[]{"*"};
        Cursor c = getContentResolver().query(uri, projects, null, null, null);
        if (c != null) {
            int idName = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int albumId = c.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistId = c.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int durationID = c.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int indexData = c.getColumnIndex(MediaStore.Audio.Media.DATA);
            if (c.moveToFirst()) {
                while (c.moveToNext()) {
                    int song_id = c.getInt(c.getColumnIndex("_id"));
                    String name = c.getString(idName);
                    String album = c.getString(albumId);
                    String artist = c.getString(artistId);
                    String duration = c.getString(durationID);
                    String path = c.getString(indexData);
                    database.addSong(new ItemSong(song_id, name, album, artist, duration, path));
                }
            }
        }
        Log.i("MAIN", "Save list song");
    }

    private void initFragment() {
        listSongFRG = new ListSongFragment();
        playSOngFRG = new PlaySongFragment();
        albumFagment = new AlbumFagment();
        artistFragment = new ArtistFragment();
        //   addFragment();
    }

    public void addFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_content, playSOngFRG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showFragment(Fragment show) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, show);
        transaction.addToBackStack(null);
        transaction.commit();

        viewPager.setVisibility(View.GONE);
        fragmentContent.setVisibility(View.VISIBLE);

    }

    public void hideFragment() {
        viewPager.setVisibility(View.VISIBLE);
        fragmentContent.setVisibility(View.GONE);
    }

    public ListSongFragment getListSongFRG() {
        return listSongFRG;
    }

    public PlaySongFragment getPlaySOngFRG() {
        return playSOngFRG;
    }

    public void hideAppBar() {
        tab.setVisibility(View.GONE);
    }

    public void showAppBar() {
        tab.setVisibility(View.VISIBLE);
    }
}
