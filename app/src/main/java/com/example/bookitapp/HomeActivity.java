package com.example.bookitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    public static ListView listview;
    public  static CustomAdapter adapter;
    EditText SearchBar;
    FloatingActionButton Search, Profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        final UserInfo User = (UserInfo)getIntent().getSerializableExtra("user");
        listview = (ListView) findViewById(R.id.listview);
        SearchBar = (EditText) findViewById(R.id.searchBar);
        Search = (FloatingActionButton) findViewById(R.id.SearchButton);
        Profile = (FloatingActionButton) findViewById(R.id.ProfileButton);
        Search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SearchResult().execute(SearchBar.getText().toString());
            }
        });
        Profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("user", User);
                startActivity(intent);
            }
        });
        new PreviewAllHotels ().execute(0);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hotel hotel= (Hotel)parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),hotel.getName(), Toast.LENGTH_SHORT).show();
                TransferFile file = new TransferFile();
                file.hotel=hotel;
                file.user=User;
                Intent intent = new Intent(HomeActivity.this, ReservationActivity.class);
                intent.putExtra("file",file);
                startActivity(intent);

            }
        });
    }


    public class PreviewAllHotels extends AsyncTask<Integer, Void, ArrayList<Hotel>> {
        ProgressDialog dialog = new ProgressDialog(HomeActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching Data");
            dialog.show();
        }

        @Override
        protected ArrayList<Hotel> doInBackground(Integer ...params) {
            ArrayList<Hotel> HotelList = new ArrayList<Hotel>();
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet hotelsrs = st.executeQuery("select * from hotels");
                while (hotelsrs.next()) {
                    int id = hotelsrs.getInt("ID");
                    String name = hotelsrs.getString("Name");
                    String location = hotelsrs.getString("Location");
                    Blob blob = hotelsrs.getBlob("Image");
                    byte[] image = blob.getBytes(1,(int)blob.length());
                    Bitmap bt = BitmapFactory.decodeByteArray(image,0,image.length);
                    Hotel hotel = new Hotel(id,name,location,bt);
                    if(hotelsrs.getString("Cleaning").equals("No")){
                        hotel.cleaning=false;
                    }
                    if(hotelsrs.getString("Wifi").equals("No")){
                        hotel.wifi=false;
                    }
                    if(hotelsrs.getString("Bar").equals("No")){
                        hotel.bar=false;
                    }
                    HotelList.add(hotel);
                }
                con.close();
            } catch (Exception e) {
                //Handle Exception
            }
            return HotelList;
        }


        @Override
        protected void onPostExecute(ArrayList<Hotel> HotelList) {
            dialog.dismiss();
            adapter= new CustomAdapter(HotelList,getApplicationContext());
            listview.setAdapter(adapter);
        }
    }
    public class SearchResult extends AsyncTask<String, Void, ArrayList<Hotel>> {
        ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching Data");
            dialog.show();
        }

        @Override
        protected ArrayList<Hotel> doInBackground(String ...params) {
            ArrayList<Hotel> HotelList = new ArrayList<Hotel>();
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet hotelsrs = st.executeQuery("select * from hotels where Name like '%" + params[0] +"%' or Location like '%" + params[0] + "%';");
                while (hotelsrs.next()) {
                    int id = hotelsrs.getInt("ID");
                    String name = hotelsrs.getString("Name");
                    String location = hotelsrs.getString("Location");
                    Blob blob = hotelsrs.getBlob("Image");
                    byte[] image = blob.getBytes(1,(int)blob.length());
                    Bitmap bt = BitmapFactory.decodeByteArray(image,0,image.length);

                    Hotel hotel = new Hotel(id,name,location,bt);
                    if(hotelsrs.getString("Cleaning").equals("No")){
                        hotel.cleaning=false;
                    }
                    if(hotelsrs.getString("Wifi").equals("No")){
                        hotel.wifi=false;
                    }
                    if(hotelsrs.getString("Bar").equals("No")){
                        hotel.bar=false;
                    }
                    HotelList.add(hotel);
                }
                con.close();
            } catch (Exception e) {
                //Handle Exception
            }
            return HotelList;
        }


        @Override
        protected void onPostExecute(ArrayList<Hotel> HotelList) {
            dialog.dismiss();
            adapter= new CustomAdapter(HotelList,getApplicationContext());
            listview.setAdapter(adapter);
        }
    }
}
