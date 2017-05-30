package com.myangelcrys.downloader.interfaces;

import com.myangelcrys.downloader.TaskInfo;

/**
 * Created by cs on 16-10-10.
 */
public interface DownloadTaskFactory {
    DownloadTask creatTask(TaskInfo taskInfo, DownloadManager downloadManager);
}
