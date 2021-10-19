package com.example.photogalleryapp.view;

import static androidx.exifinterface.media.ExifInterface.TAG_IMAGE_DESCRIPTION;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.example.photogalleryapp.R;
import com.example.photogalleryapp.model.PhotoExifData;
import com.example.photogalleryapp.presenter.MainPresenter;
import com.example.photogalleryapp.util.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    /**
     * ALL CLASS VARIABLES
     */
    private static final int REQUEST_IMAGE_CAPTURE = 1;


    private MainPresenter presenter = null;
    private static ArrayList<String> photos = null;
    private String currentPhotoPath;
    private int index = 0;
    private Location curLocation;

    private FusedLocationProviderClient fusedLocationClient;

    public static int getPhotoCount() {
        if (photos != null)
            return photos.size();
        else return 0;
    }

    /**
     * OVERRIDE METHODS
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter();

        presenter.checkGrantPermissions(MainActivity.this);

        photos = presenter.findPhotos();
        updatePhotoFromIndex();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        curLocation = location;
                        Log.d("Location", "location => " + location.toString());
                    }
                });
    }

    // Confirm permissions and display an appropriate toad message for each permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        presenter.onPermissionsResult(
                MainActivity.this,
                requestCode,
                permissions,
                grantResults);
    }

    /**
     * ALL NORMAL METHODS ARE DECLARED HERE
     */


    // Create a temporary placeholder image file, and pass it back to the Camera intent
    private File createImageFile() throws IOException {
        File image = presenter.createImageFile(this);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    // Add location and tagging data to the new photo's exif metadata
    @SuppressLint("MissingPermission")
    private void decorateNewPhotoWithExifData(String currentPhotoPath) {
        CancellationTokenSource cts = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, cts.getToken())
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        curLocation = location;
                        Log.d("Location", "location => " + location.toString());
                    }
                });
        if (curLocation != null) {
            presenter.saveNewPhotoWithExifData(currentPhotoPath, "",
                                    curLocation.getLatitude(), curLocation.getLongitude());;
        }
    }

    // Auto update photo using the current index
    private void updatePhotoFromIndex() {
        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }
    }

    // Update photo UI based based on given data from displayPhoto()
    @SuppressLint("DefaultLocale")
    public void loadPhotoDataIntoView(String caption, String timestamp, String filename,
                                      double latitude, double longitude) {
        EditText etCaption = (EditText) findViewById(R.id.editTextCaption);
        TextView tvTimestamp = (TextView) findViewById(R.id.textViewTimestamp);
        TextView tvFilename = (TextView) findViewById(R.id.textViewFilename);
        TextView tvLat = (TextView) findViewById(R.id.textViewLat);
        TextView tvLong = (TextView) findViewById(R.id.textViewLong);

        etCaption.setText(caption);
        tvTimestamp.setText(timestamp);
        tvFilename.setText(filename);
        tvLat.setText(String.format("Latitude: %,.4f", latitude));
        tvLong.setText(String.format("Longitude: %,.4f", longitude));
    }

    // Load current picture with the caption and geo from the picture's exif metadata
    public void displayPhoto(String path) {
        ImageView image = (ImageView) findViewById(R.id.imageView2);

        // if photos can't be found, display generic android logo
        if (path == null || path.equals("")) {
            image.setImageResource(R.mipmap.ic_launcher);
            loadPhotoDataIntoView("", "", "", 0, 0);
        } else {

            File f = presenter.getPhotoFile(index);
            Date fDate = new Date(f.lastModified());
            PhotoExifData photoExifData = presenter.getPhotoExifData(path);

            // Set photo based on retrieved data
            image.setImageBitmap(BitmapFactory.decodeFile(path));
            loadPhotoDataIntoView(photoExifData.getCaption(), fDate.toString(), f.getName(),
                    photoExifData.getLatitude(), photoExifData.getLongitude());
        }
    }

    /**
     * ALL RESULT CALL BACK METHOD AND ACTIVITY LAUNCHERS
     */

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> searchActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    // Retrieve intent's data
                    String startDate = data.getStringExtra("startDate");
                    String endDate = data.getStringExtra("endDate");
                    String editKeywordSearch = data.getStringExtra("editKeywordSearch");
                    String latitude = data.getStringExtra("latitude");
                    String longitude = data.getStringExtra("longitude");

                    // Refresh photo list
                    index = 0;
                    photos = presenter.findPhotos(startDate, endDate, editKeywordSearch, latitude, longitude);

                    updatePhotoFromIndex();
                }
            });


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle result call back from Camera capture intent
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                // If a new photo is saved successfully, add EXIF data and display the photo
                // View will collect information and send back to presenter to save attributes
                decorateNewPhotoWithExifData(currentPhotoPath);

                // Refresh photo list and index
                photos = presenter.findPhotos();
                for (int i = 0; i < photos.size(); i++) {
                    if (photos.get(i).equals(currentPhotoPath)) {
                        index = i;
                        break;
                    }
                }
                updatePhotoFromIndex();
            } else {
                // If photo is unavailable, delete the placeholder file from disk
                // and reset the current photo path
                File file = new File(currentPhotoPath);
                file.delete();
                currentPhotoPath = "";
            }
        }
    }

    /**
     * PUBLIC METHOD FOR BUTTON ACTION
     */

    // Navigate the user to the search view
    public void gotoSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        searchActivityResultLauncher.launch(intent);
    }

    // Closes the search view and takes the user back to parent view (MainActivity)
    public void cancelButton(View view) {
        finish();
    }

    // Take a new picture and launch picture intent
    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    // Scroll photo and update of the photo
    @SuppressLint("NonConstantResourceId")
    public void scrollPhotos(View v) {
        switch (v.getId()) {
            case R.id.buttonLeft:
                if (index > 0) {
                    this.index--;
                }
                break;
            case R.id.buttonRight:
                if (index < (photos.size() - 1)) {
                    index++;
                }
                break;
            default:
                break;
        }
        updatePhotoFromIndex();
    }

    // Start to update the current picture caption, then reload the picture data
    public void updateCaption(View view) {
        if (photos.size() > 0) {
            EditText etCaption = (EditText) findViewById(R.id.editTextCaption);
            presenter.saveCaptionToExif(this, index, etCaption.getText().toString());
            updatePhotoFromIndex();
        }
    }

    // Share photo to social media using Android Sharesheet
    public void uploadPhoto(View view) {
        presenter.uploadPhotoIntent(this, index);
    }
}
