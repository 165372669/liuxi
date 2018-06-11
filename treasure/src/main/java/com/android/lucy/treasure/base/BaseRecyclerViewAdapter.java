package com.android.lucy.treasure.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.lucy.treasure.holder.SuperViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 书架RecylerView万能适配器
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<SuperViewHolder> {

    protected Context mContext;
    protected List<T> mDataList = new ArrayList<>();
    private LayoutInflater mInflater;

    public abstract int getLayoutId();

    public abstract void onBindItemHolder(SuperViewHolder holder, int position);

    public BaseRecyclerViewAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public SuperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(getLayoutId(), parent, false);
        return new SuperViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SuperViewHolder holder, int position) {
        onBindItemHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<T> getDataList() {
        return mDataList;
    }


    public void setDataList(Collection<T> list) {
        this.mDataList.clear();
        this.mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> list) {
        int lastIndex = this.mDataList.size();
        if (this.mDataList.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }


    public void remove(int position) {
        this.mDataList.remove(position);
        notifyItemRemoved(position);
        if (position != (getDataList().size())) { // 如果移除的是最后一个，忽略
            notifyItemRangeChanged(position, this.mDataList.size() - position);
        }
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void adpterNotifyDataSetChanged()
    {
        notifyDataSetChanged();
    }
}
