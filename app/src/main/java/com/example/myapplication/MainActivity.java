 package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

 public class MainActivity extends AppCompatActivity {

     private Button download_button;
     private ProgressBar download_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download_button = findViewById(R.id.btn_download);
        download_progressBar=findViewById(R.id.pb_download);

        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.adobe.com/support/products/enterprise/knowledgecenter/media/c4611_sample_explain.pdf";

                downloadFromURL(url);
                receiveDownloadStatusBroadcast();
                //percentageDownload(url);
            }
        });

    }

     /*
     private void percentageDownload(String sampleurl) {

         ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
         Handler handler = new Handler(Looper.getMainLooper());

         executor.execute(new Runnable() {

             int count;

             @Override
             public void run() {

                 //Background work here
                 try {

                     // put your url.this is sample url.
                     URL url = new URL(sampleurl);
                     URLConnection conection = url.openConnection();
                     conection.connect();



                     int lenghtOfFile = conection.getContentLength();

                     // download the file

                     InputStream input = conection.getInputStream();

                     //catalogfile is your destenition folder
                     OutputStream output = new FileOutputStream(Environment.DIRECTORY_DOWNLOADS);


                     byte data[] = new byte[1024];

                     long total = 0;

                     while ((count = input.read(data)) != -1) {
                         total += count;
                         // publishing the progress....


                         publishProgress(Integer.valueOf("" + (int) ((total * 100) / lenghtOfFile)));

                         // writing data to file
                         output.write(data, 0, count);
                     }

                     // flushing output
                     output.flush();

                     // closing streams
                     output.close();
                     input.close();


                     handler.post(new Runnable() {
                         @Override
                         public void run() {
                             //UI Thread work here
                             download_progressBar.setVisibility(View.GONE);

                         }
                     });
                 } catch (Exception e) {

                 }
             }
         });
     }

      */

     private void receiveDownloadStatusBroadcast() {
         BroadcastReceiver attachmentDownloadCompleteReceive = new BroadcastReceiver() {
             private Context context;
             private Intent intent;

             @Override
             public void onReceive(Context context, Intent intent) {
                 this.context = context;
                 this.intent = intent;
                 String action = intent.getAction();
                 if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                     Toast.makeText(MainActivity.this,"Completed",Toast.LENGTH_LONG).show();
                     download_progressBar.setVisibility(View.INVISIBLE);
                 }
             }
         };

         MainActivity.this.registerReceiver(attachmentDownloadCompleteReceive, new IntentFilter(
                 DownloadManager.ACTION_DOWNLOAD_COMPLETE));
     }

     private void downloadFromURL(String url) {
         download_progressBar.setVisibility(View.VISIBLE);
         DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
         String title = URLUtil.guessFileName(url,null,null);
         request.setTitle(title);
         request.setDescription("Downloading file");
         String cookie = CookieManager.getInstance().getCookie(url);
         request.addRequestHeader("cookie",cookie);
         request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
         request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title);
         DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
         downloadManager.enqueue(request);

     }
     private void publishProgress(Integer... progress) {

         download_progressBar.setProgress(progress[0]);

     }

 }