package com.myangelcrys.cmd;

import com.myangelcrys.downloader.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.net.*;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        System.out.println("starting.........");
        TaskInfo taskInfo = null;
        CommandLine cmd=CommandUtils.getInstance(args);
/*        m(null);
        if (true)return;*/
        try {
            taskInfo = new TaskInfo(new URI(cmd.getOptionValue("url")));
            if (cmd.getOptionValue("proxy")!=null){
                URI uri=new URI(cmd.getOptionValue("proxy"));
                taskInfo.setProxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(uri.getHost(),uri.getPort())));
            }
            taskInfo.setMaxSpeed(Long.parseLong(cmd.getOptionValue("max-speed",Long.MAX_VALUE+"")));
            System.out.println(taskInfo.getUri());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        int thnums=Integer.parseInt(cmd.getOptionValue("ts",5+""));
        DefaultDownloadManager defaultDownloadManager=new DefaultDownloadManager(taskInfo,thnums);
        try {
            defaultDownloadManager.initServerInformation();
        } catch (IOException e) {
            System.out.println("error when init :"+e.getMessage());
            return;
        }
        File in;
        in=new File(cmd.getOptionValue("fn",defaultDownloadManager.getFilename()));
        defaultDownloadManager.setFilename(in.getName());
        FileUtils.newFile(in,defaultDownloadManager.getContentLength());
        defaultDownloadManager.splitTaskAuto(new DefaultDownloadTaskFactory(in), null);
        defaultDownloadManager.startAll();
        defaultDownloadManager.shutdownWhenFinish();
//        System.out.println(FileUtils.newFile(new File("/home/cs/rand"),1024*1024));
    }
}
