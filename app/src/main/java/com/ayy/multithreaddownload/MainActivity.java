package com.ayy.multithreaddownload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private String downloadUrl = "http://192.168.199.240:8081/static/ayy/qqmusic.apk";
//    private String downloadUrl = "http://192.168.199.240:8081/static/ayy/b.jpg";
    private String filePath = "";

    private ImageView iv;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.result);
        progressBar = findViewById(R.id.progress);
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ayy";
    }

    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.download:
                final long startTime = System.currentTimeMillis();
                new DownloadUtil(downloadUrl, filePath, new Callback() {
                    @Override
                    public void progressUpdate(int finishedLength, int totalLength) {
                        progressBar.setMax(totalLength);
                        progressBar.setProgress(finishedLength);
                    }

                    @Override
                    public void complete() {
                        long endTime = System.currentTimeMillis();
                        Log.e(TAG, "complete: 用时：" + (endTime - startTime) / 1000);
//                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//                        iv.setImageBitmap(bitmap);
                    }
                }).download();
                break;
            case R.id.show:
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                iv.setImageBitmap(bitmap);
                break;
            default:
                break;
        }
    }
}
