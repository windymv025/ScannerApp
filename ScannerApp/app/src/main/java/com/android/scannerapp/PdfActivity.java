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
    Intent intent;
    private final int CHOOSE_PDF_FROM_FILE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfView = (PDFView) findViewById(R.id.pdf_view);
        callChoosePdfFile();
    }

    public void callChoosePdfFile() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent = new Intent (Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, CHOOSE_PDF_FROM_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
//        super.onActivityResult(requestCode, resultCode, resultData);
//        if(requestCode == CHOOSE_PDF_FROM_FILE && resultCode == RESULT_OK) {
//            if(resultData != null) {
//                String path = resultData.getData().getPath();
//                Log.i("TEST", path);
//                file = new File(path);
//
////                pdfView.fromFile(file).load();
//                pdfView.fromAsset("pdf_book.pdf").load();
//
//            }
//        }

        switch(requestCode) {
            case CHOOSE_PDF_FROM_FILE:
                if(resultCode == RESULT_OK) {
                    String path = resultData.getData().getPath();
                    Log.i("TEST", path);
                    file = new File(path);

                    pdfView.fromFile(file).load();
                }
                break;
        }


    }

}
