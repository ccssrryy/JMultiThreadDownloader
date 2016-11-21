package com.myangelcrys.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * Created by cs on 16-10-4.
 */
public interface DownloadManager {
    void addTask(DownloadTask downloadTask);
    void commitTask(DownloadTask downloadTask);
    Future<?> getFuture(DownloadTask task);
    ConcurrentHashMap<DownloadTask,Future<?>[]> getRunningTasks();

    String getWorkingDir();

    void setWorkingDir(String workingDir);

    ConcurrentHashMap<DownloadTask,Future<?>> getDeadTasks();
    void removeDownloadTask(DownloadTask task);
    void pauseAll();
    void stopAll();
    void startAll();
    int restoreTask(InputStream inputStream, DefaultDownloadTaskFactory downloadTaskFactory,boolean reset);
    boolean isSupportMultiThread();

    long getFinishedSize();

    void postFinish(DownloadTask downloadTasks, long[] finish);
    void shutdownWhenFinish();
    void splitTaskAuto(DownloadTaskFactory factory, TaskEventListener taskEventListener);
    void wakeAll();
    void initServerInformation() throws IOException;
    void increaseSize(long l);
    void increasePoolSize(int i);
    long getTotalSize();
    void setTotalSize(long size);

    void persist();
    void addToRetryList(DownloadTask downloadTask);
    ArrayList<DownloadTask> getRetryList();
}
