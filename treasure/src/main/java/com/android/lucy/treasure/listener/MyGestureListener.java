package com.android.lucy.treasure.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 *  自定义滑动事件监听器
 * SimpleOnGestureListener已经实现了OnGestureListener接口和OnDoubleTapListener接口，
 * 可以有选择性的复写需要的方法，提供方法onFling()作为滑动事件的回调函数
 */

public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

    private OnFlingListener mOnFlingListener;
    private boolean isFling = false;//滑动状态

    public OnFlingListener getmOnFlingListener() {
        return mOnFlingListener;
    }

    public void setmOnFlingListener(OnFlingListener mOnFlingListener) {
        this.mOnFlingListener = mOnFlingListener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (null == mOnFlingListener) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
        float xFrom = e1.getX();//按下坐标
        float xTo = e2.getX();
        float yFrom = e1.getY();
        float yTo = e2.getY();
        if (!isFling) {
            // 左右滑动的X轴幅度大于100，并且X轴方向的速度大于100
            if (Math.abs(xFrom - xTo) > 100.0f && Math.abs(velocityX) > 100.0f) {
                // X轴幅度大于Y轴的幅度
                if (Math.abs(xFrom - xTo) >= Math.abs(yFrom - yTo)) {
                    isFling = true;//开始滑动
                    if (xFrom > xTo) {
                        // 下一个
                        mOnFlingListener.flingToNext();
                    } else {
                        // 上一个
                        mOnFlingListener.flingToPrevious();
                    }
                    isFling = false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public interface OnFlingListener {

        void flingToNext();

        void flingToPrevious();
    }
}
