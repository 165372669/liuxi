package com.android.lucy.treasure.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.holder.SuperViewHolder;
import com.android.lucy.treasure.runnable.async.BookImageAsync;

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
        ImageView tv_bookImg = (ImageView) holder.getView(R.id.rl_book_img);
        new BookImageAsync(tv_bookImg).execute(bookInfo.getImgUrl());
        TextView tv_bookName = (TextView) holder.getView(R.id.rl_book_name);
        TextView tv_bookAuthor = (TextView) holder.getView(R.id.rl_book_author);
        TextView tv_bookType = (TextView) holder.getView(R.id.rl_book_type);
        TextView tv_bookCount = (TextView) holder.getView(R.id.rl_book_count);
        TextView tv_noChapterCount = (TextView) holder.getView(R.id.rl_book_noChapterCount);
        TextView tv_uptateTime = (TextView) holder.getView(R.id.rl_book_updateTime);
        int noChapterCount = bookInfo.getChapterTotal() - (bookInfo.getReadChapterid() + 1);
        tv_noChapterCount.setText(String.valueOf(noChapterCount));
        tv_bookName.setText(bookInfo.getBookName());
        tv_bookAuthor.setText(bookInfo.getAuthor());
        tv_bookType.setText(bookInfo.getType());
        tv_bookCount.setText(bookInfo.getWordCountTotal());
        tv_uptateTime.setText(bookInfo.getUpdateTime());
    }

}
