package com.example.teamapp.gallery;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.ArrayList;

public class Folder {

    int imageView;
    String id;
    String name;
    ArrayList<Bitmap> bitmaps;

    public Folder(int imageView, String  id, String name, ArrayList<Bitmap> bitmaps ) {
        this.imageView = imageView;
        this.id = id;
        this.name = name;
        this.bitmaps=bitmaps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }
}
