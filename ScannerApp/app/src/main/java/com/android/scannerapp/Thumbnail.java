package com.android.scannerapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import java.util.Date;

public class Thumbnail {
    public int Image;
    public String name;
    public String day_create;

    public int getImage() {
        return Image;
    }

    public String getName() {
        return name;
    }

    public String getDay_create() {
        return day_create;
    }

    public void setImage(int image) {
        Image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDay_create(String day_create) {
        this.day_create = day_create;
    }

    public Thumbnail(int image, String name, String day_create) {
        Image = image;
        this.name = name;
        this.day_create = day_create;
    }
}
