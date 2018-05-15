package com.android.lucy.treasure.base;

import android.os.Handler;
import android.os.Message;

import com.android.lucy.treasure.MyApp;
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

public abstract class BaseReadThread implements Runnable {


    private String url;
    private Handler myHandler;
    private boolean isCancelled;
    protected String flag;//区分线程标志

    public abstract void resoloveUrl(Document doc);

    public BaseReadThread(String url, Handler myHandler) {
        this.url = url;
        this.myHandler = myHandler;
        flag = "BaseReadThread";
    }

    @Override
    public void run() {
        if (isCancelled)
            return;
        readUrl(url);
    }

    public void readUrl(String url) {
        MyLogcat.myLog("BaseUrl:" + url);
        if (isCancelled)
            return;
        if (url.isEmpty())
            return;
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
            resoloveUrl(doc);
            if (isCancelled)
                return;
        } catch (IOException e) {
            System.out.println(getClass().getName() + ":读取失败");
        } finally {
            //删除集合里的任务
            ThreadPool.getInstance().deleteRunnable(this);
        }
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

    /*
    * 发送对象
    * */
    protected void sendObj(Object obj) {
        sendObj(obj, 0);
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
            File file = new File(MyApp.getContext().getExternalCacheDir(), "obj.txt");
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
