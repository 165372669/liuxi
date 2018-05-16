package com.android.lucy.treasure.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.android.lucy.treasure.application.MyApp;

/**
 * Drawable工具类
 */

public class BitmapUtils {

    /**
     * Bitmap转化为Drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(MyApp.getContext().getResources(), bitmap);
    }

    //dstWidth和dstHeight分别为目标ImageView的宽高
    public static int calSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        while (height / inSampleSize > reqHeight || width / inSampleSize > reqWidth) {
            inSampleSize *= 2;
        }
        return inSampleSize / 2;
    }

    /*
    * 返回缩放后的图片
    * */
    public static Bitmap getBitmap(byte[] buf, View view) {
        if (buf.length > 0 && view != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(buf, 0, buf.length, options);
            ViewGroup.LayoutParams para = view.getLayoutParams();
            int sampleSize = BitmapUtils.calSampleSize(options, para.width, para.height);
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            ImageLruCache.getInstance().addInBitmapOptions(options);
            return BitmapFactory.decodeByteArray(buf, 0, buf.length, options);
        }
        return null;
    }


}
