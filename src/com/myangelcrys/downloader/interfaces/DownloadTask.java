package com.myangelcrys.downloader.interfaces;

import com.myangelcrys.downloader.TaskInfo;

import java.io.InputStream;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by cs on 16-10-5.
 */
public interface DownloadTask extends Runnable{
    void pause();

    void go();

    void retry();

//    DownloadManager getDownloadManage();
    TaskInfo getTaskInfo();

    void setTaskInfo(TaskInfo taskInfo);
    HashSet<TaskEventListener> getTaskEventListeners();
    void addTaskEventListener(TaskEventListener taskEventListener);
    InputStream getInputStream();
    boolean getIsPause();
    void setPaused(boolean paused);
    UUID getUuid();
    DownloadManager getDownloadManager();
    boolean isFinishedCompletely();

}
