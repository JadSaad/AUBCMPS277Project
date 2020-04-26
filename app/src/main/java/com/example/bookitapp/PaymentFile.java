package com.example.bookitapp;

import java.io.Serializable;

public class PaymentFile implements Serializable {
    public ReserveInfo reserveInfo;
    public int TotalPrice=0;
    public int roomPrice=0;
    public int RoomID=-1;
}
