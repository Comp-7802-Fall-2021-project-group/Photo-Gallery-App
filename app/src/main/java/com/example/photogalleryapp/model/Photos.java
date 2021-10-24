package com.example.photogalleryapp.model;

import java.util.ArrayList;
import java.util.List;

public class Photos extends ArrayList<String> {

    ArrayList<Photo> photos;

    public Photos() {
        photos = new ArrayList<>();
    }

    public Photos(List<Photo> photoList) {
        photos = new ArrayList<>(photoList);
    }

    public ArrayList<Photo> getPhotosList() {
        return photos;
    }
}