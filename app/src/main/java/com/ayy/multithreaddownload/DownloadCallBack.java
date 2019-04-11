package com.ayy.multithreaddownload;

public interface DownloadCallBack {
    void progressUpdate(int finishedLength,int totalLength);
    void complete();
    void error();
}
