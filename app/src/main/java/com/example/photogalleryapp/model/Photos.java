package com.example.photogalleryapp.model;

import java.util.ArrayList;

public class Photos {

    ArrayList<String> photos;

    public Photos() {
        photos = new ArrayList<>();
    }

    public void addPhoto(String path) {
        photos.add(path);
    }

    public void deletePhoto(int index) {
        photos.remove(index);
    }

    public String getPhoto(int index) {
        return photos.get(index);
    }

    public ArrayList<String> getPhotosList() {
        return photos;
    }
}