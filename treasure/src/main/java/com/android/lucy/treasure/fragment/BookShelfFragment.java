package com.android.lucy.treasure.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.activity.BookContentActivity;
import com.android.lucy.treasure.activity.BookMainActivity;
import com.android.lucy.treasure.adapter.BookShelfAdapter;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.SourceInfo;
import com.android.lucy.treasure.runnable.catalog.DDABookCatalogThread;
import com.android.lucy.treasure.runnable.catalog.LIABookCatalogThread;
import com.android.lucy.treasure.utils.ArrayUtils;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 书架
 */

public class BookShelfFragment extends Fragment implements OnItemClickListener, OnRefreshListener {


    private LRecyclerView recyclerView;
    private List<BookInfo> bookInfos;
    private BookShelfAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private final ShelfHandler shelfHandler = new ShelfHandler((BookMainActivity) getActivity());
    private int bookCount;  //书籍数量
    private boolean startUpdate;//进入时更新


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
                        List<BookInfo> bookInfosTemp = DataSupport.findAll(BookInfo.class, true);
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
        List<BookInfo> bookInfosTemp = DataSupport.findAll(BookInfo.class, true);
        if (bookCount != bookInfosTemp.size()) {
            recyclerView.refreshComplete(adapter.getDataList().size());//如果书架数量改变，去掉头部。
        }
        bookCount = bookInfosTemp.size();
        adapter.setDataList(bookInfosTemp);//解决RecyclerView不刷新问题
        if (!startUpdate) {
            onRefresh();//进入更新数据
            startUpdate = true;
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
        adapter = new BookShelfAdapter(getContext(), bookInfos);
        recyclerView.setOnRefreshListener(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerViewAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mLRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * 书架item点击
     */
    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), BookContentActivity.class);
        intent.putExtra("baiduInfo", adapter.getDataList().get(position));
        startActivity(intent);
    }

    /**
     * 下拉更新
     */
    @Override
    public void onRefresh() {
        MyLogcat.myLog("正在更新" + "-size:" + adapter.getDataList().size());
        updateBookAll();
    }

    public void updateBookAll() {
        List<BookInfo> bookInfos = adapter.getDataList();
        for (BookInfo bookInfo : bookInfos) {
            int sourceIndex = ArrayUtils.getSourceIndex(bookInfo);
            SourceInfo sourceInfo = bookInfo.getSourceInfos().get(sourceIndex);
            String sourceUrl = sourceInfo.getSourceUrl();
            if (null == sourceUrl) {
                sourceUrl = sourceInfo.getSourceBaiduUrl();
            }
            String sourceType = sourceInfo.getWebType();
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
}
