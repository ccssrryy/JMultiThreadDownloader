package com.myangelcrys.test;

import com.myangelcrys.cmd.Main;

import java.net.MalformedURLException;

/**
 * Created by cs on 16-10-18.
 */
public class Test {
    public static void main(String[]args) throws MalformedURLException {
        if (args==null||args.length==0){
            args=new String[]
                    {"--url", "http://cdimage.kali.org/kali-2017.1/kali-linux-2017.1-amd642.iso"
                    /*"--proxy","http://127.0.0.1:8787"*/};
        }
        Main.main(args);
    }
}
