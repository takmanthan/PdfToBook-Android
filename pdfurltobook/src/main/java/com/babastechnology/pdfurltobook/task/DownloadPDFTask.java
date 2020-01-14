package com.babastechnology.pdfurltobook.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.babastechnology.pdfurltobook.PDFUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadPDFTask {

    private static final String TAG = "Download Task";
    private Context context;
    private String downloadUrl = "";
    public static ProgressDialog progressDialog = null;
    OnDownloadCompleteListner completeListner;

    public DownloadPDFTask(Context context,String downloadUrl, OnDownloadCompleteListner completeListner) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.completeListner = completeListner;

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinnerStart(context);
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    completeListner.onPdfDownload(outputFile);
                    spinnerStop();
                } else {
                    Toast.makeText(context, "Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Download Failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Please try again.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());
            }
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }

                apkStorage = new File(
                        Environment.getExternalStorageDirectory() + "/"
                                + PDFUtils.downloadDirectory);
                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, PDFUtils.PdfFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }


                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                Log.e(TAG, "File PAth :-  "+outputFile.getAbsolutePath());

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }

    public void spinnerStart(Context context) {
        String pleaseWait = "Loading...";
        spinnerStop();
        progressDialog = ProgressDialog.show(context, "", pleaseWait, true);
    }

    public void spinnerStop() {

        try {

            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
