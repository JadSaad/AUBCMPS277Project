package com.example.songsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArtistAdapter extends ArrayAdapter<Artist>{

    private ArrayList<Artist> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView artistName;
        ImageView image;
    }

    public ArtistAdapter(ArrayList<Artist> data, Context context) {
        super(context, R.layout.artist_element, data);
        this.dataSet = data;
        this.mContext=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Artist artist = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.artist_element, parent, false);
            viewHolder.artistName = (TextView) convertView.findViewById(R.id.artistListTextView);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.artistListImageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.artistName.setText(artist.name);
        Picasso.get().load(artist.imageUrl).into(viewHolder.image);
        // Return the completed view to render on screen
        return convertView;
    }
}