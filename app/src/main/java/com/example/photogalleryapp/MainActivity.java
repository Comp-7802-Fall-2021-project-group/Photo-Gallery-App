package com.example.photogalleryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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
        final ImageView imageView = findViewById(R.id.imageView);
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