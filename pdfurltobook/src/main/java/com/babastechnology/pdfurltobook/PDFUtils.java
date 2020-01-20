package com.babastechnology.pdfurltobook;

import android.content.Context;
import android.content.Intent;

public class PDFUtils {

    //String Values to be Used in App
    public static final String PDFUrl = "PdfUrlToBookPager_Library_TechnicalBaba";
    public static String downloadDirectory = "TechnicalBaba Downloads";
    public static final String PdfFileName = "TechnicalBaba.pdf";


    public static void openPdfBook(Context context,String url)
    {
        context.startActivity(new Intent(context, PdfUrlToBookPagerActivity.class).putExtra(PDFUtils.PDFUrl,url));
    }
}