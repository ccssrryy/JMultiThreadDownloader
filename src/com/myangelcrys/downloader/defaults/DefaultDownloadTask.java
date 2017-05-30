package com.myangelcrys.downloader.defaults;

import com.myangelcrys.downloader.FileDataProccessor;
import com.myangelcrys.downloader.TaskInfo;
import com.myangelcrys.downloader.abstracts.AbstractDownloadTask;
import com.myangelcrys.downloader.interfaces.DownloadManager;
import com.myangelcrys.downloader.interfaces.TaskEventListener;
import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by cs on 16-10-2.
 */
public class DefaultDownloadTask extends AbstractDownloadTask {
    int redirectTimes=5;

    public DefaultDownloadTask(TaskInfo taskInfo, DownloadManager dm, File f) {
        super(taskInfo, dm, new FileDataProccessor(f, taskInfo.getStartByte() + taskInfo.getDownloadedBytes()));
    }

    @Override
    @NotNull
    protected InputStream initInputStream() throws IOException {
        URI uri=getTaskInfo().getUri();
        HttpURLConnection s = null;
        if (getTaskInfo().getProxy()==null) s=(HttpURLConnection) uri.toURL().openConnection();
        else {
            s=(HttpURLConnection) uri.toURL().openConnection(getTaskInfo().getProxy());
        }
        if (getDownloadManager().isSupportMultiThread()){
            s.setRequestProperty("Range", "bytes="
                    +(getTaskInfo().getStartByte()+getTaskInfo().getDownloadedBytes()) + "-" + (0>=getTaskInfo().getStopByte()?"":getTaskInfo().getStopByte())
            );
        }
        if (getTaskInfo().getHeaders()!=null){
            for (Map.Entry<String,String> entry:getTaskInfo().getHeaders().entrySet()){
                s.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        s.connect();
        System.out.println(getTaskInfo().getHeaders());
        System.out.println(s.getHeaderFields());
        int code=s.getResponseCode();
        if(code/100==3){//3xx
            if (redirectTimes--<=0)return null;
            String cookie = s.getHeaderField("Set-Cookie");
            Map<String, String> headers = getTaskInfo().getHeaders();
            if (cookie!=null)headers.put("Cookie",cookie);
            headers.put("Refer",getTaskInfo().getUri().toString());//FIXME refer ok?
            getTaskInfo().setHeaders(headers);
            try {
                getTaskInfo().setUri(new URI(s.getHeaderField("Location")));
                System.out.println("redirecting to:"+getTaskInfo().getUri());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return initInputStream();
        }
        else if (code>=400){
            for (TaskEventListener taskEventListener:getTaskEventListeners()) {
                taskEventListener.onErrorCode(this,code);
            }
            return null;
        }
        //TODO
        for (TaskEventListener taskEventListener:getTaskEventListeners()){
            taskEventListener.onConnected(s);
        }
        return s.getInputStream();
    }
}
