package com.example.songsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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
    }
    private void stopPlaying() {
        if (mediaplayer != null) {
            mediaplayer.stop();
            mediaplayer.start();
        }
    }
}
