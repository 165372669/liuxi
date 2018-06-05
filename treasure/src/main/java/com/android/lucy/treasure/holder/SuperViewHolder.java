package com.android.lucy.treasure.holder;

import android.view.View;

import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

/**
 * 书架Holder
 */

public class SuperViewHolder extends LRecyclerViewAdapter.ViewHolder {
    private View itemView;

    public SuperViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public View getView(int id) {
        return itemView.findViewById(id);
    }

}
