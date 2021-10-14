package com.example.photogalleryapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    // Public vars
    EditText startDate, endDate, editKeywordSearch, editLat, editLong;

    final Calendar myCalendar = Calendar.getInstance();


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
        editLat = (EditText) findViewById(R.id.latitude);
        editLong = (EditText) findViewById(R.id.longitude);

        // startDate.setText(today);
        // endDate.setText(tomorrow);

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
        boolean validation = validateSearchInput();

        if (validation) {
            // create a new intent, retrieve the search data and
            // return value to MainActivity (GalleryActivity)

            Intent intent = new Intent();

            // Set intent extra data with value from the text input
            intent.putExtra("startDate", startDate.getText().toString());
            intent.putExtra("endDate", endDate.getText().toString());
            intent.putExtra("editKeywordSearch", editKeywordSearch.getText().toString());
            intent.putExtra("latitude", editLat.getText().toString());
            intent.putExtra("longitude", editLong.getText().toString());

            // Return ok signal
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean validateSearchInput() {
        // TODO: Basic validation
        return true;
    }

    // Closes the search view and takes the user back to parent view (MainActivity)
    public void cancelButton(View view) {
        finish();
    }
}
