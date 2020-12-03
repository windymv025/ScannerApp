package com.android.scannerapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
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
    private ImageView Share;
    //public String image_name="Document";
    Uri imageUri;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.load_image_layout);

        cropImage = (ImageView) findViewById(R.id.cropImageView);
        ActivityCompat.requestPermissions(LoadImage.this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                1
        );
        ActivityCompat.requestPermissions(LoadImage.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                1
        );


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

        Share = findViewById(R.id.Share);
        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareActivity();
                Toast.makeText(getApplicationContext(), "Share successfully", Toast.LENGTH_SHORT).show();
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
    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }
    private void shareActivity()
    {
        bitmapDrawable = (BitmapDrawable) cropImage.getDrawable();
        bitmap = bitmapDrawable.getBitmap();
        try {
            FileOutputStream fos = null;
            File file = Environment.getExternalStorageDirectory();
            File dir = new File(file.getAbsolutePath(), "/ScannerApp");
            dir.mkdir();

            String filename = String.format("%d.jpeg", System.currentTimeMillis());
            File outFile = new File(dir, filename);
            try {
                fos = new FileOutputStream(outFile);

            } catch (Exception e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            try {
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");

            share.putExtra(Intent.EXTRA_STREAM,
                    Uri.parse(filename));

            startActivity(Intent.createChooser(share, "share with"));

            //outFile.delete();

        } catch (Exception e) {
            Toast.makeText(LoadImage.this, "Error in sharing.", Toast.LENGTH_SHORT).show();
        }

    }
}


