package com.android.lucy.treasure.adapter;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.lucy.treasure.MyApp;
import com.android.lucy.treasure.R;
import com.android.lucy.treasure.base.BaseAdapter;
import com.android.lucy.treasure.base.BaseViewHolder;
import com.android.lucy.treasure.utils.MyMathUtils;

import java.util.List;

/**
 * 历史搜索数据适配器
 */

public class SearchHistoryAdapter extends BaseAdapter<String> {

    public SearchHistoryAdapter(Context context, List<String> datas, int laoyoutId) {
        super(context, datas, laoyoutId);
    }

    @Override
    public void convert(BaseViewHolder myViewHolder, String s, int position) {
        TextView tv_history_item = myViewHolder.getView(R.id.tv_history_item);
        tv_history_item.setCompoundDrawablePadding(MyMathUtils.dip2px(MyApp.getContext(), 10));//设置图片之间的间距
        tv_history_item.setText(s);
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
