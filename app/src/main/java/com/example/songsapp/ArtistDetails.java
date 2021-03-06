package com.example.songsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ArtistDetails extends AppCompatActivity {
    ImageView image;
    Button followButton;
    TextView artistName;
    ListView list;

    Artist artist;
    boolean followed = false;
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
        new CheckIfFollowed().execute(artist.id);

        followButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(followed){
                    new UnfollowArtist().execute(artist.id);
                }else{
                    new FollowArtist().execute(artist.id);
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = (Song)parent.getItemAtPosition(position);
                Intent intent = new Intent(ArtistDetails.this, SongDetails.class);
                intent.putExtra("song",song);
                startActivity(intent);
            }
        });

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
                ResultSet songs = st.executeQuery("select songid, name, duration, releasedate, audio from songs where artistid = " + params[0] + ";");
                while (songs.next()) {
                    Song song = new Song();
                    song.artist = artist;
                    song.name = songs.getString("name");
                    song.songid = songs.getInt("songid");
                    song.date = songs.getString("releasedate");
                    song.duration = songs.getInt("duration");
                    song.audioUrl = songs.getString("audio");
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


    public class UnfollowArtist extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer ...params) {
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                PreparedStatement psst = con.prepareStatement("delete from follow where username = '" + UserInfo.shared.username + "' and artistid = " + params[0] + ";");
                psst.execute();
                con.close();
                return true;
            } catch (Exception e) {
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean result) {
            followed = !result;
            if (result){
                Toast.makeText(getApplicationContext(), "Artist Unfollowed", Toast.LENGTH_SHORT).show();
                followButton.setText("Follow");
            }
            else{
                Toast.makeText(getApplicationContext(), "Error Unfollowing Song", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class FollowArtist extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer ...params) {
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                PreparedStatement psst = con.prepareStatement("INSERT INTO follow (artistid, username) " +
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
            followed = result;
            if (result){
                Toast.makeText(getApplicationContext(), "Artist Followed", Toast.LENGTH_SHORT).show();
                followButton.setText("Unfollow");
            }
            else{
                Toast.makeText(getApplicationContext(), "Error Following Song", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CheckIfFollowed extends AsyncTask<Integer, Void, Boolean> {
        ProgressDialog dialog = new ProgressDialog(ArtistDetails.this);
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
                ResultSet follows = st.executeQuery("select * from follow where username = '" + UserInfo.shared.username + "' and artistid = " + params[0] + ";");
                while (follows.next()) {
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
            followed = result;
            followButton.setText(result  ? "Unfollow" : "Follow");
        }
    }
}
