package com.connect.infixd.mobile.Functions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.connect.infixd.mobile.R;
import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {

    TouchImageView imgDisplay;
    private String picURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        picURL = getIntent().getStringExtra("PICTURE_URL");
        imgDisplay = (TouchImageView) findViewById(R.id.fullImageView);

        if(picURL != null && !picURL.isEmpty()){
            Picasso.with(ImageViewerActivity.this)
                    .load(picURL)
                    .into(imgDisplay);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
