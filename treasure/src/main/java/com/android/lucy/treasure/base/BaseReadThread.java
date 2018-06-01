package com.android.lucy.treasure.base;

import android.os.Handler;
import android.os.Message;

import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.interfaces.HTMLFollowRedirects;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 获取网页数据基类
 */

public abstract class BaseReadThread implements Runnable, HTMLFollowRedirects {


    protected String baseUrl;
    private Handler myHandler;
    private boolean isCancelled;
    protected String flag;//区分线程标志

    public abstract void resoloveUrl(Document doc);


    public BaseReadThread(String baseUrl, Handler myHandler) {
        this.baseUrl = baseUrl;
        this.myHandler = myHandler;
        flag = "BaseReadThread";
    }

    @Override
    public void run() {
        readUrl();
    }

    public void readUrl() {
        if (isCancelled)
            return;
        if (baseUrl.isEmpty())
            return;
        try {
            boolean isConnect = xxbuquge();
            MyLogcat.myLog("BaseUrl:" + baseUrl);
            if (isConnect) {
                resoloveUrl(Jsoup.connect(baseUrl).get());
            }
        } catch (IOException e) {
            MyLogcat.myLog(baseUrl + "，网页读取失败！！！");
        } finally {
            //删除集合里的任务
            ThreadPool.getInstance().deleteRunnable(this);
        }
    }

    @Override
    public boolean xxbuquge() {
        return true;
    }

    /*
        * 取消线程
        * */
    public void cancel() {
        this.isCancelled = true;
    }

    /**
     * 获取取消线程标志
     *
     * @return
     */
    public boolean getIsCancelled() {
        return isCancelled;
    }

    /*
    * 返回标志
    * */
    public String getFlag() {
        return flag;
    }


    protected void sendObj(int arg) {
        Message msg = Message.obtain();
        msg.arg1 = arg;
        myHandler.sendMessage(msg);
    }

    /*
* 发送有标识的对象
* */
    protected void sendObj(Object obj, int arg) {
        Message msg = Message.obtain();
        msg.obj = obj;
        msg.arg1 = arg;
        myHandler.sendMessage(msg);
    }

    /*
* 发送有标识的对象
* */
    protected void sendObj(Object obj, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.obj = obj;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        myHandler.sendMessage(msg);
    }

    public void writeFile(Object obj) {
        BufferedWriter bw = null;
        try {
            File file = new File(MyApplication.getContext().getExternalCacheDir(), "sv.txt");
            MyLogcat.myLog("file:" + file.getPath());
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(obj.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bw)
                    bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
