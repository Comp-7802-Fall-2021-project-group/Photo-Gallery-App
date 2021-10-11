package com.example.photogalleryapp;

import static androidx.exifinterface.media.ExifInterface.TAG_IMAGE_DESCRIPTION;
import static com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /**
     * ALL CLASS VARIABLES
     */
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int LOCATION_PERMISSION_CODE = 103;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ArrayList<String> photos = null;
    private String currentPhotoPath;
    private int index = 0;
    private Location curLocation;

    private FusedLocationProviderClient fusedLocationClient;

    /**
     * OVERRIDE METHODS
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Earrly check for appropriate permission
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION_PERMISSION_CODE);


        photos = findPhotos();

        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }

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

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * ALL NORMAL METHODS ARE DECLARED HERE
     */

    // Check for app required permission and ask for user approval
    private void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    // Create a temporary placeholder image file, and pass it back to the Camera intent
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    // Add location and tagging data to the new photo's exif metadata
    @SuppressLint("MissingPermission")
    private void decorateNewPhotoWithExifData(String currentPhotoPath) {
        try {
            ExifInterface exif = new ExifInterface(currentPhotoPath);
            exif.setAttribute(TAG_IMAGE_DESCRIPTION, "");
            exif.setLatLong(0, 0);

            CancellationTokenSource cts = new CancellationTokenSource();
            fusedLocationClient.getCurrentLocation(PRIORITY_LOW_POWER, cts.getToken())
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            curLocation = location;
                            Log.d("Location", "location => " + location.toString());
                        }
                    });
            exif.setLatLong(curLocation.getLatitude(), curLocation.getLongitude());
            exif.saveAttributes();
        } catch (IOException e) {
            Log.d("REQUEST_IMAGE_CAPTURE", "Unable to set EXIF attribute for the image");
        }
    }

    // Default find photos method to reload the list of pictures
    public ArrayList<String> findPhotos() {
        return findPhotos("", "", "");
    }

    // Overloading the default find photo methods to reload picture based on search criterias
    public ArrayList<String> findPhotos(String startDate, String endDate, String
            editKeywordSearch) {
        // create start date and end date if exist
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date start = null, end = null;
        List<String> listKeyword;
        try {
            if (!endDate.isEmpty()) end = formatter.parse(endDate);
            if (!startDate.isEmpty()) start = formatter.parse(startDate);
        } catch (ParseException e) {
            Log.d("findPhotos", "Problem with date parser");
        }
        if (!editKeywordSearch.isEmpty()) {
            listKeyword = Arrays.asList(editKeywordSearch.split("\n"));
        }

        // load file list
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Android/data/com.example.photogalleryapp/files/Pictures");
        File[] fList = file.listFiles();
        ArrayList<String> photos = new ArrayList<String>();

        //
        if (fList != null) {
            for (File f : fList) {
                Date fDate = new Date(f.lastModified());
                if (!startDate.isEmpty() && !fDate.after(start)) continue;
                if (!endDate.isEmpty() && !fDate.before(end)) continue;
                photos.add(f.getPath());
            }
        }

        return photos;
    }

    // Update photo UI based based on given data from displayPhoto()
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

        if (path == null || path.equals("")) {
            image.setImageResource(R.mipmap.ic_launcher);
            loadPhotoDataIntoView("", "", "", 0, 0);
        } else {
            File f = new File(path);
            Date fDate = new Date(f.lastModified());

            String caption = null;
            double latitude, longitude;
            try {
                ExifInterface exif = new ExifInterface(path);
                caption = exif.getAttribute(TAG_IMAGE_DESCRIPTION);
                latitude = exif.getLatLong()[0];
                longitude = exif.getLatLong()[1];
            } catch (NullPointerException | IOException e) {
                latitude = 0;
                longitude = 0;
            }
            if (caption == null)
                caption = "";

            // Set photo based on retrieved data
            image.setImageBitmap(BitmapFactory.decodeFile(path));
            loadPhotoDataIntoView(caption, fDate.toString(), path, latitude, longitude);
        }
    }


    //Update the photo caption EXIF metadata
    private void saveCaptionToExif(String path, String caption) {
        try {
            ExifInterface exif = new ExifInterface(path);
            exif.setAttribute(TAG_IMAGE_DESCRIPTION, caption);
            exif.saveAttributes();
        } catch (IOException e) {
            Log.d("updatePhoto", "Unable to retrieve EXIF data from the file");
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

                    // Refresh photo list
                    photos = findPhotos(startDate, endDate, editKeywordSearch);

                    if (photos.size() == 0) {
                        displayPhoto(null);
                    } else {
                        displayPhoto(photos.get(index));
                    }
                }
            });


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle result call back from Camera capture intent
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                // If a new photo is saved successfully, add EXIF data and display the photo
                decorateNewPhotoWithExifData(currentPhotoPath);

                // Refresh photo list and index
                photos = findPhotos();
                for (int i = 0; i < photos.size(); i++) {
                    if (photos.get(i).equals(currentPhotoPath)) {
                        index = i;
                        break;
                    }
                }
                displayPhoto(photos.get(index));
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
        displayPhoto(photos.get(index));
    }

    // Start to update the current picture caption, then reload the picture data
    public void updateCaption(View view) {
        if (photos.size() > 0) {
            EditText etCaption = (EditText) findViewById(R.id.editTextCaption);
            saveCaptionToExif(photos.get(index), etCaption.getText().toString());
            displayPhoto(photos.get(index));
            Toast.makeText(MainActivity.this, "Caption saved", Toast.LENGTH_SHORT).show();
        }
    }

    // Share photo to social media using Android Sharesheet
    public void uploadPhoto(View view) {

        File file = new File(photos.get(index));

        if (!file.exists()) {
            Log.wtf("File Upload Error", file.getAbsolutePath() + " does not exist");
            finish();
        }

        Uri uri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", file);

        String filename = uri.getLastPathSegment();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_TITLE, filename);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");

        Intent chooser = Intent.createChooser(shareIntent, "Share photo");

        startActivity(chooser);
    }
}
