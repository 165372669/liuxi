package com.android.lucy.treasure.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 自定义Handler，解决内存泄漏
 */

public abstract class MyHandler<T extends Activity> extends Handler{

    public abstract void myHandleMessage(Message msg);

    protected WeakReference<T> mActivity;

    public MyHandler(T t)
    {
        mActivity=new WeakReference<>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        myHandleMessage(msg);
    }


}
