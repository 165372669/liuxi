package com.android.lucy.treasure.manager;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.lucy.treasure.bean.SearchDataInfo;
import com.android.lucy.treasure.runnable.BookImageAsync;
import com.android.lucy.treasure.utils.BitmapUtils;
import com.android.lucy.treasure.utils.ImageLruCache;
import com.android.lucy.treasure.utils.ThreadPool;

import java.util.List;

/**
 * 图片下载管理
 */

public class ImageDownloadManager {

    private ImageLruCache lruCache;
    private ListView listView;
    private List<SearchDataInfo> searchDataInfos;

    public ImageDownloadManager(ListView listView, List<SearchDataInfo> searchDataInfos) {
        this.listView = listView;
        this.searchDataInfos = searchDataInfos;
        lruCache = ImageLruCache.getInstance();
    }

    /**
     * 加载从Start到end所有视频图片
     *
     */
    public void loadVideoInfos(int start, int end) {
        if (searchDataInfos.size() == 0)
            return;
        for (int i = start; i < end; i++) {
            String mImgUrl = searchDataInfos.get(i).getImgUrl();
            //MyLogcat.myLog(getClass().getName()+"，mImgUrl："+mImgUrl);
            //从缓存中取出图片
            BitmapDrawable bitmapDrawable = lruCache.getBitmapFromCache(mImgUrl);
            ImageView mImageView = listView.findViewWithTag(mImgUrl);
            if (null != bitmapDrawable) {
                Drawable drawable = BitmapUtils.bitmapToDrawable(bitmapDrawable.getBitmap());
                if (null != mImageView)
                    mImageView.setImageDrawable(drawable);
            } else {
                //开启图片下载线程
                BookImageAsync bitmapAsync = new BookImageAsync(mImageView);
                bitmapAsync.executeOnExecutor(ThreadPool.getInstance().getExecutorService(), mImgUrl);
            }
        }
    }

}
