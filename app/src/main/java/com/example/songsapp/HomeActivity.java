package com.example.songsapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.Bundle;
import android.widget.ToggleButton;

public class HomeActivity extends AppCompatActivity {
    public static ListView listview;
    EditText searchBar;
    FloatingActionButton searchButton, likesButton, artistsButton, playlistButton;
    ToggleButton songArtistToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        listview = findViewById(R.id.listview);
        searchButton = findViewById(R.id.searchActionButton);
        likesButton = findViewById(R.id.likesActionButton);
        artistsButton = findViewById(R.id.artistsActionButton);
        playlistButton = findViewById(R.id.playlistActionButton);
        searchBar = findViewById(R.id.searcheditText);
        songArtistToggle = findViewById(R.id.toggleButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!songArtistToggle.isChecked()){
                    new SearchSongs().execute(searchBar.getText().toString());
                }
                else {
                    new SearchArtists().execute(searchBar.getText().toString());
                }
            }
        });

        artistsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new GetFollowedArtists().execute();
            }
        });

        likesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new GetLikedSongs().execute();
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = parent.getItemAtPosition(position);
                if(obj instanceof Artist){
                    Artist artist = (Artist) obj;
                    Intent intent = new Intent(HomeActivity.this, ArtistDetails.class);
                    intent.putExtra("artist",artist);
                    startActivity(intent);
                }
                else{
                    Song song = (Song) obj;
                    Intent intent = new Intent(HomeActivity.this, SongDetails.class);
                    intent.putExtra("song",song);
                    startActivity(intent);
                }
            }
        });
    }

    public class SearchArtists extends AsyncTask<String, Void, ArrayList<Artist>> {
        ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected ArrayList<Artist> doInBackground(String ...params) {
            ArrayList<Artist> artistList = new ArrayList<Artist>();
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet artists = st.executeQuery("select * from artists where name like '%" + params[0] +"%';");
                while (artists.next()) {
                    int id = artists.getInt("artistid");
                    String name = artists.getString("name");
                    String imageUrl = artists.getString("imageurl");
                    Artist artist = new Artist();
                    artist.id = id; artist.name = name; artist.imageUrl = imageUrl;
                    artistList.add(artist);
                }
                con.close();
            } catch (Exception e) {
                //Handle Exception
            }
            return artistList;
        }


        @Override
        protected void onPostExecute(ArrayList<Artist> artistList) {
            dialog.dismiss();
            ArtistAdapter adapter= new ArtistAdapter(artistList,getApplicationContext());
            listview.setAdapter(adapter);
        }
    }

    public class SearchSongs extends AsyncTask<String, Void, ArrayList<Song>> {
        ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected ArrayList<Song> doInBackground(String ...params) {
            ArrayList<Song> songList = new ArrayList<>();
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet songs = st.executeQuery("select a.artistid, a.name, a.imageurl, s.songid, s.name, s.releasedate, s.audio, s.duration from artists a, songs s where (s.name like '%" + params[0] +"%') and (s.artistid = a.artistid);");
                while (songs.next()) {
                    int artistid = songs.getInt("a.artistid");
                    String artistname = songs.getString("a.name");
                    String imageUrl = songs.getString("a.imageurl");
                    Artist artist = new Artist();
                    artist.id = artistid; artist.name = artistname; artist.imageUrl = imageUrl;
                    Song song = new Song();
                    song.artist = artist;
                    song.name = songs.getString("s.name");
                    song.songid = songs.getInt("s.songid");
                    song.audioUrl = songs.getString("s.audio");
                    song.duration = songs.getInt("s.duration");
                    song.date = songs.getString("s.releasedate");
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
            listview.setAdapter(adapter);
        }
    }

    public class GetLikedSongs extends AsyncTask<String, Void, ArrayList<Song>> {
        ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected ArrayList<Song> doInBackground(String ...params) {
            ArrayList<Song> songList = new ArrayList<>();
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet songs = st.executeQuery("select a.artistid, a.name, a.imageurl, s.songid, s.name, s.releasedate, s.audio, s.duration from artists a, songs s, likes l where (s.artistid = a.artistid) and (s.songid = l.songid) and (l.username = '" + UserInfo.shared.username + "');");
                while (songs.next()) {
                    int artistid = songs.getInt("a.artistid");
                    String artistname = songs.getString("a.name");
                    String imageUrl = songs.getString("a.imageurl");
                    Artist artist = new Artist();
                    artist.id = artistid; artist.name = artistname; artist.imageUrl = imageUrl;
                    Song song = new Song();
                    song.artist = artist;
                    song.name = songs.getString("s.name");
                    song.songid = songs.getInt("s.songid");
                    song.audioUrl = songs.getString("s.audio");
                    song.duration = songs.getInt("s.duration");
                    song.date = songs.getString("s.releasedate");
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
            listview.setAdapter(adapter);
        }
    }

    public class GetFollowedArtists extends AsyncTask<String, Void, ArrayList<Artist>> {
        ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected ArrayList<Artist> doInBackground(String ...params) {
            ArrayList<Artist> artistList = new ArrayList<Artist>();
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet artists = st.executeQuery("select a.artistid, a.name, a.imageurl from artists a, follow f where a.artistid = f.artistid and f.username = '" + UserInfo.shared.username + "';");
                while (artists.next()) {
                    int id = artists.getInt("a.artistid");
                    String name = artists.getString("a.name");
                    String imageUrl = artists.getString("a.imageurl");
                    Artist artist = new Artist();
                    artist.id = id; artist.name = name; artist.imageUrl = imageUrl;
                    artistList.add(artist);
                }
                con.close();
            } catch (Exception e) {
                //Handle Exception
            }
            return artistList;
        }


        @Override
        protected void onPostExecute(ArrayList<Artist> artistList) {
            dialog.dismiss();
            ArtistAdapter adapter= new ArtistAdapter(artistList,getApplicationContext());
            listview.setAdapter(adapter);
        }
    }
}