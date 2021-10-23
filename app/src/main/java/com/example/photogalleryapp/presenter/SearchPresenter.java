package com.example.photogalleryapp.presenter;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SearchPresenter {

    public ArrayList<String> createDefaultDateTimeStrings() {
        Calendar calendar = Calendar.getInstance();
        ArrayList<String> dateStrings = new ArrayList<>();

        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());
        dateStrings.add(today);

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrow = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());

        dateStrings.add(tomorrow);

        return dateStrings;
    }

    public Intent submitButtonIntent(String startDate, String endDate, String editKeywordSearch,
                                     String latitude, String longitude) {
        Intent intent = new Intent();

        // Set intent extra data with value from the text input
        intent.putExtra("startDate", startDate);
        intent.putExtra("endDate", endDate);
        intent.putExtra("editKeywordSearch", editKeywordSearch);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        return intent;
    }

    public boolean validateSearchInput() {
        // TODO: Basic validation
        return true;
    }
}