package com.example.photogalleryapp.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photogalleryapp.R;
import com.example.photogalleryapp.presenter.SearchPresenter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    // Public vars
    EditText startDate, endDate, editKeywordSearch, editLat, editLong;

    SearchPresenter presenter;

    final Calendar myCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        presenter = new SearchPresenter();

        // Create default datetime string values, appears on the UI
        ArrayList<String> dateStrings = presenter.createDefaultDateTimeStrings();

        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        editKeywordSearch = (EditText) findViewById(R.id.editKeywordSearch);
        editLat = (EditText) findViewById(R.id.latitude);
        editLong = (EditText) findViewById(R.id.longitude);

        // startDate.setText(dateStrings.get(0));
        // endDate.setText(dateStrings.get(1));

        DatePickerDialog.OnDateSetListener startDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(startDate);
            }
        };

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SearchActivity.this, startDatePicker, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        DatePickerDialog.OnDateSetListener endDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(endDate);
            }
        };

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SearchActivity.this, endDatePicker, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    private void updateDateLabel(EditText etDate) {
        String myFormat = "yyyyMMdd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        etDate.setText(sdf.format(myCalendar.getTime()));
    }


    public void submitButton(View view) {
        boolean validation = presenter.validateSearchInput();

        if (validation) {

            Intent intent = presenter.submitButtonIntent(startDate.getText().toString(),
                    endDate.getText().toString(),
                    editKeywordSearch.getText().toString(),
                    editLat.getText().toString(),
                    editLong.getText().toString());

            // Return ok signal
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    // Closes the search view and takes the user back to parent view (MainActivity)
    public void cancelButton(View view) {
        finish();
    }
}
