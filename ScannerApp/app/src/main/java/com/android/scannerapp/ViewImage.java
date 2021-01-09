package com.android.scannerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.io.IOPdfDocument;

import java.io.File;
import java.io.OutputStream;

public class ViewImage extends AppCompatActivity {

    ImageView imageView;
    Spinner spinnerShare;
    ImageView btnDelete;
    ImageView btnBack;
    String[] listChooseShare = new String[]{"", "Share with PDF file", "Share with JPEG file"};
    ArrayAdapter<String> adapterShare;

    String fileName = new String();
    File pdfFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        addCotrols();
        addEvents();
    }

    private void addCotrols() {
        imageView = findViewById(R.id.imageView);
        Intent intent = getIntent();
        File imageFile = (File) intent.getSerializableExtra("IMAGE_CHOOSE_ON_GRIDVIEW");
        pdfFile = (File) intent.getSerializableExtra("PDF_FILE_CHOOSE");
        if (imageFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }

        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        spinnerShare = findViewById(R.id.spinnerShare);
        adapterShare = new ArrayAdapter<>(ViewImage.this, android.R.layout.simple_spinner_item, listChooseShare);
        adapterShare.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerShare.setAdapter(adapterShare);
        //spinnerShare.setSelection(-1);
    }

    private void addEvents() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyDeleteFile();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewImage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        spinnerShare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xuLyLuChonChiaSe(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerShare.setSelection(0);
            }
        });
    }

    private void xuLyLuChonChiaSe(int position) {
        Uri imageUri;
        BitmapDrawable bitmapDrawable;
        Bitmap bitmap;

        if (position == 1) {
//            Uri uri;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", pdfFile);
//            } else {
//                uri = Uri.fromFile(pdfFile);
//            }
//            Intent share = new Intent();
//            share.setAction(Intent.ACTION_SEND);
//
//            if(pdfFile.exists()) {
//                share.setType("application/pdf");
//                share.putExtra(Intent.EXTRA_STREAM, uri);
//
//                startActivity(Intent.createChooser(share, "share with"));
//            }

            //startActivity(share);
        } else if (position == 2) {
            bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
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

            } catch (Exception e) {
                Toast.makeText(this, "Error in sharing.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void xuLyDeleteFile() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewImage.this);
        alertDialogBuilder.setMessage("Bán có muốn xóa hình ảnh này!");
        alertDialogBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent deleteIntent = new Intent(ViewImage.this, MainActivity.class);
                Intent intent = getIntent();
                File imageFile = (File) intent.getSerializableExtra("IMAGE_CHOOSE_ON_GRIDVIEW");
                File f = new File(imageFile.getPath());
                f.delete();
                startActivity(deleteIntent);

            }
        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialogBuilder.show();
    }


}