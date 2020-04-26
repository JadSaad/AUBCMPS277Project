package com.example.bookitapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomAdapter2 extends ArrayAdapter<Reservation>{
        private ArrayList<Reservation> dataSet;
        Context mContext;

        // View lookup cache
        private static class ViewHolder {
            TextView HotelName;
            TextView CheckInOut;
            TextView roomID;
        }

        public CustomAdapter2(ArrayList<Reservation> data, Context context) {
            super(context, R.layout.reserveelement, data);
            this.dataSet = data;
            this.mContext=context;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Reservation reservation = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.reserveelement, parent, false);
                viewHolder.HotelName = (TextView) convertView.findViewById(R.id.textViewReserveHotelName);
                viewHolder.CheckInOut = (TextView) convertView.findViewById(R.id.textViewResCheckIn);
                viewHolder.roomID = (TextView) convertView.findViewById(R.id.textViewrrRoomID);
                result=convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result=convertView;
            }
            viewHolder.HotelName.setText(reservation.HotelName);
            viewHolder.CheckInOut.setText(reservation.CheckIn + " - " + reservation.CheckOut);
            viewHolder.roomID.setText("Room ID: "+ String.valueOf(reservation.roomId));
            // Return the completed view to render on screen
            return convertView;
        }
    }

