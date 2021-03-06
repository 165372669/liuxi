package com.android.lucy.treasure.base;

import android.os.Handler;
import android.os.Message;

import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.interfaces.HTMLFollowRedirects;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 获取网页数据基类
 */

public abstract class BaseReadThread implements Runnable {


    protected String baseUrl;
    protected Handler myHandler;
    private boolean isCancelled;
    protected String flag;//区分线程标志
    protected int count;
    protected String bookName;
    protected String author;
    protected String url;

    public abstract void resoloveUrl(Document doc);

    public abstract void getCatalogs(Document doc);

    public abstract void errorHandle(IOException e);


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
            MyLogcat.myLog("BaseUrl:" + baseUrl);
            // Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0
            Connection conn = Jsoup.connect(baseUrl);
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0");
            conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            conn.header("Accept-Encoding", "gzip, deflate");
            conn.header("Cookie", "UM_distinctid=163f6db119c1d2-0644048e7f41168-143e7140-1fa400-163f6db119d650; " +
                    "CNZZDATA1273376891=667199016-1528854860-%7C1529665059; bcolor=; font=; size=; fontcolor=; width=");
            conn.header("Connection", "keep");
            conn.header("Upgrade-Insecure-Requests", "1");
            Document doc = conn.timeout(3 * 1000).get();
//            Connection.Response response = conn.response();
//            MyLogcat.myLog("状态码：" + response.statusCode());
            resoloveUrl(doc);
            getCatalogs(doc);
        } catch (IOException e) {
            MyLogcat.myLog(baseUrl + "，网页读取失败！！！");
            errorHandle(e);
        } finally {
            //删除集合里的任务
            ThreadPool.getInstance().deleteRunnable(this);
        }
    }

    public void saveBookData(BookInfo bookInfo) {
        int chapterTotal = bookInfo.getBookCatalogInfos().size();
        int newChapterid = chapterTotal - 1;
        bookInfo.setChapterTotal(chapterTotal);
        bookInfo.setNewChapterId(newChapterid); //最新章节id
        bookInfo.setNewChapterName(bookInfo.getBookCatalogInfos().get(newChapterid).getChapterName());
        if (bookInfo.getId() > 0) {
            bookInfo.update(bookInfo.getId());
            DataSupport.saveAll(bookInfo.getBookCatalogInfos());
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

    /**
     * 发送消息
     *
     * @param arg1 标识1
     * @param arg2 标识2
     */
    protected void sendObj(int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.arg1 = arg1;
        msg.arg2 = arg2;
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
            File file = new File(MyApplication.getContext().getExternalCacheDir(), "baidu.txt");
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
