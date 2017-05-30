package com.myangelcrys.downloader.defaults;

import com.myangelcrys.downloader.AbstractDownloadManager;
import com.myangelcrys.downloader.TaskInfo;
import com.myangelcrys.downloader.TaskInfoList;
import com.myangelcrys.downloader.interfaces.DownloadTask;
import com.myangelcrys.downloader.interfaces.DownloadTaskFactory;
import com.myangelcrys.downloader.interfaces.TaskEventListener;
import com.myangelcrys.downloader.utils.FileUtils;
import com.myangelcrys.downloader.utils.Utils;
import com.myangelcrys.downloader.utils.XMLUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cs on 16-10-4.
 *
 */
public class DefaultDownloadManager extends AbstractDownloadManager {
    private int threadNums;
    boolean isServerInit;
    private Map<String, List<String>> responseHeaders =new HashMap<>();
    TaskInfo taskInfo=null;
    private int redirectTimes=5;

    public DefaultDownloadManager(TaskInfo taskInfo,int threadNums) {
        super(threadNums);
        this.threadNums=threadNums;
        this.taskInfo=taskInfo;
    }
    @Override
    public void splitTaskAuto(DownloadTaskFactory factory, TaskEventListener taskEventListener){
        for (long[]task:divideTask()){
            TaskInfo ti=taskInfo.clone();
            ti.setStartByte(task[0]);
            ti.setStopByte(task[1]);
            DownloadTask t=factory.creatTask(ti,this);
            t.addTaskEventListener(taskEventListener);
            addTask(t);
            System.out.println(task[0]+"-"+task[1]);
        }
    }
    private List<long[]> divideTask(){
        long size=getContentLength();
        setTotalSize(size);
        List<long[]> tasks=new ArrayList<>();
//        if (size<=0)return;
        if (!isSupportMultiThread())threadNums=1;
        for (int i=0;i<threadNums;i++){
            tasks.add(new long[]{size/threadNums*i,i==threadNums-1?size-1:size/threadNums*(i+1)-1});
        }
        return tasks;
    }


    public long getContentLength(){
        List<String> r = getResponseHeaders().get("Content-Length");
        long size=0;
        if (r!=null)size=Long.parseLong(r.get(0));
        return size;
    }
    @Override
    public void initServerInformation() throws IOException {
        if (isServerInit) return;
        URL url = taskInfo.getUri().toURL();
        HttpURLConnection connection;
        if (taskInfo.getProxy() == null) connection = (HttpURLConnection) url.openConnection();
        else connection = (HttpURLConnection) url.openConnection(taskInfo.getProxy());
        Map<String, String> hs = taskInfo.getHeaders();
        if (hs != null) {
            for (Map.Entry<String, String> entry : hs.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        connection.connect();
        int code = connection.getResponseCode();
        if (code / 100 == 3) {//3xx
            if (redirectTimes-- <= 0) return;
            String cookie = connection.getHeaderField("Set-Cookie");
            Map<String, String> headers = getTaskInfo().getHeaders();
            if (cookie!=null)headers.put("Cookie", cookie);
            headers.put("Refer", getTaskInfo().getUri().toString());//FIXME refer ok?
            getTaskInfo().setHeaders(headers);
            try {
                getTaskInfo().setUri(new URI(connection.getHeaderField("Location")));
                System.out.println("redirecting to:" + getTaskInfo().getUri());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            initServerInformation();
            return;
        }
        else if (code>=400){
            throw new IOException("http code error "+code);
        }
        responseHeaders.putAll(connection.getHeaderFields());
        System.out.println(responseHeaders);
        isServerInit = true;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    @Override
    public String getFilename(){
        if (filename!=null)return filename;
        List<String> r=responseHeaders.get("Content-Disposition");
        String f=null;
        if (r!=null&&!r.isEmpty()){
            f=r.get(0).split("filename=")[1].replace('"',' ').trim();
        }
        if (f==null){
            String t=taskInfo.getUri().getPath();
            if (t.length()>1){
                int end=Math.min(t.indexOf('#'),t.indexOf('?'));
                if (end==-1)end=t.length();
                f=t.substring(t.lastIndexOf('/')+1,end);
            }
        }
        return f;
    }

    @Override
    public boolean isSupportMultiThread() {
        return responseHeaders.get("Accept-Ranges")!=null;
    }


    @Override
    protected void process() {
    }

    public TaskInfo getTaskInfo(){
        return taskInfo;
    }

    @Override
    protected void onTaskEmpty() {
        if (getRetryList().size()==0)getProgressFile().delete();
    }

    //FIXME not thread safe
    @Override
    public void persist(){
        TaskInfoList taskInfoList=new TaskInfoList(getFilename(),taskInfo.getUri().toString());
        for (DownloadTask downloadTask:getDeadTasks().keySet()){
            taskInfoList.addTaskInfo(downloadTask.getTaskInfo());
        }
        for (DownloadTask downloadTask:getRunningTasks().keySet()){
            taskInfoList.addTaskInfo(downloadTask.getTaskInfo());
        }
        String s= XMLUtils.getXML(taskInfoList);
        if (s==null)return;
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(getProgressFile());
            fo.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fo!=null) try {
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public File getProgressFile(){
        return new File(FileUtils.pathJoin(getWorkingDir(), Utils.md5sum(getTaskInfo().getUri().toString().getBytes())));
    }
}
