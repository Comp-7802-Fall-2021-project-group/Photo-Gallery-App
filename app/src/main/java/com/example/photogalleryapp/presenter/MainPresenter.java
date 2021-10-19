package com.example.photogalleryapp.presenter;

import static androidx.exifinterface.media.ExifInterface.TAG_IMAGE_DESCRIPTION;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;

import com.example.photogalleryapp.model.PhotoExifData;
import com.example.photogalleryapp.model.Photos;
import com.example.photogalleryapp.util.Utilities;
import com.example.photogalleryapp.view.MainActivity;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainPresenter {

    PhotoExifData photoExifData;
    Photos photos;

    private static final int PERMISSION_ALL = 99;

    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    public MainPresenter() {
        photos = new Photos();
        photoExifData = new PhotoExifData();
    }

    public ArrayList<String> getPhotosList() {
        return photos.getPhotosList();
    }

   // Default find photos method to reload the list of pictures
    public ArrayList<String> findPhotos() {
        return findPhotos("", "", "", "", "");
    }
    // Overloading the default find photo methods to reload picture based on search criterias
    public ArrayList<String> findPhotos(String startDate, String endDate, String editKeywordSearch, String latitude, String longitude) {
        // create start date and end date if exist
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date start = null, end = null;
        double searchLatitude = 0, searchLongitude = 0;
        List<String> listKeyword = null;

        // prepare data for search
        try {
            if (!endDate.isEmpty()) end = formatter.parse(endDate);
            if (!startDate.isEmpty()) start = formatter.parse(startDate);
            if (!latitude.isEmpty()) searchLatitude = Double.parseDouble(latitude);
            if (!longitude.isEmpty()) searchLongitude = Double.parseDouble(longitude);
        } catch (ParseException e) {
            Log.d("findPhotos", "Problem with date parser");
        }
        if (!editKeywordSearch.isEmpty()) {
            listKeyword = Arrays.asList(editKeywordSearch.split("\\s+"));
        }


        // load file list
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Android/data/com.example.photogalleryapp/files/Pictures");
        File[] fList = file.listFiles();
        ArrayList<String> photos = new ArrayList<>();

        // checking photo for search criteria for each file of the list
        if (fList != null) {
            for (File f : fList) {
                // get last modified day
                Date fDate = new Date(f.lastModified());

                // Retrieve caption from the exif data
                String fCaption = null;
                double fLatitude = 0, flongitude = 0;
                try {
                    ExifInterface exif = new ExifInterface(f);
                    fCaption = exif.getAttribute(TAG_IMAGE_DESCRIPTION);
                    if (exif.getLatLong() != null) {
                        fLatitude = exif.getLatLong()[0];
                        flongitude = exif.getLatLong()[1];
                    }

                } catch (IOException e) {
                    Log.d("findPhotos", "Unable to load exif data for " + f.getAbsolutePath());
                } catch (NullPointerException e) {
                    fLatitude = 0;
                    flongitude = 0;
                }

                if (fCaption == null)
                    fCaption = "";
                else
                    fCaption = fCaption.toLowerCase(Locale.ROOT);

                /*
                 * start checking the search criteria, skip photo if any criteria is unmatched
                 */
                if (!startDate.isEmpty() && !fDate.after(start)) continue;
                if (!endDate.isEmpty() && !fDate.before(end)) continue;

                if (!editKeywordSearch.isEmpty()) { // if not empty
                    boolean found = false;
                    for (String keyword : listKeyword) {
                        if (fCaption.contains(keyword.toLowerCase(Locale.ROOT)))
                            found = true;
                    }
                    if (!found) continue;
                }

                if (!latitude.isEmpty() && !longitude.isEmpty()) {
                    double distance = Utilities.CalculateDistance(fLatitude, searchLatitude, flongitude, searchLongitude, 0.0, 0.0);
                    Log.d("findPhotos", "Search distance " + distance + "km");
                    if (distance > 20)
                        continue;
                }

                // add photo if pass all check
                photos.add(f.getPath());
            }
        }
        return photos;
    }

    public PhotoExifData getPhotoExifData(String path) {

        try {
            ExifInterface exif = new ExifInterface(path);
            if(exif.getAttribute(TAG_IMAGE_DESCRIPTION) != null) {
                photoExifData.setCaption(exif.getAttribute(TAG_IMAGE_DESCRIPTION));
            }
            if (exif.getLatLong() != null) {
                photoExifData.setLatitude(exif.getLatLong()[0]);
                photoExifData.setLongitude(exif.getLatLong()[1]);
            }
        } catch (NullPointerException | IOException e) {
            Log.d("findPhotos", "Unable to retrieve EXIF data from file");
        }
        return photoExifData;

    }

    public void saveCaptionToExif(String path, String caption) {
        try {
            ExifInterface exif = new ExifInterface(path);
            exif.setAttribute(TAG_IMAGE_DESCRIPTION, caption);
            exif.saveAttributes();
        } catch (IOException e) {
            Log.d("saveCaptionToExif", "Unable to save EXIF data from the file");
        }
    }

    public void saveNewPhotoWithExifData(String path, String caption, double latitude, double longitude) {
        try {
            // Setting up default data if needed
            ExifInterface exif = new ExifInterface(path);
            exif.setAttribute(TAG_IMAGE_DESCRIPTION, "");
            exif.setLatLong(0, 0);

            exif.setAttribute(TAG_IMAGE_DESCRIPTION, caption);
            exif.setLatLong(latitude, longitude);
            exif.saveAttributes();
        } catch (IOException e) {
            Log.d("ExifData", "Unable to set EXIF attribute for the image");
        }

    }

    /**
     * Permissions checking
     *
     * @param mainActivity
     */
    public void checkGrantPermissions(MainActivity mainActivity) {
        // Early check for appropriate permission
        if (!hasPermissions(mainActivity, false, PERMISSIONS)) {
            ActivityCompat.requestPermissions(mainActivity, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private boolean hasPermissions(Context context, boolean showToast, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    if (showToast)
                        Toast.makeText(context, "Permission denied: " + permission, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    public void onPermissionsResult(MainActivity mainActivity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mainActivity, "All Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mainActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                hasPermissions(mainActivity, true, PERMISSIONS);
            }
        }
    }
}
