package com.android.scannerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageOptions;

import java.io.File;
import java.security.Provider;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btnAdd;

    FloatingActionButton btnCamera;
    int REQUEST_CODE = 123;
    GridView gvThumbnail;
    public static  ArrayList<File> filelist = new ArrayList<File>();
    ThumbnailAdapter obj_adapter;
    File dir;

    FloatingActionButton btnLoadImage;
    private static final int PICK_IMAGE = 1;
    Uri imgUri;

    FloatingActionButton btnDrive;
    DriveServiceHelper driveServiceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        addControls();
        addEvents();
    }

    private void init() {
        gvThumbnail = (GridView) findViewById(R.id.gvThumbnail);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ Environment.DIRECTORY_PICTURES + "/ScannerApp");
            Toast.makeText(getApplicationContext(),Environment.getExternalStorageDirectory().getAbsolutePath().toString(),Toast.LENGTH_SHORT).show();

        }else {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ScannerApp");
        }
        getFile(dir);
        obj_adapter = new ThumbnailAdapter(getApplicationContext(),filelist);
        gvThumbnail.setAdapter(obj_adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
    private void addEvents() {
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CropImage.activity(imgUri).getIntent(getApplicationContext());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnLoadImage.getVisibility() == View.GONE && btnCamera.getVisibility() == View.GONE) {
                    btnLoadImage.setVisibility(View.VISIBLE);
                    btnCamera.setVisibility(View.VISIBLE);
                } else {
                    btnLoadImage.setVisibility(View.GONE);
                    btnCamera.setVisibility(View.GONE);
                }
            }
        });

        gvThumbnail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                xuLyChonFileTrenGridView(position);
            }
        });
    }

    private void xuLyChonFileTrenGridView(int position) {
        File f = filelist.get(position);
        Intent intent = new Intent(MainActivity.this, ViewImage.class);
        intent.putExtra("IMAGE_CHOOSE_ON_GRIDVIEW",f);
        startActivity(intent);
    }

    private void addControls() {
        //        Màn hình chụp ảnh
        btnCamera = findViewById(R.id.btn_Camera);

        // load image from gallery
        btnLoadImage = findViewById(R.id.btnLoadImage);
        //btnLoadImage.setVisibility(View.INVISIBLE);

        //upload Drive
        btnAdd = findViewById(R.id.btnAdd);

        gvThumbnail = (GridView) findViewById(R.id.gvThumbnail);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ Environment.DIRECTORY_PICTURES + "/ScannerApp");
            Toast.makeText(getApplicationContext(),Environment.getExternalStorageDirectory().getAbsolutePath().toString(),Toast.LENGTH_SHORT).show();

        }else {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ScannerApp");
        }
        getFile(dir);
        obj_adapter = new ThumbnailAdapter(getApplicationContext(),filelist);
        gvThumbnail.setAdapter(obj_adapter);
    }

    private ArrayList<File> getFile(File dir) {
        File listfile[] = dir.listFiles();
        if (listfile != null && listfile.length > 0) {
            for (int i = 0; i < listfile.length; i++) {
                if (listfile[i].isDirectory()) {
                    getFile(listfile[i]);
                } else {
                    boolean booleanimage = false;
                    if (listfile[i].getName().endsWith(".jpg") || listfile[i].getName().endsWith(".png") || listfile[i].getName().endsWith(".jpeg")) {
                        for (int j = 0; j < filelist.size(); j++) {
                            if (filelist.get(j).getName().equals(listfile[i].getName())) {
                                booleanimage = true;
                            }
                        }
                        if (booleanimage) {
                            booleanimage = false;
                        } else {
                            filelist.add(listfile[i]);
                        }
                    }
                }
            }
        }
        return filelist;
    }


    //     open gallery
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && null != data) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Intent openLoadImageForm = new Intent(getApplicationContext(), LoadImage.class);
                // Get uri
                imgUri = result.getUri();

                openLoadImageForm.putExtra("imageUri", imgUri.toString());
                startActivity(openLoadImageForm);
            } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
                imgUri =  data.getData();
                Intent openLoadImageForm = new Intent(getApplicationContext(), LoadImage.class);
                openLoadImageForm.putExtra("imageUri", imgUri.toString());
                startActivity(openLoadImageForm);
            } else {
                Toast.makeText(this, "No. ", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}