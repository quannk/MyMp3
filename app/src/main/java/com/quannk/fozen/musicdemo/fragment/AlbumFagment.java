package com.quannk.fozen.musicdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quannk.fozen.musicdemo.R;

/**
 * Created by QuanNguy on 23/05/2017.
 */

public class AlbumFagment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_album, container, false);
        return view;
    }
}
