package com.android.lucy.treasure.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.activity.BookIntroducedActivity;
import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.base.BaseAdapter;
import com.android.lucy.treasure.base.BaseViewHolder;
import com.android.lucy.treasure.bean.SearchInfo;
import com.android.lucy.treasure.manager.AsyncManager;
import com.android.lucy.treasure.manager.ImageDownloadManager;
import com.android.lucy.treasure.utils.BitmapUtils;
import com.android.lucy.treasure.utils.ImageLruCache;

import java.util.List;

/**
 * 搜索List列表适配器
 */

public class SearchDataAdapter extends BaseAdapter<SearchInfo> {


    private ImageDownloadManager imageManager;
    private ImageLruCache lruCache;
    private int start;
    private int end;
    private List<SearchInfo> mDatas;
    private boolean flag = true;


    public SearchDataAdapter(Context context, List<SearchInfo> mDatas, int laoyoutId, ImageDownloadManager imageManager) {
        super(context, mDatas, laoyoutId);
        this.imageManager = imageManager;
        this.mDatas = mDatas;
        lruCache = ImageLruCache.getInstance();

    }


    @Override
    public void convert(BaseViewHolder myViewHolder, SearchInfo searchInfo, int position) {
        if (isAnimation)
            if (isScrollDown)
                setAnimation(convertView);
        TextView tv_bookName = myViewHolder.getView(R.id.tv_bookName);
        TextView tv_author = myViewHolder.getView(R.id.tv_author);
        TextView tv_type = myViewHolder.getView(R.id.tv_type);
        TextView tv_desc = myViewHolder.getView(R.id.tv_desc);
        ImageView iv_book = myViewHolder.getView(R.id.iv_book);

        String imgUrl = searchInfo.getImgUrl();
        iv_book.setTag(imgUrl);
        BitmapDrawable drawable = lruCache.getBitmapFromCache(imgUrl);
        if (null != drawable)
            iv_book.setImageDrawable(BitmapUtils.bitmapToDrawable(drawable.getBitmap()));
        else
            iv_book.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher));
        tv_bookName.setText(searchInfo.getBookName());
        tv_author.setText("作者：" + searchInfo.getAuthor());
        tv_type.setText("类型：" + searchInfo.getType());
        tv_desc.setText("简介:" + searchInfo.getDesc());
    }


    @Override
    public void getDatas(List<SearchInfo> mDatas) {
    }



    /*
    滑动时的状态调用，滑动时取消加载任务,停止滑动加载任务
    * */
    @Override
    public void myOnScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
            imageManager.loadVideoInfos(start, end);
        else
            AsyncManager.getInstance().cancelTask("BookImageAsync");
    }

    /*
    * 滑动时调用
    * visibleItemCount--屏幕item显示个数。
      firstVisibleItem--屏幕最上面是第几个。
      totalItemCount--item总数。
      滚动停止刷新数据，滚动时取消任务。
    * */
    @Override
    public void myOnscroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        start = firstVisibleItem;
        end = visibleItemCount + firstVisibleItem;
        if (flag && visibleItemCount > 0) {
            imageManager.loadVideoInfos(firstVisibleItem, visibleItemCount);
            flag = false;
        }
    }


}
