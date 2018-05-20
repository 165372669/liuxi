package com.android.lucy.treasure.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.base.BaseAdapter;
import com.android.lucy.treasure.base.BaseViewHolder;
import com.android.lucy.treasure.bean.ChapterIDAndName;

import java.util.List;

public class ChapterCatalogAdapter extends BaseAdapter<ChapterIDAndName> {

    private int readChapterid;

    public ChapterCatalogAdapter(Context context, List<ChapterIDAndName> datas, int laoyoutId) {
        super(context, datas, laoyoutId);
    }


    @Override
    public void convert(BaseViewHolder myViewHolder, ChapterIDAndName chapterIDAndName, int position) {
        TextView tv_chapter_id = myViewHolder.getView(R.id.tv_chapter_id);
        TextView tv_catalog_chapter_name = myViewHolder.getView(R.id.tv_catalog_chapter_name);
        tv_chapter_id.setText(chapterIDAndName.getChapterId() + ".");
        tv_catalog_chapter_name.setText(chapterIDAndName.getChapterName());
        //设置当前章节在章节目录中颜色高亮
        if (position == readChapterid) {
            tv_chapter_id.setTextColor(context.getResources().getColor(R.color.chenghongse));
            tv_catalog_chapter_name.setTextColor(context.getResources().getColor(R.color.chenghongse));
        } else {
            tv_chapter_id.setTextColor(context.getResources().getColor(R.color.black));
            tv_catalog_chapter_name.setTextColor(context.getResources().getColor(R.color.black));
        }
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

    /**
     * 获取正在阅读的章节id
     * * @param readChapterid
     */
    public void setReadChapterid(int readChapterid) {
        this.readChapterid = readChapterid;
    }
}
