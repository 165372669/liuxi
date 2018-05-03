package com.android.lucy.treasure.adapter;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.base.BaseAdapter;
import com.android.lucy.treasure.base.BaseViewHolder;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.ChapterIDAndName;

import java.util.List;

public class ChapterCatalogAdapter extends BaseAdapter<ChapterIDAndName> {

    public ChapterCatalogAdapter(Context context, List<ChapterIDAndName> datas, int laoyoutId) {
        super(context, datas, laoyoutId);
    }


    @Override
    public void convert(BaseViewHolder myViewHolder, ChapterIDAndName chapterIDAndName, int position) {
        TextView tv_chapter_id = myViewHolder.getView(R.id.tv_chapter_id);
        TextView tv_catalog_chapter_name = myViewHolder.getView(R.id.tv_catalog_chapter_name);
        tv_chapter_id.setText(chapterIDAndName.getChapterId() + ".");
        tv_catalog_chapter_name.setText(chapterIDAndName.getChapterName());
    }

    @Override
    public void myOnscroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void myOnScrollStateChanged(AbsListView absListView, int scrollState) {

    }

    @Override
    public void getDatas(List mDatas) {

    }
}
