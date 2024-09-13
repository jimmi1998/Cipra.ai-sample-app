package com.cipra.app;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ImageDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        ImageView imageView = findViewById(R.id.steps_image);

        // Load the CIPRA.ai logo using Glide
        Glide.with(this)
                .load(R.drawable.cipra_logo)
                .into(imageView);
    }
}
