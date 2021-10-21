package com.example.photogalleryapp.model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Photos extends ArrayList<String> {

    ArrayList<String> photos;

    public Photos() {
        photos = new ArrayList<>();
    }

    public ArrayList<String> getPhotosList() {
        return photos;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Stream<String> stream() {
        return photos.stream();
    }
}