package com.android.lucy.treasure.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.base.BaseRecyclerViewAdapter;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.holder.SuperViewHolder;
import com.android.lucy.treasure.runnable.async.BookImageAsync;
import com.android.lucy.treasure.view.SwipeMenuView;

import java.util.List;

/**
 * 书架RecyclerView适配器
 */

public class BookShelfRecyclerViewAdapter extends BaseRecyclerViewAdapter<BookInfo> {

    private final Context context;

    public BookShelfRecyclerViewAdapter(Context context, List<BookInfo> bookInfoList) {
        super(context);
        this.context = context;
        setDataList(bookInfoList);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_shelf_recycler;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        BookInfo bookInfo = getDataList().get(position);
        View contentView = holder.getView(R.id.cl_book_shelf);
        ImageView tv_bookImg = (ImageView) holder.getView(R.id.rl_book_img);
        new BookImageAsync(tv_bookImg).execute(bookInfo.getImgUrl());
        TextView tv_bookName = (TextView) holder.getView(R.id.rl_book_name);
        TextView tv_bookAuthor = (TextView) holder.getView(R.id.rl_book_author);
        TextView tv_bookType = (TextView) holder.getView(R.id.rl_book_type);
        TextView tv_bookCount = (TextView) holder.getView(R.id.rl_book_count);
        TextView tv_noChapterCount = (TextView) holder.getView(R.id.rl_book_noChapterCount);
        TextView tv_uptateTime = (TextView) holder.getView(R.id.rl_book_updateTime);
        TextView delete = (TextView) holder.getView(R.id.tv_shelf_delete);
        TextView details = (TextView) holder.getView(R.id.tv_shelf_details);
        TextView top = (TextView) holder.getView(R.id.tv_shelf_top);
        int noChapterCount = bookInfo.getChapterTotal() - (bookInfo.getReadChapterid() + 1);
        tv_noChapterCount.setText(String.valueOf(noChapterCount));
        tv_bookName.setText(bookInfo.getBookName());
        tv_bookAuthor.setText(bookInfo.getAuthor());
        tv_bookType.setText(bookInfo.getType());
        tv_bookCount.setText(bookInfo.getBookCount());
        tv_uptateTime.setText(bookInfo.getUpdateTime());

        //开启左滑功能
        ((SwipeMenuView) holder.getitemView()).setIos(false).setLeftSwipe(true);

        //书架点击
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSwipeListener.onItemClick(position);
            }
        });
        //删除功能
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnSwipeListener) {
                    //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                    mOnSwipeListener.onDelete(position);
                }
            }
        });
        //详情功能
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSwipeListener.onDetails(position);
            }
        });
        //置顶功能
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSwipeListener.onTop(position);
            }
        });
    }


    /**
     * 和Activity通信的接口
     */
    public interface onSwipeListener {
        void onDelete(int pos);

        void onDetails(int pos);

        void onTop(int pos);

        void onItemClick(int pos);

    }

    private onSwipeListener mOnSwipeListener;

    public void setOnDelListener(onSwipeListener mOnDelListener) {
        this.mOnSwipeListener = mOnDelListener;
    }
}
