package com.myangelcrys.downloader;

import java.nio.ByteBuffer;

/**
 * Created by cs on 17-2-10.
 */
public interface DataProccessor {
    void processData(ByteBuffer byteBuffer);
}
