package com.myangelcrys.downloader;

import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by cs on 16-10-9.
 *
 */
public class XMLUtils {
    public static String getXML(Object t){
        try {
            Serializer serializer=new Persister();
            OutputStream o=new ByteArrayOutputStream();
            serializer.write(t,o);
            String s=o.toString();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Object getObject(Class c,InputStream is){
        try {
            Serializer serializer=new Persister();
            return serializer.read(c, is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[]args){
        try {
            TaskInfo taskInfo=new TaskInfo(new URI("http://www.baidu.com"),100,100,100);
            taskInfo.setProxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("localhost",8000)));
            taskInfo.setDescription(new String("陈实".getBytes(),"utf-8"));
            HashMap<String,String>m=new HashMap<>();
            m.put("name","cs");
            m.put("pwd","1993");
            taskInfo.setHeaders(m);
            TaskInfo t=taskInfo.clone();
            t.setDescription("任英");
            t.getHeaders().put("name","ry");
            TaskInfoList taskInfoList=new TaskInfoList("this is name","url");
            taskInfoList.getTaskInfos().add(taskInfo);
            taskInfoList.getTaskInfos().add(t);
            String s= XMLUtils.getXML(taskInfoList);
            System.out.println(s);
            TaskInfoList ss= (TaskInfoList) XMLUtils.getObject(TaskInfoList.class,new ByteArrayInputStream(s.getBytes()));
            System.out.println(ss.getFilename());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
@Root(name = "tasks")
class TaskInfoList{
    @Attribute
    String filename=null;
    @Attribute
    String url="";
    @ElementList
    HashSet<TaskInfo>taskInfos=new HashSet<>();
    public TaskInfoList(String filename,String url){
        this.url=url;
        this.filename=filename;
    }
    private TaskInfoList(){
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public HashSet<TaskInfo> getTaskInfos() {
        return taskInfos;
    }

    public void setTaskInfos(HashSet<TaskInfo> taskInfos) {
        this.taskInfos = taskInfos;
    }

    public void addTaskInfo(TaskInfo taskInfo){
        taskInfos.add(taskInfo);
    }

}
