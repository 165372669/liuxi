package com.android.lucy.treasure.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 自定义Handler，解决内存泄漏
 */

public abstract class MyHandler<T extends Activity> extends Handler {

    //目录下载标志

    public final static int CATALOG_SOURCE_NO = 0;

    public final static int CATALOG_SOURCE_OK = 1;

    public final static int CATALOG_SOURCE_ERROR = 2;

    //百度搜索标志

    public final static int BAIDU_SEARCH_OK = 3;

    public final static int BAIDU_SEARCH_NO = 4;

    public final static int BAIDU_SEARCH_ERROR = 11;

    //章节下载标记

    public final static int CHAPTER_LOADING_OK = 5;

    public final static int CHAPTER_LOADING_NO = 6;

    public final static int CHAPTER_LOADING_ERROR = 7;

    //历史搜索标志

    public final static int SEARCH_HISTORYS_OK = 8;

    public final static int SEARCH_HISTORYS_NOTFOUND = 9;

    public final static int SEARCH_HISTORYS_ERROR = 10;

    //小说详情页面搜索

    public final static int SEARCH_DETAILS_OK = 12;

    public final static int SEARCH_DETAILS_NO = 13;

    public final static int SEARCH_DETAILS_ERROR = 14;


    public abstract void myHandleMessage(Message msg);

    protected WeakReference<T> mActivity;

    public MyHandler(T t) {
        mActivity = new WeakReference<>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        myHandleMessage(msg);
    }


}
