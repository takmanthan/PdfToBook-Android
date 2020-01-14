package com.babastechnology.pdfurltobook;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.babastechnology.pdfurltobook.adptor.PDFPagerAdaptor;
import com.babastechnology.pdfurltobook.task.DownloadPDFTask;
import com.babastechnology.pdfurltobook.task.OnDownloadCompleteListner;
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer;

import java.io.File;
import java.util.ArrayList;

public class PdfUrlToBookPagerActivity extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private CustomeViewPager pdfViewPager;
    private ParcelFileDescriptor mFileDescriptor;
    private PdfRenderer mPdfRenderer;
    private PdfRenderer.Page mCurrentPage;
    private static String PDFUrl = "";
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pdf_url_to_book_pager);

        //get url from last activity with intent
        if (getIntent().hasExtra(PDFUtils.PDFUrl))
        {
            PDFUrl = getIntent().getStringExtra(PDFUtils.PDFUrl);
        }

        initViews();
    }

    /**
     * use for find view by id
     * check storage permission
     */
    private void initViews() {
        pdfViewPager = findViewById(R.id.pdfViewPager);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            downloadPdfFromUrl();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }

    }

    /**
     * The funcation use for downlaod pdf from url
     */
    private void downloadPdfFromUrl() {
        if (urlIsValid(PDFUrl))
        {
            new DownloadPDFTask(this, PDFUrl, new OnDownloadCompleteListner() {
                @Override
                public void onPdfDownload(File file) {
                    openRenderer(file);
                }
            });
        }
    }

    /**
     * 1. Check Internet connectivity
     * 2. Check url validation
     *
     * @param url
     * @return
     */
    private Boolean urlIsValid(String url)
    {
        if (!isConnectingToInternet())
        {
            showToast(this,getString(R.string.no_internet_message));
        }
        if (TextUtils.isEmpty(url))
        {
            showToast(this,getString(R.string.empty_message));
        }
        if (!url.contains(".pdf"))
        {
            showToast(this,getString(R.string.validation_message));
        }
        return true;
    }


    /**
     * This function use for rendering PDF from local storage
     * @param file
     */
    private void openRenderer(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // In this sample, we read a PDF from the assets directory.
            //   File f = new File("/storage/emulated/0/Androhub Downloads/PiccolinoMagazine.pdf");
            try {
                mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                mPdfRenderer = new PdfRenderer(mFileDescriptor);
                int count = mPdfRenderer.getPageCount();
                if (count > 0) {
                    for (int index = 0; index < count; index++) {
                        bitmapArrayList.add(convertPDFPageToBitmap(index));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (bitmapArrayList.size() > 0) {
            pdfViewPager.setAdapter(new PDFPagerAdaptor(this, bitmapArrayList));

            // Create an object of page transformer
            BookFlipPageTransformer bookFlipPageTransformer = new BookFlipPageTransformer();

// Enable / Disable scaling while flipping. If true, then next page will scale in (zoom in). By default, its true.
            bookFlipPageTransformer.setEnableScale(true);

// The amount of scale the page will zoom. By default, its 5 percent.
            bookFlipPageTransformer.setScaleAmountPercent(15f);

// Assign the page transformer to the ViewPager.
            pdfViewPager.setPageTransformer(true, bookFlipPageTransformer);
        }
    }

    /**
     * The function use for convert PDF to Bitmap
     * @param index
     * @return
     */
    private Bitmap convertPDFPageToBitmap(int index) {

        Bitmap newBitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Make sure to close the current page before opening another one.
            if (null != mCurrentPage) {
                mCurrentPage.close();
            }
            // Use `openPage` to open a specific page in PDF.
            mCurrentPage = mPdfRenderer.openPage(index);
            // Important: the destination bitmap must be ARGB (not RGB).
            Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
                    Bitmap.Config.ARGB_8888);

            // Create new bitmap based on the size and config of the old
            newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

// Instantiate a canvas and prepare it to paint to the new bitmap
            Canvas canvas = new Canvas(newBitmap);

// Paint it white (or whatever color you want)
            canvas.drawColor(Color.WHITE);

// Draw the old bitmap ontop of the new white one
            canvas.drawBitmap(bitmap, 0, 0, null);

            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get
            // the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            mCurrentPage.render(newBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        }
        return newBitmap;

    }

    /**
     * Check Device is connected to internet or not
     * using Connectivity Manager
     * @return
     */
    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    /**
     * onRequestPermissionsResult is give us the result of
     * Permissions (Like in this code we have Storage permission)
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadPdfFromUrl();
            } else {
                Toast.makeText(this, "try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void showToast(Context context,String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

}
