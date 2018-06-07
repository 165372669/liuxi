package com.android.lucy.treasure.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.adapter.BookShelfAdapter;
import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.utils.MyLogcat;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.crud.DataSupport.findAll;

/**
 * 书架
 */

public class BookShelfFragment extends Fragment {


    private LRecyclerView recyclerView;
    private List<BookInfo> bookInfos;
    private RecyclerView.Adapter adapter;
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
    public void onResume() {
        super.onResume();
        MyLogcat.myLog("BookShelfFragment------onResume");
        bookInfos = DataSupport.findAll(BookInfo.class, true);
        mLRecyclerViewAdapter.notifyItemRangeChanged(0, 0);
    }


    private void initDatas() {
        bookInfos = DataSupport.findAll(BookInfo.class, true);
        adapter = new BookShelfAdapter(getContext(), bookInfos);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        recyclerView.setAdapter(mLRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
