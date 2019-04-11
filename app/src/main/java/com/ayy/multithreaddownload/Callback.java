package com.ayy.multithreaddownload;

import android.util.Log;

public abstract class Callback implements DownloadCallBack {

    @Override
    public void error() {
        Log.e("DownloadCallBack", "error: 下载失败");
    }
}
