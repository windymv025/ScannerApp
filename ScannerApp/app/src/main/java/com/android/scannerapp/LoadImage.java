package com.android.scannerapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static androidx.core.app.ActivityCompat.startActivityForResult;

public class LoadImage extends Activity {
    private ImageView imageView2;
    private Button exit;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.load_image_layout);
        imageView2 = (ImageView) findViewById(R.id.imageView2);

        final Intent intent = getIntent();

        if (getIntent().getExtras() != null) {
            imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            imageView2.setImageURI(imageUri);
        }

        exit = (Button)findViewById(R.id.btnExitLoadImage);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHomePage = new Intent(LoadImage.this, MainActivity.class);
                startActivity(openHomePage);
            }
        });
    }
}


