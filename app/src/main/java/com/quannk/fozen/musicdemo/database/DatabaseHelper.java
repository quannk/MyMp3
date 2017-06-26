package com.quannk.fozen.musicdemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.quannk.fozen.musicdemo.item.ItemSong;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/7/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "iMusicDB";

    private static final String TABLE_SONGS = "songs";
    private static final String KEY_SONG_ID = "song_id";
    private static final String KEY_SONG_NAME = "song_name";
    private static final String KEY_SONG_ARTIST = "song_artist";
    private static final String KEY_SONG_ALBUM = "song_album";
    private static final String KEY_SONG_DURATION = "song_duration";
    private static final String KEY_SONG_PATH = "song_path";
    private static final String TABLE_FAVORITE_SONGS = "favorite_songs";
    private static final String KEY_FAVORITE_ID = "favorite_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SONGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SONGS + " ("
                + KEY_SONG_ID + " INTEGER PRIMARY KEY, "
                + KEY_SONG_NAME + " TEXT, "
                + KEY_SONG_ALBUM + " TEXT, "
                + KEY_SONG_ARTIST + " TEXT, "
                + KEY_SONG_DURATION + " INTEGER, "
                + KEY_SONG_PATH + " TEXT)";

        db.execSQL(CREATE_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        this.onCreate(db);
    }

    public void addSong(ItemSong songObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SONG_ID, songObject.getId());
        values.put(KEY_SONG_NAME, songObject.getName());
        values.put(KEY_SONG_ARTIST, songObject.getArtist());
        values.put(KEY_SONG_ALBUM, songObject.getAlbum());
        values.put(KEY_SONG_DURATION, songObject.getDuration());
        values.put(KEY_SONG_PATH, songObject.getPath());
        db.insert(TABLE_SONGS, null, values);
        db.close();
    }

    public void addAllSong(ArrayList<ItemSong> listSong) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction(); // tang toc do doc ghi du lieu

        for (ItemSong songObject : listSong) {
            ContentValues values = new ContentValues();
            values.put(KEY_SONG_ID, songObject.getId());
            values.put(KEY_SONG_NAME, songObject.getName());
            values.put(KEY_SONG_ARTIST, songObject.getArtist());
            values.put(KEY_SONG_ALBUM, songObject.getAlbum());
            values.put(KEY_SONG_DURATION, songObject.getDuration());

            db.insert(TABLE_SONGS, null, values);
        }

        db.setTransactionSuccessful();// tang toc do doc ghi du lieu
        db.endTransaction();

    }


    public ArrayList<ItemSong> getAllSongs() {
        ArrayList<ItemSong> listSongs = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ItemSong song = new ItemSong();
                song.setId(cursor.getInt(0));
                song.setName(cursor.getString(1));
                song.setAlbum(cursor.getString(2));
                song.setArtist(cursor.getString(3));
                song.setDuration(cursor.getString(4));
                song.setPath(cursor.getString(5));
                listSongs.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listSongs;
    }

    public void deleteFavoriteSong(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_FAVORITE_SONGS, KEY_FAVORITE_ID + " = " + id, null);

        db.close();
    }

    public void deleteAllSongs() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_SONGS, null, null);

        db.close();
    }
}
