package com.quannk.fozen.musicdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by QuanNguy on 23/05/2017.
 */

public class MusicViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> listFragment;

    public MusicViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> listFragment) {
        super(fm);
        this.listFragment = listFragment;
    }

    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    public int getCount() {
        return this.listFragment.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Songs";
            case 1:
                return "Album";
            case 2:
                return "Artist";
        }
        return null;
    }

}

