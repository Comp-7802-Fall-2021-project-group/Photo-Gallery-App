package com.example.photogalleryapp.model;

import androidx.exifinterface.media.ExifInterface;

public class Photo {
    PhotoExifData exifData;
    String path;

    public Photo(PhotoExifData data, String filePath) {
        exifData = data;
        path = filePath;
    }

    public String getPath() {
        return path;
    }

    public PhotoExifData getPhotoExifData() {
        return exifData;
    }

    public void setPath(String filePath) {
        path = filePath;
   }

    public void setExifData(PhotoExifData data) {
        exifData = data;
   }
}