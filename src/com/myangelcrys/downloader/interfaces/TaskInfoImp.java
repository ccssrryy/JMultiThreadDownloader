package com.myangelcrys.downloader.interfaces;

import java.io.Serializable;
import java.net.Proxy;
import java.net.URI;
import java.util.Calendar;

/**
 * Created by cs on 16-10-2.
 */
public interface TaskInfoImp extends Cloneable,Serializable {
    long getMaxSpeed();

    int getRetryTimes();

    void setRetryTimes(int retryTimes);

    void setRetryDelay(long retryDelay);

    long getRetryDelay();

    long getAverageSpeed();
    long getCurrentSpeed();

    void setCurrentSpeed(long currentSpeed);

    void setMaxSpeed(long speed);
    URI getUri();
    long getDownloadedBytes();
    long getStartByte();
    long getStopByte();
    Calendar getStartTime();
    Calendar getStopTime();
//    TaskInfoImp clone();
    Proxy getProxy();
    void setProxy(Proxy proxy);
}
