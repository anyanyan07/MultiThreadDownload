package com.ayy.multithreaddownload;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import java.io.File;

public class DownloadUtil {
    private static final String TAG = "DownloadUtil";

    private String url;
    private String savePath;
    private Callback callback;
    private Handler handler;
    public static final int MSG_UPDATE_PROGRESS = 0;
    public static final int MSG_COMPLETE = 1;


    public DownloadUtil(@NonNull String url, @NonNull String savePath, Callback callback) {
        this.url = url;
        this.callback = callback;
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdir();
        }
        int lastIndexOf = url.lastIndexOf("/");
        String fileName = url.substring(lastIndexOf + 1);
//        this.savePath = savePath + "/" + fileName;
        file = new File(savePath,fileName);
        file.delete();
        this.savePath = file.getAbsolutePath();
    }

    @SuppressLint("HandlerLeak")
    public void download() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_PROGRESS:
                        if (callback != null) {
                            callback.progressUpdate(msg.arg1, msg.arg2);
                        }
                        break;
                    case MSG_COMPLETE:
                        if (callback != null) {
                            callback.complete();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        new DownloadTask(url, savePath, handler, callback).start();
    }
}
