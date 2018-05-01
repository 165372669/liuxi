package com.android.lucy.treasure.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 自定义缓存类
 */

public class ImageLruCache {

    private static ImageLruCache lruCache;
    private LruCache<String, BitmapDrawable> bitmapLruCache;

    //3.0后的bitmap重用机制
    private Set<SoftReference<Bitmap>> mReusableBitmaps;



    public static ImageLruCache getInstance() {
        if (lruCache == null) {
            synchronized (ThreadPool.class) {
                if (lruCache == null) {
                    lruCache = new ImageLruCache();
                }
            }
        }
        return lruCache;
    }

    private ImageLruCache() {
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int bitmapCacheSize = maxMemory / 8;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mReusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        MyLogcat.myLog(getClass().getName() + ",maxMemory:" + maxMemory + "，bitmapCacheSize：" + bitmapCacheSize);
        bitmapLruCache = new LruCache<String, BitmapDrawable>(bitmapCacheSize) {
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                int bitmapSize = getBitmapSize(value) / 1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
                //加入待重用集合
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mReusableBitmaps.add(new SoftReference<>(oldValue.getBitmap()));
                }
            }
        };
    }


    public void addInBitmapOptions(BitmapFactory.Options options) {
        //将inMutable设置true，inBitmap生效的条件之中的一个
        options.inMutable = true;
        // 尝试寻找能够内存中课复用的的Bitmap
        Bitmap inBitmap = getBitmapFromReusableSet(options);

        if (inBitmap != null) {

            options.inBitmap = inBitmap;
        }
    }

    // 获取当前能够满足复用条件的Bitmap，存在则返回该Bitmap，不存在则返回null
    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;

        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            synchronized (mReusableBitmaps) {
                final Iterator<SoftReference<Bitmap>> iterator
                        = mReusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {

                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;
                            iterator.remove();
                            break;
                        }
                    } else {

                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }


    private static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Android4.4開始，被复用的Bitmap尺寸规格大于等于须要的解码规格就可以满足复用条件
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }

        // Android4.4之前，必须满足被复用的Bitmap和请求的Bitmap尺寸规格一致才干被复用
        return candidate.getWidth() == targetOptions.outWidth
                && candidate.getHeight() == targetOptions.outHeight
                && targetOptions.inSampleSize == 1;
    }

    /**
     * 获取bitmap大小
     *
     * @param bitmapDrawable
     * @return
     */
    private int getBitmapSize(BitmapDrawable bitmapDrawable) {

        Bitmap bitmap = bitmapDrawable.getBitmap();
        //4.4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        //3.1及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        //3.1之前的版本
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    private static int getBytesPerPixel(Config config) {
        if (config == Config.ARGB_8888) {
            return 4;
        } else if (config == Config.RGB_565) {
            return 2;
        } else if (config == Config.ARGB_4444) {
            return 2;
        } else if (config == Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }

    /**
     * 获取缓存中的图片
     *
     * @param mImgUrl
     * @return
     */
    public  BitmapDrawable getBitmapFromCache(String mImgUrl) {
        if (null != mImgUrl) {
            return bitmapLruCache.get(mImgUrl);
        }
        return null;
    }


    /**
     * 将图片加入缓存
     *
     * @param mImgUrl
     * @param
     */
    public  void addBitmapToCache(String mImgUrl, BitmapDrawable bitmap) {
        if (null == bitmapLruCache.get(mImgUrl)) {
            if (null != mImgUrl && null != bitmap) {
                bitmapLruCache.put(mImgUrl, bitmap);
            }
        }
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        if (bitmapLruCache != null) {
            bitmapLruCache.evictAll();
            mReusableBitmaps.clear();
        }
    }

}
