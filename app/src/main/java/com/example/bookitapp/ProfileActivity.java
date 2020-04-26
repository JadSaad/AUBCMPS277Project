package com.example.bookitapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class ProfileActivity extends AppCompatActivity {

    public static ListView listview2;
    public  static CustomAdapter2 adapter2;
    public static UserInfo User;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        User = (UserInfo)getIntent().getSerializableExtra("user");
        listview2 = (ListView) findViewById(R.id.ReservationsList);
        new PreviewAllReservations().execute(User.id);
        listview2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                final Reservation reservation = (Reservation) arg0.getItemAtPosition(pos);
                AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
                alertDialog.setTitle("ATTENTION");
                alertDialog.setMessage("Are you sure you want to cancel this reservation?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Disregard",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteReservation().execute(reservation.id);
                                dialog.dismiss();
                            }
                                });
                alertDialog.show();
                return true;
            }
        });

    }
    public class PreviewAllReservations extends AsyncTask<Integer, Void, ArrayList<Reservation>> {
        ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching Reservations");
            dialog.show();
        }

        @Override
        protected ArrayList<Reservation> doInBackground(Integer... params) {
            ArrayList<Reservation> ReservationList = new ArrayList<>();
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet resrs = st.executeQuery("select * from reservations where UserID=" + params[0] + ";");
                while (resrs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.CheckIn = resrs.getString("CheckIn");
                    reservation.CheckOut = resrs.getString("CheckOut");
                    reservation.roomId = resrs.getInt("RoomID");
                    reservation.id = resrs.getInt("ID");
                    int id = resrs.getInt("HotelID");
                    Statement stt = con.createStatement();
                    ResultSet hotel = stt.executeQuery("select Name from hotels where ID=" + id + ";");
                    while (hotel.next()) {
                        reservation.HotelName = hotel.getString("Name");
                    }
                    ReservationList.add(reservation);
                }
                con.close();
            } catch (Exception e) {
                AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
                alertDialog.setTitle("ATTENTION");
                alertDialog.setMessage("Error");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return null;
            }
            return ReservationList;
        }


        @Override
        protected void onPostExecute(ArrayList<Reservation> ReservationList) {
            dialog.dismiss();
            adapter2= new CustomAdapter2(ReservationList,ProfileActivity.this);
            listview2.setAdapter(adapter2);
        }
    }

    public class DeleteReservation extends AsyncTask<Integer, Void, Boolean> {
        ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching Reservations");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            ArrayList<Reservation> ReservationList = new ArrayList<>();
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet resrs = st.executeQuery("select * from reservations where ID=" + params[0] + ";");
                while (resrs.next()) {
                    PreparedStatement psst = con.prepareStatement("DELETE FROM reservations WHERE ID=?"+";");
                    psst.setInt(1, params[0]);
                    psst.execute();
                }
                con.close();
                return true;
            } catch (Exception e) {
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean Succeeded) {
            dialog.dismiss();
            if(Succeeded){
                AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
                alertDialog.setTitle("ATTENTION");
                alertDialog.setMessage("Reservation Canceled");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new PreviewAllReservations().execute(User.id);
                            }
                        });
                alertDialog.show();

            }
            else{
                AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
                alertDialog.setTitle("ATTENTION");
                alertDialog.setMessage("Error In Cancelation");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }
}
