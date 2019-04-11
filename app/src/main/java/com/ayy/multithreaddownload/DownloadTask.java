package com.ayy.multithreaddownload;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadTask extends Thread {

    private static final String TAG = "DownloadTask";

    private String url;
    private String savePath;
    private ExecutorService executorService;
    private int totalLength;
    private List<DownloadThread> downloadThreads;
    private boolean finished;
    private Handler handler;
    private Callback callback;

    public DownloadTask(String url, String savePath, Handler handler,Callback callback) {
        this.url = url;
        this.savePath = savePath;
        this.handler = handler;
        this.callback = callback;
    }

    /**
     * 获取文件总长度，分配给子线程下载
     */
    @Override
    public void run() {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(cpuCount);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                totalLength = connection.getContentLength();
                connection.disconnect();
                downloadThreads = new ArrayList<>(cpuCount);
                int avgLength = totalLength / cpuCount;
                for (int i = 0; i < cpuCount; i++) {
                    int startIndex = i * avgLength;
                    int endIndex = (i + 1) * avgLength - 1;
                    if (i == cpuCount - 1) {
                        endIndex = totalLength;
                    }
                    DownloadThread downloadThread = new DownloadThread(url, savePath, startIndex, endIndex);
                    executorService.execute(downloadThread);
                    downloadThreads.add(downloadThread);
                }
                //发消息给UI线程，更新进度
                while (!finished) {
                    //回调在主线程
                    int finishedProgress = publishProgress();
                    Message message = handler.obtainMessage();
                    message.arg1 = finishedProgress;
                    message.arg2 = totalLength;
                    message.what = DownloadUtil.MSG_UPDATE_PROGRESS;
                    handler.sendMessage(message);
                    if (finishedProgress >= totalLength) {
                        finished = true;
                        Message completeMsg = handler.obtainMessage();
                        completeMsg.what = DownloadUtil.MSG_COMPLETE;
                        handler.sendMessage(completeMsg);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int publishProgress() {
        int finishedLength = 0;
        for (DownloadThread downloadThread : downloadThreads) {
            finishedLength += downloadThread.getFinishedLength();
        }
        return finishedLength;
    }
}
