package com.myangelcrys.downloader;

/**
 * Created by cs on 16-10-7.
 */
public class LockableObject {
    private static LockableObject object;
    public synchronized static LockableObject getInstance(){
        if (object==null)object=new LockableObject();
        return object;
    }
}
