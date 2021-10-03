package com.example.photogalleryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    // Public vars
    EditText startDate, endDate, editKeywordSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        editKeywordSearch = (EditText) findViewById(R.id.editKeywordSearch);
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
