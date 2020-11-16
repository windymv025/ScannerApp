package com.android.scannerapp;

import java.util.Date;

public class Thumbnail {
    public Integer Image;
    public String name;
    public String day_create;

    public Thumbnail(Integer image, String name, String day_create) {
        Image = image;
        this.name = name;
        this.day_create = day_create;
    }
}
