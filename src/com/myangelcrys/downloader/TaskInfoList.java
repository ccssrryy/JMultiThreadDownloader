package com.myangelcrys.downloader;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.HashSet;

/**
 * Created by cs on 17-5-21.
 */
@Root(name = "tasks")
public class TaskInfoList {
    @Attribute
    String filename = null;
    @Attribute
    String url = "";
    @ElementList
    HashSet<TaskInfo> taskInfos = new HashSet<>();

    public TaskInfoList(String filename, String url) {
        this.url = url;
        this.filename = filename;
    }

    private TaskInfoList() {
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

    public void addTaskInfo(TaskInfo taskInfo) {
        taskInfos.add(taskInfo);
    }

}
