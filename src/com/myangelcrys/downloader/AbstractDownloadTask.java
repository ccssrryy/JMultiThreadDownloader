package com.myangelcrys.downloader;


import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Calendar;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by cs on 16-10-2.
 *
 */
public abstract class AbstractDownloadTask implements DownloadTask {
    private UUID uuid=UUID.randomUUID();

    volatile boolean isPaused=false;
    private HashSet<TaskEventListener> taskEventListeners=new HashSet<>();
    private TaskInfo taskInfo;
    private InputStream inputStream;
    private DownloadManager downloadManager;
    private long preBytes;
    private long preTime=System.currentTimeMillis();
    public AbstractDownloadTask(TaskInfo taskInfo,DownloadManager dm){
        this.taskInfo=taskInfo;
        downloadManager=dm;
    }
    @Override
    public void run() {
        try {
            inputStream = initInputStream();
            for (TaskEventListener taskEventListener:taskEventListeners) {
                taskEventListener.onStart(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
            for (TaskEventListener taskEventListener:taskEventListeners) {
                taskEventListener.onConnectError(this, e);
            }
            for (TaskEventListener taskEventListener:taskEventListeners) {
                taskEventListener.onDead(this);
            }
            return;
        }
        try {
            ByteBuffer bytes = ByteBuffer.allocate((int) Math.min(taskInfo.getMaxSpeed(), 2048));
            int size;
            ReadableByteChannel channel = Channels.newChannel(inputStream);
            long prevTime=System.currentTimeMillis();
            long prevbytes=taskInfo.getDownloadedBytes();
            while ((size=channel.read((ByteBuffer) bytes.clear()))!=-1){
                bytes.flip();
                processData(bytes);
                taskInfo.addDownloadedBytes(size);
                //limit the data traffic
                //// FIXME: 16-10-19
                long cur=System.currentTimeMillis();
                double desire=((double) (taskInfo.getDownloadedBytes()-prevbytes)*1000)/taskInfo.getMaxSpeed();
                if (cur-prevTime<desire){
                    Thread.sleep((Math.round(desire-cur+prevTime)));
                    prevTime=System.currentTimeMillis();
                    prevbytes=taskInfo.getDownloadedBytes();
                }
                if (cur-preTime>=1000){
                    taskInfo.setCurrentSpeed(1000*(taskInfo.getDownloadedBytes()-preBytes)/(cur-preTime));
                    preTime=cur;
                    preBytes=taskInfo.getDownloadedBytes();
                    for (TaskEventListener taskEventListener:taskEventListeners){
                        taskEventListener.onSpeedChanged(this,taskInfo.getCurrentSpeed());
                    }
                }
                for (TaskEventListener taskEventListener:taskEventListeners){
                    taskEventListener.onReceive(this,bytes);
                }
                if (getIsPause()) {
                    pausedBySelf();
                    prevTime=System.currentTimeMillis();
                }
            }
        } catch (IOException e) {
            for (TaskEventListener taskEventListener:taskEventListeners) {
                taskEventListener.onReadError(this, e);
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (inputStream!=null)inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            taskInfo.setStopTime(Calendar.getInstance());
            for (TaskEventListener taskEventListener:taskEventListeners) {
                taskEventListener.onFinish(this);
                taskEventListener.onDead(this);
            }
        }
    }
    @Override
    public void addTaskEventListener(TaskEventListener taskEventListener){
        if (taskEventListener!=null)taskEventListeners.add(taskEventListener);
    }

    public HashSet<TaskEventListener> getTaskEventListeners() {
        return taskEventListeners;
    }

    @Override
    public InputStream getInputStream(){
        return inputStream;
    }

    @Override
    public boolean getIsPause() {
        return isPaused;
    }
    @Override
    public void setPaused(boolean paused){
        isPaused=paused;
        if (!(isPaused=paused))synchronized (this){
            this.notifyAll();
        }
    }

    private void pausedBySelf(){
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }



    @Override
    public void retry() {
        int retry=getTaskInfo().getRetryTimes();
        if (retry>0){
            getTaskInfo().setRetryTimes(retry-1);
            try {
                Thread.sleep(getTaskInfo().getRetryDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            run();
        }
    }
    @Override
    public void pause(){
        //only called by another thread
        setPaused(true);
    }
    @Override
    public void go(){
        setPaused(false);
    }

    @Override
    public TaskInfo getTaskInfo(){
        return taskInfo;
    }
    @Override
    public UUID getUuid(){
        return uuid;
    }
    abstract InputStream initInputStream() throws IOException;
    abstract void processData(ByteBuffer byteBuffer);

    @Override
    public boolean isFinishedCompletely() {
        return  (taskInfo.getStopByte()-taskInfo.getStartByte()+1<=taskInfo.getDownloadedBytes());
    }

    @Override
    public DownloadManager getDownloadManager() {
        return downloadManager;
    }
}
