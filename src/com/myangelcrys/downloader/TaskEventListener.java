package com.myangelcrys.downloader;

import java.net.URLConnection;
import java.nio.ByteBuffer;

/**
 * Created by cs on 16-10-2.
 */
public interface TaskEventListener {
    /**
     * called when inputStream initialed properly
     * @param task
     */
    void onStart(DownloadTask task);
    void onPause(DownloadTask task);
    /**
     * called when after the task removed from the map and before the task dead
     * @param task
     */
    /**
     * called after a task had been started(connection established) and read the EOF
     * (may be disconnected by peer,or finished completely)
     * @param task
     */
    void onFinish(DownloadTask task);

    /**
     * when every task is going to die with nothing else to do
     * @param task
     */
    void onDead(DownloadTask task);

    /**
     * when raise a error when reading data from connection
     * @param task
     * @param e
     */
    void onReadError(DownloadTask task, Exception e);

    /**
     * as u see
     * @param task
     * @param e
     */
    void onConnectError(DownloadTask task, Exception e);

    /**
     * read bytes
     * @param task
     * @param bytes bytes read
     */
    void onReceive(DownloadTask task, ByteBuffer bytes);

    /**
     * got a lately speed
     * @param task
     * @param speed
     */
    void onSpeedChanged(DownloadTask task, long speed);

    /**
     * after a connection established
     * @param urlConnection
     */
    void onConnected(URLConnection urlConnection);

    /**
     * when server responses a code present a error
     * @param task
     * @param errorCode
     */
    void onErrorCode(DownloadTask task, int errorCode);

    /**
     * when a task is going to restore(going to be committed to run) from a file etc
     * @param task
     */
    void onTaskRestore(DownloadTask task);
}
