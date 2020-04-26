package com.example.bookitapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Hotel>{

    private ArrayList<Hotel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView HotelName;
        TextView HotelLocation;
        ImageView image;
    }

    public CustomAdapter(ArrayList<Hotel> data, Context context) {
        super(context, R.layout.element, data);
        this.dataSet = data;
        this.mContext=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Hotel hotel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.element, parent, false);
            viewHolder.HotelName = (TextView) convertView.findViewById(R.id.ElementName);
            viewHolder.HotelLocation = (TextView) convertView.findViewById(R.id.ElementLocation);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.ElementIcon);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        viewHolder.HotelName.setText(hotel.getName());
        viewHolder.HotelLocation.setText(hotel.getLocation());
        viewHolder.image.setImageBitmap(hotel.getImage());
        // Return the completed view to render on screen
        return convertView;
    }
}