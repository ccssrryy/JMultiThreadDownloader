package com.myangelcrys.downloader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by cs on 16-10-4.
 */
public class SafeFile {
    RandomAccessFile rf=null;
    public SafeFile(RandomAccessFile randomAccessFile){
        rf=randomAccessFile;
    }
    public synchronized int write(byte[]bytes,long posision){
        try {
            if (posision>=rf.length()){
                //TODO
            }
            return rf.getChannel().write(ByteBuffer.wrap(bytes),posision);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
