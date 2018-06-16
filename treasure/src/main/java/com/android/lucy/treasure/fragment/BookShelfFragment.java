package com.android.lucy.treasure.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.activity.BookContentActivity;
import com.android.lucy.treasure.activity.BookMainActivity;
import com.android.lucy.treasure.adapter.BookShelfRecyclerViewAdapter;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.BookSourceInfo;
import com.android.lucy.treasure.runnable.catalog.DDABookCatalogThread;
import com.android.lucy.treasure.runnable.catalog.LIABookCatalogThread;
import com.android.lucy.treasure.runnable.file.WriteDataFileThread;
import com.android.lucy.treasure.utils.ArrayUtils;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 书架
 */

public class BookShelfFragment extends Fragment implements OnRefreshListener, BookShelfRecyclerViewAdapter.onSwipeListener {


    private LRecyclerView recyclerView;
    private List<BookInfo> bookInfos;
    private BookShelfRecyclerViewAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private final ShelfHandler shelfHandler = new ShelfHandler((BookMainActivity) getActivity());
    private int bookCount;  //书籍数量
    private boolean isRefresh;//进入时更新

    class ShelfHandler extends MyHandler<BookMainActivity> {

        private int taskCount;

        public ShelfHandler(BookMainActivity bookMainActivity) {
            super(bookMainActivity);
        }

        @Override
        public void myHandleMessage(Message msg) {
            taskCount++;
            switch (msg.arg1) {
                case MyHandler.CATALOG_SOURCE_OK:
                    //成功更新书籍;
                    if (taskCount == adapter.getDataList().size()) {
                        MyLogcat.myLog("taskCount:" + taskCount);
                        recyclerView.refreshComplete(adapter.getDataList().size());//书架更新完成，去掉头部
                        taskCount = 0;
                        List<BookInfo> bookInfosTemp = DataSupport.order("closeTime desc").find(BookInfo.class, true);
                        adapter.setDataList(bookInfosTemp);
                    }
                    break;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        recyclerView = view.findViewById(R.id.rl_bookshelf);
        initDatas();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //倒序查询
        List<BookInfo> bookInfosTemp = DataSupport.order("closeTime desc").find(BookInfo.class, true);
        if (bookCount != bookInfosTemp.size()) {
            recyclerView.refreshComplete(adapter.getDataList().size());//如果书架数量改变，去掉头部。
        }
        bookCount = bookInfosTemp.size();
        adapter.setDataList(bookInfosTemp);//解决RecyclerView不刷新问题
        if (!isRefresh) {
            onRefresh();//进入更新数据
            isRefresh = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 加载数据
     */
    private void initDatas() {
        bookInfos = new ArrayList<>();
        adapter = new BookShelfRecyclerViewAdapter(getContext(), bookInfos);
        adapter.setOnDelListener(this);
        recyclerView.setOnRefreshListener(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        recyclerView.setAdapter(mLRecyclerViewAdapter);
        //添加分割线
        DividerDecoration divider = new DividerDecoration.Builder(getActivity())
                .setHeight(R.dimen.default_divider_height)
                .setLeftPadding(R.dimen.default_divider_leftpadding)
                .setColorResource(R.color.qianhuise)
                .build();
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //设置头部刷新样式
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotate);
    }


    /**
     * 下拉更新小说目录
     */
    @Override
    public void onRefresh() {
        MyLogcat.myLog("正在更新-size:" + adapter.getDataList().size());
        recyclerView.refresh();//刷新的头部自动下拉
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                updateBookAll();
            }
        });
    }

    /**
     * 更新书架全部小说
     */
    public void updateBookAll() {
        List<BookInfo> bookInfos = adapter.getDataList();
        for (BookInfo bookInfo : bookInfos) {
            int sourceIndex = ArrayUtils.getSourceIndex(bookInfo);
            BookSourceInfo bookSourceInfo = bookInfo.getBookSourceInfos().get(sourceIndex);
            String sourceUrl = bookSourceInfo.getSourceUrl();
            if (null == sourceUrl) {
                sourceUrl = bookSourceInfo.getSourceBaiduUrl();
            }
            String sourceType = bookSourceInfo.getWebType();
            switch (sourceType) {
                case Key.KEY_DDA:
                    ThreadPool.getInstance().submitTask(new DDABookCatalogThread(sourceUrl, bookInfo, shelfHandler));
                    break;
                case Key.KEY_LIA:
                    ThreadPool.getInstance().submitTask(new LIABookCatalogThread(sourceUrl, bookInfo, shelfHandler));
                    break;
                default:
                    break;
            }
        }
        if (bookInfos.size() == 0) {
            recyclerView.refreshComplete(0);
        }
    }

    @Override
    public void onDelete(int pos) {
        BookInfo bookInfo = adapter.getDataList().get(pos);
        bookInfo.delete();
        adapter.getDataList().remove(pos);
        adapter.notifyItemRemoved(pos);
        if (pos != (adapter.getDataList().size())) {
            adapter.notifyItemRangeChanged(pos, adapter.getDataList().size() - pos);
        }
        ThreadPool.getInstance().submitTask(new WriteDataFileThread("bookData.db"));
    }

    @Override
    public void onDetails(int pos) {
//        Intent intent = new Intent(getActivity(), BookIntroducedActivity.class);
//        intent.putExtra("baiduInfo", adapter.getDataList().get(pos));
//        startActivity(intent);
    }

    @Override
    public void onTop(int pos) {
        if (pos != 0) {
            BookInfo bookInfo = adapter.getDataList().get(pos);
            adapter.getDataList().remove(pos);
            adapter.notifyItemRemoved(pos);
            adapter.getDataList().add(0, bookInfo);//数据源添加数据
            bookInfo.setCloseTime(System.currentTimeMillis());
            bookInfo.update(bookInfo.getId());
            adapter.notifyItemInserted(0);//添加item到hodler
            //刷新数据
            if (pos != (adapter.getDataList().size())) { // 插入位置0，从位置1开始刷新
                adapter.notifyItemRangeChanged(1, adapter.getDataList().size() - 1);
            }
            //滚动到item
            recyclerView.scrollToPosition(0);
        } else {
            Toast.makeText(getActivity(), "已经是最上面啦!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int pos) {
        Intent intent = new Intent(getActivity(), BookContentActivity.class);
        intent.putExtra("baiduInfo", adapter.getDataList().get(pos));
        startActivity(intent);
    }

}
