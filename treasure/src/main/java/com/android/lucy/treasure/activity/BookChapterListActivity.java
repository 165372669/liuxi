package com.android.lucy.treasure.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.lucy.treasure.MyApp;
import com.android.lucy.treasure.R;
import com.android.lucy.treasure.adapter.ChapterCatalogAdapter;
import com.android.lucy.treasure.bean.ChapterIDAndName;

import java.util.List;

public class BookChapterListActivity extends Activity implements AdapterView.OnItemClickListener {

    private List<ChapterIDAndName> chapterIDAndNames;
    private ListView lv_chapter_catalog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_catalog);
        initView();
        initData();
        initEvent();
    }


    private void initView() {
        lv_chapter_catalog = findViewById(R.id.lv_chapter_catalog);
    }

    private void initData() {
        chapterIDAndNames = getIntent().getParcelableArrayListExtra("chapterIDAndNames");
        ChapterCatalogAdapter chapterCatalogAdapter = new ChapterCatalogAdapter(this, chapterIDAndNames,
                R.layout.chapter_catalog_list_item);
        lv_chapter_catalog.setAdapter(chapterCatalogAdapter);
    }

    private void initEvent() {
        lv_chapter_catalog.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, BookContentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("chapterid", position);
        startActivity(intent);

    }
}
