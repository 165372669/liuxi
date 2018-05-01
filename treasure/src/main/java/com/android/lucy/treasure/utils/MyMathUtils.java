package com.android.lucy.treasure.utils;

import android.content.Context;

/**
 * 数学工具类
 */

public class MyMathUtils {

    /*
    * 两数相除小数+1返回。
    * */
    public static int myCeil(double d) {
        int number = (int) d;
        double temp = d - number;
        if (temp > 0)
            number = number + 1;
        return number;
    }

    /*
    * 根据手机的分辨率从 dp转px
    * */
    public static int dip2px(Context context, float dip) {

        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
