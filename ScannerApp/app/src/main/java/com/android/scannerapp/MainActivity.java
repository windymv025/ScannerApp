package com.android.scannerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.adapter.ThumbnailAdapter;
import com.android.io.IOPdfDocument;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 123;
    private static final int PICK_IMAGE = 1;

    private FloatingActionButton btnAdd;
    private LinearLayout layoutButtonAdd;
    private FloatingActionButton btnCamera;
    private GridView gvThumbnail;
    private FloatingActionButton btnLoadImage;
    private SearchView searchView;
    private FloatingActionButton btnSearch;
    private FloatingActionButton btnLoadPdf;

    private ArrayList<File> filelist = new ArrayList<File>();
    private ThumbnailAdapter obj_adapter;
    private File dir;
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        addControls();
        addEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
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
                //imgUri = result.getUri();
                imgUri = data.getData();
                Intent openLoadImageForm = new Intent(getApplicationContext(), LoadImage.class);
                openLoadImageForm.putExtra("imageUri", imgUri.toString());
                startActivity(openLoadImageForm);
            } else {
                //Toast.makeText(this, "No. ", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void init() {
        gvThumbnail = (GridView) findViewById(R.id.gvThumbnail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES + "/ScannerApp");
        } else {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ScannerApp");
        }
        if (!dir.exists()) {
            dir.mkdir();
        }
        getFile(dir);
        obj_adapter = new ThumbnailAdapter(getApplicationContext(), filelist);
        gvThumbnail.setAdapter(obj_adapter);
    }

    private void addControls() {
        searchView = findViewById(R.id.searchViewFileName);
        btnSearch = findViewById(R.id.btnSearch);
        //        Màn hình chụp ảnh
        btnCamera = findViewById(R.id.btn_Camera);

        // load image from gallery
        btnLoadImage = findViewById(R.id.btnLoadImage);
        //btnLoadImage.setVisibility(View.INVISIBLE);

        btnAdd = findViewById(R.id.btnAdd);
        layoutButtonAdd = findViewById(R.id.layoutButtonAdd);

        gvThumbnail = (GridView) findViewById(R.id.gvThumbnail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES + "/ScannerApp");

        } else {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ScannerApp");
        }
        getFile(dir);
        obj_adapter = new ThumbnailAdapter(getApplicationContext(), filelist);
        gvThumbnail.setAdapter(obj_adapter);
        btnLoadPdf = findViewById(R.id.btnLoadPdf);
    }

    private void addEvents() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchView.getVisibility() == View.GONE) {
                    searchView.setVisibility(View.VISIBLE);
                } else {
                    searchView.setVisibility(View.GONE);
                }
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //xuLyTimKiemFileName();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CropImage.activity(imgUri).getIntent(MainActivity.this);
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
                    layoutButtonAdd.setOrientation(LinearLayout.HORIZONTAL);
                } else {
                    btnLoadImage.setVisibility(View.GONE);
                    btnCamera.setVisibility(View.GONE);
                    layoutButtonAdd.setOrientation(LinearLayout.VERTICAL);
                }
            }
        });

        gvThumbnail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                xuLyChonFileTrenGridView(position);
            }
        });

        gvThumbnail.setOnItemLongClickListener(new ItemLongClickRemove());

        // doc file pdf
        btnLoadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PdfActivity.class);
                startActivity(i);
            }
        });
    }

    private void xuLyTimKiemFileName() {
        String namefile = searchView.getQuery().toString();
        ArrayList<File> list = new ArrayList<>();
        for (File f : filelist) {
            String[] temp = f.getPath().split("/");
            String namePath = temp[temp.length - 1];
            if (namePath.contains(namefile)) {
                list.add(f);
            }
        }
        filelist = list;
        obj_adapter.notifyDataSetChanged();
    }

    private void xuLyChonFileTrenGridView(int position) {
        File f = filelist.get(position);
        File pdfFile = IOPdfDocument.getFilePDFInGridView(f.getName().substring(0,f.getName().lastIndexOf('.')));
        Intent intent = new Intent(MainActivity.this, ViewImage.class);
        intent.putExtra("IMAGE_CHOOSE_ON_GRIDVIEW", f);
        intent.putExtra("PDF_FILE_CHOOSE", pdfFile);
        startActivity(intent);
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
    //delete picture
    public class ItemLongClickRemove implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView parent, View view, final int position, long id) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setMessage("Bạn có muốn xóa hình ảnh này!");
            alertDialogBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File listfile[] = dir.listFiles();
                    // delete picture
                    filelist.remove(position);
                    listfile[position].delete();
                    //update gridview
                    obj_adapter.notifyDataSetChanged();

                }
            });
            alertDialogBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialogBuilder.show();
            return true;
        }
    }
}
