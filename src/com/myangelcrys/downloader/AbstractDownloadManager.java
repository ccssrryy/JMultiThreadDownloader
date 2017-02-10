package com.myangelcrys.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by cs on 16-10-4.
 *
 */
public abstract class AbstractDownloadManager implements DownloadManager{
    ScheduledThreadPoolExecutor threadPool;
    String filename = null;
    private ConcurrentHashMap<DownloadTask,Future<?>[]>taskHashMap=new ConcurrentHashMap<>();
    ConcurrentHashMap<DownloadTask,Future<?>> removedTasks=new ConcurrentHashMap<>();
    ArrayList<DownloadTask>retryList=new ArrayList<>();
    private volatile long finishedSize =0;
    private long totalSize =0;
    private boolean isStarted=false;
    private long preTime=System.currentTimeMillis();
    private List<long[]> tasks=new ArrayList<>();
    private String workingDir="./";
    public  AbstractDownloadManager(int threadNums){
        threadPool=new ScheduledThreadPoolExecutor(threadNums);
        threadPool.setMaximumPoolSize(threadNums);
    }

    @Override
    public synchronized void  addTask(DownloadTask downloadTask) {
        if (taskHashMap.get(downloadTask)!=null)return;
        taskHashMap.put(downloadTask, new Future<?>[]{null});
        tasks.add(new long[]{downloadTask.getTaskInfo().getStartByte(),
        downloadTask.getTaskInfo().getStopByte()});
        //set default processor
        downloadTask.addTaskEventListener(new DefaultTaskEventProcessor(this));
    }

    @Override
    public synchronized void commitTask(DownloadTask downloadTask) {
        addTask(downloadTask);
        taskHashMap.put(downloadTask,new Future[]{threadPool.submit(downloadTask)});
    }

    @Override
    public Future<?> getFuture(DownloadTask task) {
        return taskHashMap.get(task)[0];
    }

    @Override
    public void removeDownloadTask(DownloadTask downloadTask) {
        Future<?>[] f=taskHashMap.remove(downloadTask);
        if (f == null) return;
        long[] t = new long[]{downloadTask.getTaskInfo().getStartByte(), downloadTask.getTaskInfo().getStopByte()};
        for (long[] task : tasks) {
            if (Arrays.equals(t, task)) {
                t = task;
                break;
            }
        }
        tasks.remove(t);
        if (f[0]!=null){
            removedTasks.put(downloadTask,f[0]);
            f[0].cancel(true);
        }
        if (taskHashMap.size()==0)onTaskEmpty();
    }

    @Override
    public void pauseAll() {
        for (DownloadTask task:taskHashMap.keySet()){
            task.pause();
        }
    }

    @Override
    public void stopAll() {
        //TODO
        for (Future[] f:taskHashMap.values()){
            f[0].cancel(true);
        }
    }

    @Override
    public void startAll() {
        if (isStarted)return;
        isStarted=true;
        for (DownloadTask task:taskHashMap.keySet()){
            commitTask(task);
        }
        if (true)return;
        while (true){
            synchronized (LockableObject.getInstance()){
                try {
                    LockableObject.getInstance().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            process();
        }
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    abstract void process();

    @Override
    public void shutdownWhenFinish(){
        threadPool.shutdown();
    }

    @Override
    public void wakeAll() {
        for (DownloadTask task:taskHashMap.keySet()){
            task.go();
        }
    }

    final public void increaseSize(long l){
        synchronized (this){
            finishedSize +=l;
        }
        //FIXME not thread safe
        if (System.currentTimeMillis()-preTime>1000){
            try {
                System.out.write(("\rtotal:"+finishedSize+"b").getBytes());
                persist();
            } catch (IOException e) {
                e.printStackTrace();
            }
            preTime=System.currentTimeMillis();
        }
    }

    @Override
    public long getFinishedSize() {
        return finishedSize;
    }

    @Override
    public synchronized void postFinish(DownloadTask downloadTask,long[]finish){
        long[]t=null;
        for (long[]task:tasks) {
            if (Arrays.equals(task,finish)) {
                t=task;
            }
            else if (finish[0]==task[0]){
                task[0]=finish[1]+1;
                if (task[1]<=finish[1])t=task;
                else {
                    //TODO
//                    downloadTask.retry();
                    return;
                }
            }
        }
        tasks.remove(t);
    }

    @Override
    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public int restoreTask(InputStream inputStream, DownloadTaskFactory downloadTaskFactory, boolean reset) {
        TaskInfoList taskInfoList= (TaskInfoList) XMLUtils.getObject(TaskInfoList.class,inputStream);
        if (taskInfoList!=null){
            setFilename(taskInfoList.getFilename());
            int r=0;
            for (TaskInfo taskInfo:taskInfoList.getTaskInfos()){
                if (taskInfo.getStopByte()>0&&taskInfo.getStopByte()-taskInfo.getStartByte()<=0)continue;
                DownloadTask task=downloadTaskFactory.creatTask(taskInfo,this);
                task.getTaskInfo().setStartTime(Calendar.getInstance());
/*                taskInfo.setStartByte(taskInfo.getStartByte()+taskInfo.getDownloadedBytes());
                taskInfo.setDownloadedByte(0);*/
                try {
                    taskInfo.setUri(new URI(taskInfo.getUri().toString()));
                    if (reset){
                        taskInfo.setUri(new URI(taskInfoList.getUrl()));
                        taskInfo.getHeaders().clear();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                for (TaskEventListener listener:task.getTaskEventListeners()){
                    listener.onTaskRestore(task);
                }
                addTask(task);
            }
            return ++r;
        }
        return 0;
    }

    @Override
    public synchronized void increasePoolSize(int i) {
        threadPool.setMaximumPoolSize(threadPool.getMaximumPoolSize()+i);
        threadPool.setCorePoolSize(threadPool.getCorePoolSize()+i);
    }

    @Override
    public String getWorkingDir() {
        return workingDir;
    }

    @Override
    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    @Override
    public ConcurrentHashMap<DownloadTask, Future<?>> getDeadTasks() {
        return removedTasks;
    }

    @Override
    public ConcurrentHashMap<DownloadTask, Future<?>[]> getRunningTasks() {
        return taskHashMap;
    }

    abstract void onTaskEmpty();

    @Override
    public ArrayList<DownloadTask> getRetryList() {
        return retryList;
    }

    @Override
    public void addToRetryList(DownloadTask downloadTask) {
        retryList.add(downloadTask);
    }
}
