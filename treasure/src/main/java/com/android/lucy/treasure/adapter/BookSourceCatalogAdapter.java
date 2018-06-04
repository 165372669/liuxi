package com.android.lucy.treasure.adapter;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.base.BaseAdapter;
import com.android.lucy.treasure.base.BaseViewHolder;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.SourceInfo;

import java.util.List;

/**
 * 小说来源列表适配器
 */

public class BookSourceCatalogAdapter extends BaseAdapter<SourceInfo> {


    public BookSourceCatalogAdapter(Context context, List<SourceInfo> datas, int laoyoutId) {
        super(context, datas, laoyoutId);
    }

    @Override
    public void convert(BaseViewHolder myViewHolder, SourceInfo sourceInfo, int position) {
        TextView tv_chapterName_new = myViewHolder.getView(R.id.tv_chapterName_new);
        TextView tv_sourceName_new = myViewHolder.getView(R.id.tv_sourceName_new);
        TextView tv_time_new = myViewHolder.getView(R.id.tv_time_new);
        int size = sourceInfo.getCatalogInfos().size();
        if (size > 0) {
            CatalogInfo catalogInfo = sourceInfo.getCatalogInfos().get(size - 1);
            tv_chapterName_new.setText(catalogInfo.getChapterName());
            tv_sourceName_new.setText(sourceInfo.getSourceName());
            tv_time_new.setText("update-time");
        }
        //setAnimation(convertView,"translationX",500,0,500);
    }

    @Override
    public void myOnscroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void myOnScrollStateChanged(AbsListView absListView, int scrollState) {

    }

    @Override
    public void getDatas(List<SourceInfo> mDatas) {

    }

}
