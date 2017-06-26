package com.quannk.fozen.musicdemo.activity;

import android.app.Application;

import com.quannk.fozen.musicdemo.database.*;

/**
 * Created by QuanNguy on 04/05/2017.
 */

public class Myapplication extends Application{
    private DatabaseHelper db;
    @Override
    public void onCreate() {
        super.onCreate();
          db =new DatabaseHelper(this);

    }
    public DatabaseHelper getDB(){
           return db;
    }
}
