package com.android.lucy.treasure.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 禁止滑动的ViewPager
 */

public class NoSlippageViewPager extends ViewPager {

    public NoSlippageViewPager(Context context) {
        super(context);
    }

    public NoSlippageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
