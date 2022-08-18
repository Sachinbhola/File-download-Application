 package com.example.myapplication;

import static android.content.ContentValues.TAG;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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
            }
        });

    }

     private void receiveDownloadStatusBroadcast() {

        // To receive Download complete action from Download Manager
         BroadcastReceiver attachmentDownloadCompleteReceive = new BroadcastReceiver() {
             private Context context;
             private Intent intent;

             @Override
             public void onReceive(Context context, Intent intent) {
                 this.context = context;
                 this.intent = intent;
                 String action = intent.getAction();
             }
         };

         MainActivity.this.registerReceiver(attachmentDownloadCompleteReceive, new IntentFilter(
                 DownloadManager.ACTION_DOWNLOAD_COMPLETE));
     }

     private void downloadFromURL(String url) {

        // check for active internet connection
         ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

         if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){

             try{
                 download_progressBar.setVisibility(View.VISIBLE);
                 DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                 String title = URLUtil.guessFileName(url,null,null);
                 request.setTitle(title);
                 request.setDescription("Downloading file");
                 String cookie = CookieManager.getInstance().getCookie(url);
                 request.addRequestHeader("cookie",cookie);
                 request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                 //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title);
                 request.setDestinationInExternalFilesDir(this,"/path/to/save/file",title);
                 DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                 downloadManager.enqueue(request);
             }
             catch (Exception e) {
                 Log.e(TAG, "onReceive: "+ e );
                 e.printStackTrace();
             }
         }
         else{
             Toast.makeText(MainActivity.this,"Make sure that you have an active Internet Connection",Toast.LENGTH_LONG).show();
         }
     }

 }