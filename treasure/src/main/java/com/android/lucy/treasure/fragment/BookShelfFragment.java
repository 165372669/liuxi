package com.android.lucy.treasure.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.activity.BookContentActivity;
import com.android.lucy.treasure.adapter.BookShelfAdapter;
import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.utils.MyLogcat;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.crud.DataSupport.findAll;

/**
 * 书架
 */

public class BookShelfFragment extends Fragment implements OnItemClickListener, OnRefreshListener {


    private LRecyclerView recyclerView;
    private List<BookInfo> bookInfos;
    private BookShelfAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;


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
        adapter.setDataList(bookInfosTemp);//解决RecyclerView不刷新问题
        MyLogcat.myLog("size:" + adapter.getDataList().size());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

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

    }
}
