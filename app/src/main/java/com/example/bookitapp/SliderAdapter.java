package com.example.bookitapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;



public class SliderAdapter extends PagerAdapter{

    Context context;
    ArrayList<Bitmap> img = new ArrayList<>();

    public SliderAdapter(Context context, ArrayList<Bitmap> img) {
        this.context = context;
        this.img=img;
    }

    @Override
    public int getCount() {
        return img.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imageView;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.slider_item,container,false);

        imageView = itemView.findViewById(R.id.slider_image_view);

        imageView.setImageBitmap(img.get(position));

        container.addView(itemView);

        return itemView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}