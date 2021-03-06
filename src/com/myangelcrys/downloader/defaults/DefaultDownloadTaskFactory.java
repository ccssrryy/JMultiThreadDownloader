package com.myangelcrys.downloader.defaults;

import com.myangelcrys.downloader.TaskInfo;
import com.myangelcrys.downloader.interfaces.DownloadManager;
import com.myangelcrys.downloader.interfaces.DownloadTask;
import com.myangelcrys.downloader.interfaces.DownloadTaskFactory;
import com.myangelcrys.downloader.interfaces.TaskEventListener;

import java.io.File;
import java.util.HashSet;

/**
 * Created by cs on 16-10-10.
 */
public class DefaultDownloadTaskFactory implements DownloadTaskFactory {

    private File file;
    HashSet<TaskEventListener> listeners=new HashSet<>();

    public DefaultDownloadTaskFactory(File f, HashSet<TaskEventListener> listeners){
        this.listeners=listeners==null?this.listeners:listeners;
        this.file=f;
    }
    public DefaultDownloadTaskFactory(File f){
        this(f, null);
    }
    @Override
    public DownloadTask creatTask(TaskInfo taskInfo, DownloadManager dm) {
        DefaultDownloadTask d = new DefaultDownloadTask(taskInfo,dm, file);
        for (TaskEventListener taskEventListener:listeners){
            d.addTaskEventListener(taskEventListener);
        }
        return d;
    }
}
