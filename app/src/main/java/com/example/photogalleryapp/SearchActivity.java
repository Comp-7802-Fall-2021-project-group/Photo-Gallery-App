package com.example.photogalleryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    // Public vars
    EditText startDate, endDate, editKeywordSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Create default datetime string values, appears on the UI
        Calendar calendar = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrow = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());

        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        editKeywordSearch = (EditText) findViewById(R.id.editKeywordSearch);

        startDate.setText(today);
        endDate.setText(tomorrow);
    }

    public void submitButton(View view) {
        // create a new intent, retrieve the search data and
        // return value to MainActivity (GalleryActivity)

        Intent intent = new Intent();

        // Set intent extra data with value from the text input
        intent.putExtra("startDate", startDate.getText().toString());
        intent.putExtra("endDate", endDate.getText().toString());
        intent.putExtra("editKeywordSearch", editKeywordSearch.getText().toString());

        // Return ok signal
        setResult(RESULT_OK, intent);
        finish();
    }

    // Closes the search view and takes the user back to parent view (MainActivity)
    public void cancelButton(View view) {
        finish();
    }
}
