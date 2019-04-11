package com.ayy.multithreaddownload;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread implements Runnable {

    private static final String TAG = "DownloadThread";

    private String url;
    private String savePath;
    private int startIndex;
    private int endIndex;
    private int finishedLength;

    public DownloadThread(String url, String savePath, int startIndex, int endIndex) {
        this.url = url;
        this.savePath = savePath;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
            connection.setAllowUserInteraction(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == 206) {
                inputStream = connection.getInputStream();
                randomAccessFile = new RandomAccessFile(savePath, "rwd");
                randomAccessFile.seek(startIndex);
                byte[] buffer = new byte[1024 * 10];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, length);
                    finishedLength += length;
                    Log.e(TAG, "run: " + Thread.currentThread().getId() + "当前加载：" + finishedLength);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getFinishedLength() {
        return finishedLength;
    }
}
