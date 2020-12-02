package com.android.scannerapp;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static androidx.core.app.ActivityCompat.startActivityForResult;

public class LoadImage extends Activity {
    private ImageView cropImage, exit;
    private ImageView Save;
    //public String image_name="Document";
    Uri imageUri;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.load_image_layout);

        cropImage = (ImageView) findViewById(R.id.cropImageView);
        ActivityCompat.requestPermissions(LoadImage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(LoadImage.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


        final Intent intent = getIntent();

        if (getIntent().getExtras() != null) {
            imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            cropImage.setImageURI(imageUri);

        }
        Save = findViewById(R.id.Save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToInternalStorage();
                Toast.makeText(getApplicationContext(),"Image saved successfully", Toast.LENGTH_SHORT).show();
                Intent openHomePage = new Intent(LoadImage.this, MainActivity.class);
                startActivity(openHomePage);

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
    private void saveToInternalStorage(){
        bitmapDrawable = (BitmapDrawable) cropImage.getDrawable();
        bitmap = bitmapDrawable.getBitmap();
        FileOutputStream fos = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath(), "/ScannerApp");
        dir.mkdirs();

        String filename = String.format("%d.jpg",System.currentTimeMillis());
        File outFile = new File(dir, filename);

        try {
            fos = new FileOutputStream(outFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        try{
            fos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
        try{
            fos.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}


