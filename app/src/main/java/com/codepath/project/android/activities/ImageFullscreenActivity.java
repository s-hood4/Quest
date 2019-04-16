package com.codepath.project.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codepath.project.android.R;
import com.codepath.project.android.adapter.ImagePagerAdapter;
import com.codepath.project.android.helpers.FixViewPager;

public class ImageFullscreenActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);
        Intent intent = getIntent();
        String[] images = intent.getStringArrayExtra("imageSet");
        ImagePagerAdapter mCustomPagerAdapter = new ImagePagerAdapter(this, images);
        FixViewPager mViewPager = (FixViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);
    }
}
