package com.example.songsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SongDetails extends AppCompatActivity {
    Song song;
    ImageView image;
    TextView songName;
    TextView artistName;
    TextView duration;
    TextView releaseDate;
    FloatingActionButton play;
    FloatingActionButton stop;
    Button like;
    MediaPlayer mediaplayer;
    boolean liked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_song_details);
        song = (Song)getIntent().getSerializableExtra("song");
        image = findViewById(R.id.songDetailimageView);
        songName = findViewById(R.id.songDetailName);
        artistName = findViewById(R.id.songDetailArtist);
        duration = findViewById(R.id.SongDetailDuration);
        releaseDate = findViewById(R.id.songDetailReleaseDate);
        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        like = findViewById(R.id.likeButton);

        Picasso.get().load(song.artist.imageUrl).into(image);
        new CheckIfLiked().execute(song.songid);
        songName.setText(song.name);
        artistName.setText("Artist: " + song.artist.name);
        duration.setText("Duration: " + song.duration + " seconds");
        releaseDate.setText("Release Date: "  + song.date);
        mediaplayer = new MediaPlayer();
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String audioUrl = song.audioUrl;
                Log.d("audiourl", audioUrl);
                try {
                    mediaplayer.setDataSource(audioUrl);
                    mediaplayer.prepare();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mediaplayer.start();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopPlaying();
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(liked){
                    new UnlikeSong().execute(song.songid);
                }else{
                    new LikeSong().execute(song.songid);
                }
            }
        });
    }
    private void stopPlaying() {
        if (mediaplayer != null) {
            mediaplayer.stop();
            mediaplayer.start();
        }
    }
    public class UnlikeSong extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer ...params) {
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                PreparedStatement psst = con.prepareStatement("delete from likes where username = '" + UserInfo.shared.username + "' and songid = " + params[0] + ";");
                psst.execute();
                con.close();
                return true;
            } catch (Exception e) {
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean result) {
            liked = !result;
            if (result){
                Toast.makeText(getApplicationContext(), "Song Unliked", Toast.LENGTH_SHORT).show();
                like.setText("Like");
            }
            else{
                Toast.makeText(getApplicationContext(), "Error Unliking Song", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class LikeSong extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer ...params) {
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                PreparedStatement psst = con.prepareStatement("INSERT INTO likes (songid, username) " +
                        "VALUES (?, ?" +
                        ");");
                psst.setInt(1, params[0]);
                psst.setString(2, UserInfo.shared.username);
                psst.execute();
                con.close();
                return true;
            } catch (Exception e) {
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean result) {
            liked = result;
            if (result){
                Toast.makeText(getApplicationContext(), "Song Liked", Toast.LENGTH_SHORT).show();
                like.setText("Unlike");
            }
            else{
                Toast.makeText(getApplicationContext(), "Error Lking Song", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CheckIfLiked extends AsyncTask<Integer, Void, Boolean> {
        ProgressDialog dialog = new ProgressDialog(SongDetails.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Integer ...params) {
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet songs = st.executeQuery("select * from likes where username = '" + UserInfo.shared.username + "' and songid = " + params[0] + ";");
                while (songs.next()) {
                    return true;
                }
                con.close();
            } catch (Exception e) {
                //Handle Exception
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            liked = result;
            like.setText(result  ? "Unlike" : "Like");
        }
    }
}
