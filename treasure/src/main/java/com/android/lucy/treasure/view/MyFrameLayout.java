package com.android.lucy.treasure.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * 自定义FrameLayout
 * 当ViewPager为滑动状态时，拦截事件分发，让ViewPager不可点击。
 */

public class MyFrameLayout extends FrameLayout {

    private int state = 0;

    public MyFrameLayout(@NonNull Context context) {
        super(context);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return state != 0;
    }

    public void setState(int state) {

        this.state = state;
    }
}

