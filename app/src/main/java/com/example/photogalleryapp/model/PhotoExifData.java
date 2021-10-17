package com.example.photogalleryapp.model;

public class PhotoExifData {

    String caption;
    double latitude;
    double longitude;

    public PhotoExifData() {
        caption = "";
        latitude = 0;
        longitude = 0;

    }

    public String getCaption() {
        return caption;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setCaption(String cap) {
        caption = cap;
    }

    public void setLatitude(double lat) {
        latitude = lat;
    }

    public void setLongitude(double lon) {
        longitude = lon;
    }

}
