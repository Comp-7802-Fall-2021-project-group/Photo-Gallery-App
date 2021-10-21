package com.example.photogalleryapp.model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Photos extends ArrayList<String> {

    ArrayList<Photo> photos;

    public Photos() {
        photos = new ArrayList<>();
    }

    public ArrayList<Photo> getPhotosList() {
        return photos;
    }
}