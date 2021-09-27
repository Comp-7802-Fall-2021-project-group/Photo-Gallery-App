package com.example.photogalleryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
}