package com.android.io;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOPdfDocument implements Runnable {
    static final int WIDTH_PAGE = 1200;
    static final int HEIGHT_PAGE = 2010;

    private File imageFile;
    private File pdfFile;
    private Bitmap bitmap, scanledbmp;

    public IOPdfDocument(File imageFile, String filePDFname) {
        setup();
        this.imageFile = imageFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DOCUMENTS + "/ScannerApp/" + filePDFname);
        } else {
            pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ScannerApp/" + filePDFname);
        }

        if (pdfFile.exists()) {
            pdfFile.mkdirs();
        }

        if (this.imageFile.exists()) {
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            scanledbmp = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
        }
    }

    public IOPdfDocument(Bitmap bitmapImage, String filePDFname) {
        setup();
        this.bitmap = bitmapImage;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DOCUMENTS + "/ScannerApp/" + filePDFname);

        } else {
            pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ScannerApp/" + filePDFname);
        }

        scanledbmp = Bitmap.createScaledBitmap(bitmap, WIDTH_PAGE, WIDTH_PAGE * bitmap.getWidth() / bitmap.getHeight(), false);
    }

    private void setup() {
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DOCUMENTS + "/ScannerApp");

        } else {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ScannerApp");
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void run() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(WIDTH_PAGE, HEIGHT_PAGE, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        canvas.drawBitmap(scanledbmp, 0, 0, paint);

        pdfDocument.finishPage(page);

        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Log.e("<Write file PDF>", "Wite file finish!!!");
        } catch (IOException e) {
            Log.e("<Write file PDF>", e.getLocalizedMessage());
        }

    }
}
