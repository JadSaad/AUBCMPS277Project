package com.example.bookitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReservationActivity extends AppCompatActivity implements Calender.CalenderListener, Calender2.CalenderListener2{
    TextView textView;
    UserInfo user;
    Hotel hotel;
    ViewPager viewPager;
    Button ReserveButton;

    EditText checkin;
    EditText checkout;
    RadioButton S,D,T;
    CheckBox cleaning, wifi, bar,extraBed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        getSupportActionBar().hide();
        TransferFile file = (TransferFile) getIntent().getSerializableExtra("file");
        user = file.user;
        hotel = file.hotel;
        textView = (TextView) findViewById(R.id.textViewHotelName);
        textView.setText(hotel.getName());
        viewPager = (ViewPager) findViewById(R.id.view_pager_slider);
        new GetImages().execute(hotel.getId());
        ReserveButton = (Button) findViewById(R.id.buttonReserve);
        S = (RadioButton) findViewById(R.id.radioButtonSingle);
        T = (RadioButton) findViewById(R.id.radioButtonTriple);
        D = (RadioButton) findViewById(R.id.radioButtonDouble);
        cleaning = (CheckBox) findViewById(R.id.checkBoxCleaning);
        bar = (CheckBox) findViewById(R.id.checkBoxBAR);
        wifi = (CheckBox) findViewById(R.id.checkBoxCWifi);
        extraBed = (CheckBox) findViewById(R.id.checkBoxExtraBed);
        if(hotel.cleaning)
            cleaning.setChecked(true);
        if(hotel.wifi)
            wifi.setChecked(true);
        if(hotel.bar)
            bar.setChecked(true);
        cleaning.setInputType(InputType.TYPE_NULL);
        wifi.setInputType(InputType.TYPE_NULL);
        bar.setInputType(InputType.TYPE_NULL);
        checkin = (EditText) findViewById(R.id.editTextCheckIn);
        checkout = (EditText) findViewById(R.id.editTextCheckOut);
        checkin.setFocusable(false);
        checkin.setClickable(true);
        checkout.setFocusable(false);
        checkout.setClickable(true);

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCheckInDialog();
            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCheckOutDialog();
            }
        });
        ReserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkin.getText().toString().isEmpty() || checkout.getText().toString().isEmpty() || ((!S.isChecked() && (!D.isChecked()) && (!T.isChecked())))) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ReservationActivity.this).create();
                    alertDialog.setTitle("ATTENTION");
                    alertDialog.setMessage("Please Enter All Fields!");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else if(!(new myDate(checkin.getText().toString()).before(new myDate(checkout.getText().toString())))){
                    AlertDialog alertDialog = new AlertDialog.Builder(ReservationActivity.this).create();
                    alertDialog.setTitle("ATTENTION");
                    alertDialog.setMessage("Check Out must be after Check In");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    ReserveInfo r = new ReserveInfo();
                    r.userid=user.getId();
                    r.hotelid = hotel.getId();
                    r.checkin = new myDate(checkin.getText().toString());
                    r.checkout = new myDate(checkout.getText().toString());
                    if (T.isChecked()) {
                        r.roomType = "Triple";
                    } else if (S.isChecked()) {
                        r.roomType = "Single";
                    } else r.roomType = "Double";
                    r.extraBed= extraBed.isChecked();
                    new CheckAndReserve().execute(r);
                }
            }
        });
    }
    public void openCheckInDialog(){
        Calender calender=  new Calender();
        calender.show(getSupportFragmentManager(),"Calender");
    }
    public void openCheckOutDialog(){
        Calender2 calender2=  new Calender2();
        calender2.show(getSupportFragmentManager(),"Calender2");
    }

    @Override
    public void applydate(String date) {
        checkin.setText(date);
    }

    @Override
    public void applydate2(String date){
        checkout.setText(date);
    }



    public class GetImages extends AsyncTask<Integer, Void, ArrayList<Bitmap>> {
        ProgressDialog dialog = new ProgressDialog(ReservationActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching Images");
            dialog.show();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(Integer... params) {
            ArrayList<Bitmap> result = new ArrayList<>();
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet accrs = st.executeQuery("select * from images");
                while (accrs.next()) {
                    if (Integer.valueOf(accrs.getInt("HotelID")).equals(params[0])) {
                        Blob blob = accrs.getBlob("Image");
                        byte[] image = blob.getBytes(1,(int)blob.length());
                        Bitmap bt = BitmapFactory.decodeByteArray(image,0,image.length);
                        result.add(bt);
                    }
                }
                con.close();
                return result;
            } catch (Exception e) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(ArrayList<Bitmap> s) {
            dialog.dismiss();
            viewPager.setAdapter(new SliderAdapter(ReservationActivity.this, s));
        }
    }
    public class CheckAndReserve extends AsyncTask<ReserveInfo, Void, PaymentFile> {
        ProgressDialog dialog = new ProgressDialog(ReservationActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Checking Availability");
            dialog.show();
        }

        @Override
        protected PaymentFile doInBackground(ReserveInfo... params) {
            ArrayList<Bitmap> result = new ArrayList<>();
            PaymentFile file = new PaymentFile();
            file.reserveInfo=params[0];
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet rooms = st.executeQuery("SELECT ID, Price FROM rooms where HotelID=" + params[0].hotelid +" and Type=\"" + params[0].roomType +"\";");
                boolean available = false;
                while(rooms.next()){
                    available = true;
                    Statement stt = con.createStatement();
                    ResultSet reservations = stt.executeQuery("SELECT * FROM reservations WHERE RoomID=" + rooms.getInt("ID") + ";");
                    while(reservations.next()){
                        if(problemInDate(params[0].checkin,params[0].checkout, new myDate(reservations.getString("CheckIn")),new myDate(reservations.getString("CheckOut")))){
                            available = false;
                        }
                        else{
                            available =  true;
                            break;
                        }
                    }
                    if(available)
                        file.RoomID=rooms.getInt("ID");
                        file.roomPrice=rooms.getInt("Price");
                        break;
                }
                con.close();
                return file;

            } catch (Exception e) {
                file.RoomID=-2;
                return file;
            }
        }



        @Override
        protected void onPostExecute(PaymentFile file) {
            dialog.dismiss();
            if(file.RoomID==-1){
                AlertDialog alertDialog = new AlertDialog.Builder(ReservationActivity.this).create();
                alertDialog.setTitle("ATTENTION");
                alertDialog.setMessage("No Room Available");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            else if(file.RoomID==-2){
                AlertDialog alertDialog = new AlertDialog.Builder(ReservationActivity.this).create();
                alertDialog.setTitle("ATTENTION");
                alertDialog.setMessage("Exception");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

            else{
                Intent i = new Intent(ReservationActivity.this,PaymentActivity.class);
                i.putExtra("file",file);
                startActivity(i);
            }
        }
    }

    public boolean problemInDate (myDate date1, myDate date2, myDate room1, myDate room2){
        if(date1.equals(room1)|| date2.equals(room2))
            return true;
        if(date1.after(room1)&&date1.before(room2))
            return true;
        if(date2.after(room1)&&date2.before(room2))
            return true;
        return false;

    }

}
