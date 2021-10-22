package com.example.photogalleryapp.model;

import java.util.ArrayList;

public class Photos extends ArrayList<String> {

    ArrayList<Photo> photos;

    public Photos() {
        photos = new ArrayList<>();
    }

    public ArrayList<Photo> getPhotosList() {
        return photos;
    }
}