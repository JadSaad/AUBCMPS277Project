package com.example.bookitapp;

import java.io.Serializable;

public class ReserveInfo implements Serializable {
    public int userid;
    public int hotelid;
    public myDate checkin;
    public myDate checkout;
    public String roomType;
    public boolean extraBed;
}
