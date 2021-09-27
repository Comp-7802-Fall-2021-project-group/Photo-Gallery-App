package com.example.photogalleryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewPager viewPager = findViewById(R.id.viewPager);
        final ImageAdapter imageAdapter = new ImageAdapter(this);
        viewPager.setAdapter(imageAdapter);
        viewPager.setCurrentItem(imageAdapter.getCount() - 1);

        imageControl();
        setCaption();

    }

    // Navigate the user to the search view
    public void gotoSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.imageView2);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
        }
    }


    public void imageControl() {
        final ViewPager viewPager = findViewById(R.id.viewPager);
        final Button left = findViewById(R.id.left);
        final Button right = findViewById(R.id.right);
        left.setOnClickListener(v -> {
            int tab = viewPager.getCurrentItem();
            if (tab > 0) {
                tab--;
                viewPager.setCurrentItem(tab);
            } else if (tab == 0) {
                viewPager.setCurrentItem(tab);
            }
        });

        right.setOnClickListener(v -> {
            int tab = viewPager.getCurrentItem();
            tab++;
            viewPager.setCurrentItem(tab);
        });
    }

    public void setCaption() {
        final ImageView imageView = findViewById(R.id.imageView2);
        final EditText editText = findViewById(R.id.imageCaption);
        final TextView textView = findViewById(R.id.textView2);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                final String input = editText.getText().toString();
                imageView.setContentDescription(input);
                textView.setText(input);
                return true;
            }
            return false;
        });
    }
}