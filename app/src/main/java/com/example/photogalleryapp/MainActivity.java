package com.example.photogalleryapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            photos = findPhotos();
        } catch (ParseException e) {
            Log.d("MainActivity", e.toString());
        }

        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }
    }


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

                    try {
                        photos = findPhotos(startDate, endDate, editKeywordSearch);
                    } catch (ParseException e) {
                        Log.d("MainActivity", e.toString());
                    }

                    if (photos.size() == 0) {
                        displayPhoto(null);
                    } else {
                        displayPhoto(photos.get(index));
                    }
                }
            });

    // Navigate the user to the search view
    public void gotoSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        searchActivityResultLauncher.launch(intent);
    }

    // Closes the search view and takes the user back to parent view (MainActivity)
    public void cancelButton(View view) {
        finish();
    }

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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_caption_" + timeStamp + "_";
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

    public ArrayList<String> findPhotos() throws ParseException {
        return findPhotos("", "", "");
    }

    public ArrayList<String> findPhotos(String startDate, String endDate, String editKeywordSearch) throws ParseException {
        // create start date and end date if exist
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date start = null, end = null;
        List<String> listKeyword;
        if (!endDate.isEmpty()) end = formatter.parse(endDate);
        if (!startDate.isEmpty()) start = formatter.parse(startDate);
        if (!editKeywordSearch.isEmpty()) {
            listKeyword = Arrays.asList(editKeywordSearch.split("\n"));
        }

        // load file list
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Android/data/com.example.photogalleryapp/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();

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


    @SuppressLint("NonConstantResourceId")
    public void scrollPhotos(View v) {
        updatePhoto(photos.get(index), ((EditText) findViewById(R.id.editTextCaption)).getText().toString());
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

    public void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.imageView2);
        TextView tv = (TextView) findViewById(R.id.textView);
        EditText et = (EditText) findViewById(R.id.editTextCaption);
        if (path == null || path == "") {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[1]);
            tv.setText(attr[2]);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                ImageView mImageView = (ImageView) findViewById(R.id.imageView2);
                mImageView.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
                try {
                    photos = findPhotos();
                } catch (ParseException e) {
                    Log.d("MainActivity", e.toString());
                }
            } else {
                File file = new File(currentPhotoPath);
                file.delete();
            }
        }
    }

    private void updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
            File from = new File(path);
            from.renameTo(to);
        }
    }

}
