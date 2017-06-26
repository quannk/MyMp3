package com.quannk.fozen.musicdemo.item;

/**
 * Created by Foze on 25/04/2017.
 */

public class ItemSong {
    private String name,album,artist,duration,path;
    private int id,artist_id,album_id,name_id;

    public ItemSong(int id, String name, String album, String artist, String duration, String path) {
        this.name = name;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.id=id;
        this.path=path;
    }

    public ItemSong() {
    }

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public int getName_id() {
        return name_id;
    }

    public void setName_id(int name_id) {
        this.name_id = name_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public ItemSong(ItemSong itemSong) {
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
