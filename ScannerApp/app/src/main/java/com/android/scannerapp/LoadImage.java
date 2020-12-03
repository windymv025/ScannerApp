package com.android.scannerapp;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static androidx.core.app.ActivityCompat.startActivityForResult;

public class LoadImage extends Activity {
    private ImageView cropImage, exit;
    private ImageView Save;
    Uri imageUri;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.load_image_layout);
        Context context;
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
                bitmapDrawable = (BitmapDrawable)cropImage.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                try {
                    saveBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


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


    private void saveBitmap(Bitmap bitmap) throws IOException{
        String fileName = System.currentTimeMillis()+".jpg";
        OutputStream outputStream;
        boolean isSaved;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ScannerApp");
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            outputStream = resolver.openOutputStream(uri);
        }else {
            String path = Environment.getExternalStorageDirectory().toString();
            File folder = new File(path + "/" + "ScannerApp");
            if(!folder.exists()){
            folder.mkdirs();}

            File file = new File(folder, fileName);
            if(file.exists())
                file.delete();


            outputStream = new FileOutputStream(file);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));


            }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        }

    }



