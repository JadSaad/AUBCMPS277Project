package com.example.bookitapp;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Hotel implements Serializable {

        private int id;
        private String name;
        private String location;
        private transient Bitmap image;
        private String priceString;
        public boolean cleaning=true;
        public boolean wifi=true;
        public boolean bar = true;

        public Hotel(int id, String name, String location, Bitmap image) {
            this.name=name;
            this.id=id;
            this.location=location;
            this.image=image;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public String getLocation() {
            return location;
        }

        public Bitmap getImage(){
            return image;
        }

        public String getPriceString(){
            return priceString;
        }
}
