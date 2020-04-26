package com.example.bookitapp;

import java.util.Date;

public class myDate extends Date {
    String s;

    public myDate(String date){
        super(Integer.valueOf(date.substring(6,10)),Integer.valueOf(date.substring(0,2)),Integer.valueOf(date.substring(3,5)));
        s = date;
    }
    @Override
    public String toString(){
        return s;
    }

}
