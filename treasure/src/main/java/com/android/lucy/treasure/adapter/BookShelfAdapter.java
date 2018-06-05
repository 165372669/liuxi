package com.android.lucy.treasure.adapter;

import android.content.Context;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.holder.SuperViewHolder;

import java.util.List;

/**
 * 书架RecyclerView适配器
 */

public class BookShelfAdapter extends RecyclerViewBaseAdapter<BookInfo> {

    public BookShelfAdapter(Context context, List<BookInfo> bookInfoList) {
        super(context);
        setDataList(bookInfoList);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_shelf_recycler;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        BookInfo bookInfo = getDataList().get(position);
        TextView bookName = (TextView) holder.getView(R.id.rl_book_name);
        TextView sourceName = (TextView) holder.getView(R.id.rl_book_sourceName);
        bookName.setText(bookInfo.getBookName());
        sourceName.setText(bookInfo.getSourceName());
    }
}
