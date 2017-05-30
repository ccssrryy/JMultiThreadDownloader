package com.myangelcrys.downloader;

import com.myangelcrys.downloader.interfaces.DataProccessor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by cs on 17-2-10.
 */
public class FileDataProccessor implements DataProccessor {
    private FileChannel channel = null;
    RandomAccessFile randomAccessFile;

    public FileDataProccessor(File file, long start) {
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(start);
            channel = randomAccessFile.getChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processData(ByteBuffer byteBuffer) {
        try {
            channel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
