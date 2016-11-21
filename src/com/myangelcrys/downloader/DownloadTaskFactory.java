package com.myangelcrys.downloader;

/**
 * Created by cs on 16-10-10.
 */
public interface DownloadTaskFactory {
    DownloadTask creatTask(TaskInfo taskInfo,DownloadManager downloadManager);
}
