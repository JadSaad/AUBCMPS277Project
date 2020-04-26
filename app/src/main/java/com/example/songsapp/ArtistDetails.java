package com.example.songsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ArtistDetails extends AppCompatActivity {
    ImageView image;
    Button followButton;
    TextView artistName;
    ListView list;

    Artist artist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);
        getSupportActionBar().hide();
        image = findViewById(R.id.artistDetailimageView);
        artistName = findViewById(R.id.artistNameArtistDetail);
        followButton = findViewById(R.id.followButton);
        list = findViewById(R.id.artistsongslist);
        artist = (Artist)getIntent().getSerializableExtra("artist");

        Picasso.get().load(artist.imageUrl).into(image);
        artistName.setText(artist.name);
        followButton.setText("Follow");

        new GetArtistSongs().execute(artist.id);
    }


    public class GetArtistSongs extends AsyncTask<Integer, Void, ArrayList<Song>> {
        ProgressDialog dialog = new ProgressDialog(ArtistDetails.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected ArrayList<Song> doInBackground(Integer ...params) {
            ArrayList<Song> songList = new ArrayList<Song>();
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet songs = st.executeQuery("select songid, name from songs where artistid = " + params[0] + ";");
                while (songs.next()) {
                    Song song = new Song();
                    song.artist = artist;
                    song.name = songs.getString("name");
                    song.songid = songs.getInt("songid");
                    songList.add(song);
                }
                con.close();
            } catch (Exception e) {
                //Handle Exception
            }
            return songList;
        }


        @Override
        protected void onPostExecute(ArrayList<Song> songList) {
            dialog.dismiss();
            SongAdapter adapter= new SongAdapter(songList,getApplicationContext());
            list.setAdapter(adapter);
        }
    }
}
