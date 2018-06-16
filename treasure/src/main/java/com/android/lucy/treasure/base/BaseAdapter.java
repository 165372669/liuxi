package com.android.lucy.treasure.base;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.List;

/**
 * 万能数据适配器
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter implements AbsListView.OnScrollListener {
    protected Context context;
    private List<T> mDatas;
    private LayoutInflater mInflater;
    private int laoyoutId;
    private int mFirstVisibleItem;
    protected boolean isScrollDown;//是否为下滑状态
    private int mFirstTop;
    protected View convertView;

    public BaseAdapter(Context context, List<T> datas, int laoyoutId) {
        this.context = context;
        this.mDatas = datas;
        this.laoyoutId = laoyoutId;

        if (context != null) {
            mInflater = LayoutInflater.from(context);
        }


    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.convertView = convertView;
        getDatas(mDatas);
        BaseViewHolder myViewHolder = BaseViewHolder.get(context, convertView, parent, laoyoutId, position);
        convert(myViewHolder, getItem(position), position);
        return myViewHolder.getConvertView();
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //是否为下滑
        View firstChild = absListView.getChildAt(0);
        if (firstChild == null)
            return;
        int top = firstChild.getTop();
        //firstVisibleItem > mFirstPosition表示向下滑动一整个Item
        //mFirstTop > top表示在当前这个item中滑动
        isScrollDown = firstVisibleItem > mFirstVisibleItem || mFirstTop > top;
        mFirstTop = top;
        mFirstVisibleItem = firstVisibleItem;
        myOnscroll(absListView, firstVisibleItem, visibleItemCount, totalItemCount);
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        myOnScrollStateChanged(absListView, scrollState);
    }

    public abstract void convert(BaseViewHolder myViewHolder, T t, int position);

    public abstract void myOnscroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount);

    public abstract void myOnScrollStateChanged(AbsListView absListView, int scrollState);

    public abstract void getDatas(List<T> mDatas);


    public T getDatas(int index) {
        return mDatas.get(index);
    }

}
