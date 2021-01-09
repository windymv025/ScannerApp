package com.android.scannerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PdfActivity extends AppCompatActivity {
    PDFView pdfView;
    File file;
    private final int CHOOSE_PDF_FROM_FILE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfView = (PDFView) findViewById(R.id.pdf_view);
        callChooseFileFromDevice();
    }

    private void callChooseFileFromDevice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, CHOOSE_PDF_FROM_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if(requestCode == CHOOSE_PDF_FROM_FILE && resultCode == RESULT_OK) {
            if(resultData != null) {
                file = new File(String.valueOf(resultData.getData()));
                pdfView.fromFile(file).load();
            }
        }
    }
}
