package com.android.lucy.treasure.adapter;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.base.BaseAdapter;
import com.android.lucy.treasure.base.BaseViewHolder;
import com.android.lucy.treasure.bean.BookSourceInfo;

import java.util.List;

/**
 * 小说来源列表适配器
 */

public class BookSourceCatalogAdapter extends BaseAdapter<BookSourceInfo> {


    public BookSourceCatalogAdapter(Context context, List<BookSourceInfo> datas, int laoyoutId) {
        super(context, datas, laoyoutId);
    }

    @Override
    public void convert(BaseViewHolder myViewHolder, BookSourceInfo bookSourceInfo, int position) {
        TextView tv_chapterName_new = myViewHolder.getView(R.id.tv_chapterName_new);
        TextView tv_sourceName_new = myViewHolder.getView(R.id.tv_sourceName_new);
        TextView tv_time_new = myViewHolder.getView(R.id.tv_time_new);
        tv_sourceName_new.setText(bookSourceInfo.getSourceName());
        tv_time_new.setText("update-time");
        //setAnimation(convertView,"translationX",500,0,500);
    }

    @Override
    public void myOnscroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void myOnScrollStateChanged(AbsListView absListView, int scrollState) {

    }

    @Override
    public void getDatas(List<BookSourceInfo> mDatas) {

    }

}
