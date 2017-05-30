package com.myangelcrys.downloader.defaults;

import com.myangelcrys.downloader.interfaces.DownloadManager;
import com.myangelcrys.downloader.interfaces.DownloadTask;
import com.myangelcrys.downloader.interfaces.TaskEventListener;

import java.net.URLConnection;
import java.nio.ByteBuffer;

/**
 * Created by cs on 16-10-7.
 *
 */
public class DefaultTaskEventProcessor implements TaskEventListener {
    DownloadManager downloadManager;
    public DefaultTaskEventProcessor(DownloadManager dlm){
        downloadManager=dlm;
    }
    @Override
    public void onStart(DownloadTask task) {
    }

    @Override
    public void onPause(DownloadTask task) {

    }

    @Override
    public void onFinish(DownloadTask task) {
        downloadManager.postFinish(task,new long[]{task.getTaskInfo().getStartByte(),task.getTaskInfo().getStartByte()+ task.getTaskInfo().getDownloadedBytes()-1});
        System.out.println((task.getTaskInfo().getDownloadedBytes()*1000/(task.getTaskInfo().getStopTime().getTimeInMillis()
        -task.getTaskInfo().getStartTime().getTimeInMillis())));
        System.out.println("in range:"+task.getTaskInfo().getStartByte()+"-"
        +task.getTaskInfo().getStopByte()+",finish "+task.getTaskInfo().getDownloadedBytes()+
        " need more "+(task.getTaskInfo().getStopByte()-task.getTaskInfo().getStartByte()+1-
        task.getTaskInfo().getDownloadedBytes()));

    }

    @Override
    public void onDead(DownloadTask task) {
        if (!task.isFinishedCompletely()){
            downloadManager.addToRetryList(task);
            downloadManager.persist();
        }
        downloadManager.removeDownloadTask(task);
    }

    @Override
    public void onReadError(DownloadTask task, Exception e) {
    }

    @Override
    public void onReceive(DownloadTask task, ByteBuffer bytes) {
        downloadManager.increaseSize(bytes.limit());
/*        try {
            synchronized (System.out) {
                System.out.write('\r');
                if (downloadManager.getTotalSize()!=0)System.out.write((downloadManager.getFinishedSize() * 100 / downloadManager.getTotalSize() + "% "+task.getTaskInfo().getCurrentSpeed()/1024+"kb/s").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onSpeedChanged(DownloadTask task, long speed) {
    }

    @Override
    public void onConnected(URLConnection uriConnection) {
        System.out.println("connected");
    }

    @Override
    public void onTaskRestore(DownloadTask task) {
    }

    @Override
    public void onConnectError(DownloadTask task, Exception e) {

    }

    @Override
    public void onErrorCode(DownloadTask task, int errorCode) {
        //TODO decrease the pool size
        downloadManager.increasePoolSize(-1);
    }
}
