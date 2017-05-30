package com.myangelcrys.downloader;

import org.simpleframework.xml.*;

import java.io.Serializable;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cs on 16-10-2.
 *
 */
@Root
@Default(DefaultType.FIELD)
public class TaskInfo implements Cloneable,Serializable {
    @Attribute
    @Default(required = false)
    private String description ="";
    Map<String,String>headers=new HashMap<>();
    private long maxSpeed =Long.MAX_VALUE;
    @Transient
    private long currentSpeed;
    @Transient
    private long preBytes;
    @Transient
    private long preTime=System.currentTimeMillis();
    private int retryTimes=10;
    private long retryDelay=1000;
    private URI uri;
    private Calendar startTime=Calendar.getInstance();
    private Calendar stopTime=Calendar.getInstance();
    private long startByte;
    private long stopByte;
    private volatile long downloadedByte;
    @Transient
    private Proxy proxy;

    public TaskInfo(URI uri, long maxSpeed, long startByte , long stopByte){
        setUri(uri);
        setMaxSpeed(maxSpeed);
        setStartByte(startByte);
        setStopByte(stopByte);
        setStartTime(Calendar.getInstance());
    }

    /**
     * for jaxb
     */
    private TaskInfo(){
        this(null,Long.MAX_VALUE,0,0);
    }
    public TaskInfo(URI uri){
        this(uri,0,0,0);
    }
    public long getMaxSpeed() {
        return maxSpeed;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void  setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public void setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
    }

    public long getRetryDelay() {
        return retryDelay;
    }

    public long getAverageSpeed() {
        return getDownloadedBytes()/(System.currentTimeMillis()-startTime.getTimeInMillis())*1000;
    }

    public long getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(long currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void setMaxSpeed(long maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        if (uri==null)return;
        try {
            this.uri=new URI(uri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            this.uri=uri;
        }
    }

    public long getDownloadedBytes() {
        return downloadedByte;
    }

    public void setDownloadedByte(long downloadedByte) {
        this.downloadedByte = downloadedByte;
    }

    public void addDownloadedBytes(long size) {
        this.downloadedByte+=size;
    }

    public long getStartByte() {
        return startByte;
    }

    public void setStartByte(long startByte) {
        this.startByte=startByte;
    }

    public long getStopByte() {
        return stopByte;
    }

    public void setStopByte(long stopByte) {
        this.stopByte=stopByte;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime=startTime;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStopTime(Calendar stopTime) {
        this.stopTime=stopTime;
    }

    public Calendar getStopTime() {
        return stopTime;
    }

    @Override
    public TaskInfo clone() {
            //TODO
//            return new TaskInfo(uri,maxSpeed,startByte,stopByte);
        try {
            return (TaskInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy=proxy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
