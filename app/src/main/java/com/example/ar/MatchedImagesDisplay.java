package com.example.ar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class MatchedImagesDisplay extends AppCompatActivity {

    ImageView imageView;
    Bitmap frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_images_display);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        imageView = findViewById(R.id.imageView);

        if(getIntent().hasExtra("byteArray")) {
            frame = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
//
        }
        imageView.setImageBitmap(frame);

    }
}