package com.android.lucy.treasure.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.adapter.ChapterCatalogAdapter;
import com.android.lucy.treasure.bean.BaiduSearchDataInfo;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.ChapterIDAndName;

import java.util.List;

public class BookChapterCatalog extends Activity {

    private List<ChapterIDAndName> chapterIDAndNames;
    private ListView lv_chapter_catalog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_catalog);
        initView();
        initData();
    }

    private void initView() {
        lv_chapter_catalog = findViewById(R.id.lv_chapter_catalog);
    }

    private void initData() {
        chapterIDAndNames = getIntent().getParcelableArrayListExtra("chapterIDAndNames");
        ChapterCatalogAdapter chapterCatalogAdapter = new ChapterCatalogAdapter(this, chapterIDAndNames,
                R.layout.chapter_catalog_list_item);
        lv_chapter_catalog.setAdapter(chapterCatalogAdapter);
        lv_chapter_catalog.setOnItemClickListener(chapterCatalogAdapter);
    }

}
