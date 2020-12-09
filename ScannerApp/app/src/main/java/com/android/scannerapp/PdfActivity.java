package com.android.scannerapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

public class PdfActivity extends AppCompatActivity {
    PDFView pdfBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfBook = (PDFView) findViewById(R.id.pdf_view);
        pdfBook.fromAsset("pdf_book.pdf").load();
    }
}
