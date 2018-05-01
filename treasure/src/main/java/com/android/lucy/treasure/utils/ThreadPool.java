package com.android.lucy.treasure.utils;

import com.android.lucy.treasure.base.BaseReadThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池工具类
 */

public class ThreadPool {
    private volatile static ThreadPool threadPool;
    public static final int POOL_SIZE = 10;
    private ExecutorService executorService;
    private List<BaseReadThread> runnables;

    public ExecutorService getExecutorService() {

        return executorService;
    }

    public static ThreadPool getInstance() {
        if (threadPool == null) {
            synchronized (ThreadPool.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPool();
                }
            }
        }
        return threadPool;
    }


    private ThreadPool() {
        executorService = Executors.newFixedThreadPool(POOL_SIZE);
        runnables = new ArrayList<>();
    }

    public void submitTask(Runnable runnable) {
        runnables.add((BaseReadThread) runnable);
        executorService.submit(runnable);
    }

    public void deleteRunnable(Runnable runnable) {
        runnables.remove(runnable);
    }

    /*
    * 取消线程
    * */
    public void cancelTask() {
        if (runnables.size() > 0) {
            for (BaseReadThread task : runnables) {
                MyLogcat.myLog(task.getClass().getName() + "取消任务");
                task.cancel();
            }
        }
    }

    /*
* 取消读取章节线程
* */
    public void cancelTask(String flag) {
        if (runnables.size() > 0) {
            for (BaseReadThread task : runnables) {
                if (task.getFlag().equals(flag)) {
                    MyLogcat.myLog(task.getClass().getName() + "取消任务");
                    task.cancel();
                }

            }
        }
    }

    public void clear() {
        executorService.shutdownNow();
        executorService = null;
        threadPool = null;
    }


}
