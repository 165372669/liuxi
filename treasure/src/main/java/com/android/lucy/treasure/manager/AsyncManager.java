package com.android.lucy.treasure.manager;

import android.os.AsyncTask;

import com.android.lucy.treasure.utils.MyLogcat;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask线程任务管理器
 */

public class AsyncManager {
    private List<AsyncTask> asyncTasks;
    private static AsyncManager asyncManager;

    public static AsyncManager getInstance() {
        if (asyncManager == null) {
            synchronized (AsyncManager.class) {
                if (asyncManager == null) {
                    asyncManager = new AsyncManager();
                }
            }
        }
        return asyncManager;
    }

    private AsyncManager() {
        asyncTasks = new ArrayList<>();
    }

    public void addTask(AsyncTask asyncTask)
    {
        asyncTasks.add(asyncTask);
    }
    public void deleteTask(AsyncTask asyncTask)
    {
        asyncTasks.remove(asyncTask);
    }
    public void cancelTask(String className) {

        if(asyncTasks.size()>0)
        {
            for (int i=0; i<asyncTasks.size();i++) {
                AsyncTask asyncTask = asyncTasks.get(i);
                String taskName=asyncTask.getClass().getName();
                if (taskName.contains(className))
                {
                    if (null != asyncTask && asyncTask.getStatus() == AsyncTask.Status.RUNNING)
                    {
                        MyLogcat.myLog("取消"+className+"任务");
                        asyncTask.cancel(true);
                        asyncTasks.remove(asyncTask);
                        i--;
                    }
                }
            }
        }
    }



    public void cancelTask() {
        if(asyncTasks.size()>0)
        {
            for (AsyncTask asyncTask : asyncTasks) {
                if (null != asyncTask && asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                    asyncTask.cancel(true);
                    asyncTasks.remove(asyncTask);
                }
            }
        }
    }

    public int taskSize()
    {
        return asyncTasks.size();
    }
    public String toString()
    {
        StringBuilder sb=new StringBuilder();
        for (AsyncTask asyncTask:asyncTasks)
        {
            sb.append(asyncTask.getClass().getName()+"\n");
        }
        return sb.toString();
    }

}
