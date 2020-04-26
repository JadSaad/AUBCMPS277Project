package com.example.bookitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;


import android.app.Dialog;
import android.content.Context;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

public class Calender2 extends AppCompatDialogFragment {
    CalendarView calendarView;
    String date;
    EditText text;
    View view;
    private CalenderListener2 listener2;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = LayoutInflater.from(this.getActivity());
        view = inflator.inflate(R.layout.activity_calender2,null);
        builder.setView(view).setTitle("Choose CheckOut Date")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Save Date", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener2.applydate2(date);
                    }

                });
        calendarView= (CalendarView) view.findViewById(R.id.calendarView2);
        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int day) {
                String Smonth = String.valueOf(month+1);
                String Sday = String.valueOf(day);
                if(month+1<10)
                    Smonth = 0+Smonth;
                if(day<10)
                    Sday = 0 + Sday;
                date = Smonth + "/" + Sday + "/" + year;
            }
        });
        return builder.create();

    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        listener2 = (CalenderListener2)context;

    }


    public interface CalenderListener2{
        void applydate2(String date);
    }

}

