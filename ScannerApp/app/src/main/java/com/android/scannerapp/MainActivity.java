package com.android.scannerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.theartofdev.edmodo.cropper.CropImage;

import java.security.Provider;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {

    ImageButton btnCamera;
    int REQUEST_CODE = 123;
    ListView lvThumbnail;
    ArrayList<Thumbnail> arrayThumb;

    ImageView imgView;
    Button btnLoadImage;
    private static final int PICK_IMAGE = 1;
    Uri imgUri;

    Button btnDrive;
    DriveServiceHelper driveServiceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load cac thumbnail cua hinh da chup o home
        lvThumbnail = (ListView) findViewById(R.id.lvThumbnail);
        arrayThumb = new ArrayList<Thumbnail>();

        arrayThumb.add(new Thumbnail(R.drawable.boy, "Doc1", "01/01/2020"));
        arrayThumb.add(new Thumbnail(R.drawable.boy_2, "Doc2", "01/02/2020"));
        arrayThumb.add(new Thumbnail(R.drawable.woman_2, "Doc3", "01/03/2020"));
        arrayThumb.add(new Thumbnail(R.drawable.boy, "Doc4", "01/04/2020"));

        ThumbnailAdapter TNAdapter = new ThumbnailAdapter(
                MainActivity.this,
                R.layout.row_thumbnail,
                arrayThumb
        );

        lvThumbnail.setAdapter(TNAdapter);
//        Màn hình chụp ảnh
        btnCamera = (ImageButton) findViewById(R.id.btn_Camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CropImage.activity(imgUri).getIntent(getApplicationContext());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // load image from gallery
        imgView = (ImageView) findViewById(R.id.imageView);
        btnLoadImage = (Button) findViewById(R.id.btnLoadImage);
        btnLoadImage.setVisibility(View.INVISIBLE);
        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        //upload Drive
        btnDrive = (Button) findViewById(R.id.btn_UploadDrive);
        btnDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSignIn();
                // uploadPdfFile(v);
            }
        });

    }

    //     open gallery
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            super.onActivityResult(requestCode, resultCode, data);
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && null != data) {
                    Intent openLoadImageForm = new Intent(getApplicationContext(), LoadImage.class);

                    // Get uri
                    imgUri = result.getUri();
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
//                    int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
//                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                    // Set image
                    imgView.setImageURI(imgUri);

                    openLoadImageForm.putExtra("imageUri", imgUri.toString());
                    startActivity(openLoadImageForm);
                }else {
                    Toast.makeText(this, "No. ", Toast.LENGTH_LONG).show();
                }


        } catch (Exception e) {
            Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        //drive
        switch (requestCode) {
            case 400:
                if (resultCode == RESULT_OK) {
                    handleSignInIntent(data);
                }
        }
    }

    private void handleSignInIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {

                        GoogleAccountCredential credential =
                                GoogleAccountCredential.usingOAuth2(MainActivity.this, Collections.singleton(Scopes.DRIVE_FILE));

                        credential.setSelectedAccount(googleSignInAccount.getAccount());
                        Drive googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("Scanner App")
                                .build();

                        driveServiceHelper = new DriveServiceHelper(googleDriveService);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    //upload Drive
    private void requestSignIn() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.DRIVE_FILE))
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(client.getSignInIntent(), 400);

    }


    public void uploadPdfFile(View v) {
        String filePath = "/storage/emulated/0/mypdf.pdf";

        driveServiceHelper.createFilePDF(filePath).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(getApplicationContext(), "upload successfully", Toast.LENGTH_LONG).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "check your google drive api key", Toast.LENGTH_LONG).show();
                    }
                });

    }

}