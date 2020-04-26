package com.example.songsapp;

import java.io.Serializable;

public class Song implements Serializable {
    public Integer songid;
    public String name;
    public String lyrics;
    public int duration;
    public String date;
    public String audioUrl;
    public Artist artist;
    public Album album;
}
