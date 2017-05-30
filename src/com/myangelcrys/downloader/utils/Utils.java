package com.myangelcrys.downloader.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by cs on 16-10-17.
 */
public class Utils {
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
    public static String md5sum(byte[] content){
        byte[] md5=null;
        try {
            md5 = MessageDigest.getInstance("md5").digest(content);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return printHexBinary(md5);
    }
    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString().toLowerCase();
    }
}
