package com.android.scannerapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.io.IOPdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class LoadImage extends Activity {
    private ImageView cropImage, exit;
    private ImageView Save;
    private Spinner Share;
    private EditText txtNamePdfFile;

    private String[] listChooseShare = new String[]{"Share with PDF file", "Share with JPEG file"};
    private ArrayAdapter<String> adapterShare;

    private Uri imageUri;
    private BitmapDrawable bitmapDrawable;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.load_image_layout);

        loaded();
        addControls();
        addEvents();
    }

    private void loaded() {
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
    }

    private void addControls() {
        txtNamePdfFile = findViewById(R.id.txtNamePdfFile);
        cropImage = findViewById(R.id.cropImageView);

        final Intent intent = getIntent();

        if (intent.getExtras() != null) {
            imageUri = Uri.parse(intent.getStringExtra("imageUri"));
            cropImage.setImageURI(imageUri);
        }

        Save = findViewById(R.id.Save);
        exit = findViewById(R.id.btnExitLoadImage);

        Share = findViewById(R.id.Share);
        adapterShare = new ArrayAdapter<>(LoadImage.this, android.R.layout.simple_spinner_item, listChooseShare);
        adapterShare.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.Share.setAdapter(adapterShare);
    }

    private void addEvents() {
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = new String();
                if (txtNamePdfFile.getText().toString().trim().isEmpty()) {
                    fileName = System.currentTimeMillis() + "";
                } else {
                    fileName = txtNamePdfFile.getText().toString().trim();
                    if (fileName.contains(".pdf")) {
                        fileName = fileName.substring(0, fileName.lastIndexOf(".pdf"));
                    }
                }
                bitmapDrawable = (BitmapDrawable) cropImage.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                Thread t = new Thread(new IOPdfDocument(bitmap, fileName + ".pdf"));
                t.start();

                saveBitmap(bitmap, fileName);

                Intent openHomePage = new Intent(LoadImage.this, MainActivity.class);
                startActivity(openHomePage);
                Toast.makeText(getApplicationContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHomePage = new Intent(LoadImage.this, MainActivity.class);
                startActivity(openHomePage);
                finish();
            }
        });

        Share.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                } else {
                    shareImageActivity();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void saveBitmap(Bitmap bitmap, String fileName) {
        try {
            fileName += ".jpeg";
            OutputStream outputStream;
            boolean isSaved;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ScannerApp");
                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                outputStream = resolver.openOutputStream(uri);
            } else {
                String path = Environment.getExternalStorageDirectory().toString();
                File folder = new File(path + "/" + "ScannerApp");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                File file = new File(folder, fileName);
                if (file.exists())
                    file.delete();

                outputStream = new FileOutputStream(file);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Log.e("<Save image>", e.getLocalizedMessage());
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private void shareImageActivity() {
        bitmapDrawable = (BitmapDrawable) cropImage.getDrawable();
        bitmap = bitmapDrawable.getBitmap();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);

        try {
            OutputStream outputStream = null;
            File file = Environment.getExternalStorageDirectory();
            File dir = new File(file.getAbsolutePath(), "/ScannerApp");
            dir.mkdir();

            try {
                outputStream = getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "share with"));

            Toast.makeText(getApplicationContext(), "Share successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(LoadImage.this, "Error in sharing.", Toast.LENGTH_SHORT).show();
        }

    }
}


