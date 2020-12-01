package com.android.scannerapp;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static androidx.core.app.ActivityCompat.startActivityForResult;

public class LoadImage extends Activity {
    private ImageView imageView2, exit;
    private ImageView Save;
    //public String image_name="Document";
    Uri imageUri;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;
    private String filepath = "AAAScannerApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.load_image_layout);

        imageView2 = (ImageView) findViewById(R.id.imageView2);

        final Intent intent = getIntent();

        if (getIntent().getExtras() != null) {
            imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            imageView2.setImageURI(imageUri);
            bitmapDrawable = (BitmapDrawable) imageView2.getDrawable();
            bitmap = bitmapDrawable.getBitmap();
        }

        Save = findViewById(R.id.Save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "working", Toast.LENGTH_SHORT).show();
                new ImageSaver(getApplicationContext()).setFileName("MyDocument").setDirectoryName("/ScannerApp").save(bitmap);
                Intent openHomePage = new Intent(LoadImage.this, MainActivity.class);
                startActivity(openHomePage);
                Toast.makeText(getApplicationContext(),"Image saved successfully", Toast.LENGTH_SHORT).show();
            }
        });



        exit = (ImageView) findViewById(R.id.btnExitLoadImage);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHomePage = new Intent(LoadImage.this, MainActivity.class);
                startActivity(openHomePage);
            }
        });
    }

}


