package com.myangelcrys.downloader.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by cs on 16-10-4.
 */
public class FileUtils {
    public static boolean newFile(File file,long size){
        if (file.exists()&&file.length()==size)return true;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile=new RandomAccessFile(file,"rw");
            randomAccessFile.setLength(size);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (randomAccessFile!=null) try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    public static String pathJoin(String dir,String name){
        return dir.endsWith("/")?dir+name:dir+"/"+name;
    }
}
