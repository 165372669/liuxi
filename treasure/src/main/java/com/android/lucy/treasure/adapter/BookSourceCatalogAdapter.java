package com.android.lucy.treasure.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.base.BaseAdapter;
import com.android.lucy.treasure.base.BaseViewHolder;
import com.android.lucy.treasure.bean.BaiduSearchDataInfo;
import com.android.lucy.treasure.bean.CatalogInfo;

import java.util.List;
import java.util.Set;

/**
 * 小说来源列表适配器
 */

public class BookSourceCatalogAdapter extends BaseAdapter<BaiduSearchDataInfo> {


    public BookSourceCatalogAdapter(Context context, List<BaiduSearchDataInfo> datas, int laoyoutId) {
        super(context, datas, laoyoutId);
    }

    @Override
    public void convert(BaseViewHolder myViewHolder, BaiduSearchDataInfo info, int position) {
        TextView tv_chapterName_new = myViewHolder.getView(R.id.tv_chapterName_new);
        TextView tv_sourceName_new = myViewHolder.getView(R.id.tv_sourceName_new);
        TextView tv_time_new = myViewHolder.getView(R.id.tv_time_new);
        int maxSize = info.getCatalogInfos().size();
        CatalogInfo catalogInfo = info.getCatalogInfos().get(maxSize - 1);
        tv_chapterName_new.setText(catalogInfo.getChapterName());
        tv_sourceName_new.setText(info.getSourceName());
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
    public void getDatas(List<BaiduSearchDataInfo> mDatas) {

    }

}
