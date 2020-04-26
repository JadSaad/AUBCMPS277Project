package com.example.bookitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.*;
import java.util.concurrent.TimeUnit;

public class PaymentActivity extends AppCompatActivity {
    EditText CardNumber;
    EditText CardName;
    EditText Exp;
    TextView PriceTag;
    Button Pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().hide();
        final PaymentFile file = (PaymentFile) getIntent().getSerializableExtra("file");
        CardName = (EditText) findViewById(R.id.CardName);
        CardNumber = (EditText) findViewById(R.id.CardNumber);
        Exp = (EditText) findViewById(R.id.CardExp);
        Pay = (Button) findViewById(R.id.ButtonPay);
        PriceTag = (TextView) findViewById(R.id.TotalPrice);
        file.TotalPrice = ((int) TimeUnit.DAYS.convert(file.reserveInfo.checkout.getTime() - file.reserveInfo.checkin.getTime(), TimeUnit.MILLISECONDS)) * file.roomPrice;
        PriceTag.setText("Total Price: $" + file.TotalPrice);
        Pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CardName.getText().toString().isEmpty() || CardNumber.getText().toString().isEmpty() || Exp.getText().toString().isEmpty()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(PaymentActivity.this).create();
                    alertDialog.setTitle("ATTENTION");
                    alertDialog.setMessage("Enter all fields");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    try {
                        ConnectionClass c = new ConnectionClass();
                        Connection con = c.CONN();
                        PreparedStatement psst = con.prepareStatement("INSERT INTO `reservations` (`ID`,`UserID`, `HotelID`, `RoomID`, `CheckIn`, `CheckOut`, `ExtraBed`,`TotalPrice`) " +
                                "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?" +
                                ");");
                        psst.setInt(1, file.reserveInfo.userid);
                        psst.setInt(2, file.reserveInfo.hotelid);
                        psst.setInt(3, file.RoomID);
                        psst.setString(4, file.reserveInfo.checkin.toString());
                        psst.setString(5, file.reserveInfo.checkout.toString());
                        file.TotalPrice = ((int) TimeUnit.DAYS.convert(file.reserveInfo.checkout.getTime() - file.reserveInfo.checkin.getTime(), TimeUnit.MILLISECONDS)) * file.roomPrice;
                        String s = "No";
                        if(file.reserveInfo.extraBed)
                            s = "Yes";
                        psst.setString(6,s);
                        psst.setInt(7, file.TotalPrice);
                        psst.execute();
                        Toast.makeText(getApplicationContext(), "Room Successfully Reserved", Toast.LENGTH_SHORT).show();
                        finish();

                    } catch (Exception e) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PaymentActivity.this).create();
                        alertDialog.setTitle("ATTENTION");
                        alertDialog.setMessage("Error");
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
        });


    }
}
