package com.android.lucy.treasure.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 自定义Handler，解决内存泄漏
 */

public abstract class MyHandler<T extends Activity> extends Handler {

    public final static int CATALOG_SOURCE_NO = 0;

    public final static int CATALOG_SOURCE_OK = 1;

    public final static int CATALOG_SOURCE_ERROR = 2;

    public final static int BAIDU_SEARCH_OK = 3;

    public final static int BAIDU_SEARCH_NO = 4;

    public final static int CHAPTER_LOADING_OK = 5;

    public final static int CHAPTER_LOADING_NO = 6;

    public final static int CHAPTER_LOADING_ERROR=7;

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
