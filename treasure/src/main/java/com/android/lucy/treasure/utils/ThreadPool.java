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

    /**
     * 是否有该标志的任务。
     *
     * @param flag 任务标志
     * @return
     */
    public boolean isexistTask(String flag) {
        if (runnables.size() > 0) {
            for (BaseReadThread task : runnables) {
                if (null != task) {
                    if (task.getFlag().equals(flag)) {
                        MyLogcat.myLog(task.getClass().getName() + "，任务重复：" + flag);
                        return true;
                    }
                }

            }
        }
        return false;
    }

    /*
    * 取消线程
    * */
    public void cancelTask() {
        if (runnables.size() > 0) {
            for (BaseReadThread task : runnables) {
                MyLogcat.myLog(task.getClass().getName() + "，取消任务");
                task.cancel();
            }
        }
    }


    /**
     * 取消读取章节线程
     *
     * @param flag 章节标志
     */
    public void cancelTask(String flag) {
        if (runnables.size() > 0) {
            for (BaseReadThread task : runnables) {
                if (null != task && task.getFlag().equals(flag)) {
                    MyLogcat.myLog(task.getClass().getName() + "，取消任务");
                    task.cancel();
                }

            }
        }
    }

    /**
     * 留下有章节标志的线程，其余线程取消
     *
     * @param flag 章节标志
     */
    public void removeTask(String flag) {
        if (runnables.size() > 0) {
            for (BaseReadThread task : runnables) {
                if (!(null != task && task.getFlag().equals(flag))) {
                    MyLogcat.myLog(task.getClass().getName() + "：取消任务：" + flag);
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
