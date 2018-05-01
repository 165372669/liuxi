package com.android.lucy.treasure.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.adapter.BookSourceCatalogAdapter;
import com.android.lucy.treasure.base.BaseReadAsyncTask;
import com.android.lucy.treasure.bean.BaiduSearchDataInfo;
import com.android.lucy.treasure.bean.SearchDataInfo;
import com.android.lucy.treasure.runnable.BaiduSearchSingleThread;
import com.android.lucy.treasure.runnable.BookImageAsync;
import com.android.lucy.treasure.runnable.ZhuiShuDataAsync;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;
import com.android.lucy.treasure.utils.URLUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 小说详情页面
 */

public class BookIntroducedActivity extends Activity implements BaseReadAsyncTask.OnUpdateDataLisetener,View.OnClickListener{

    private ImageView iv_cover;
    private TextView tv_author_book;
    private TextView tv_type_book;
    private TextView tv_size_book;
    private TextView tv_timeUpdate_book;
    private TextView tv_bookName_book;
    private TextView tv_desc_book;
    private Button bt_start_book;
    private LinearLayout ll_book_detail;
    private ZhuiShuDataAsync zhuiShuDataAsync;
    private ProgressBar progressBar;
    private ListView lv_source_book;
    private List<BaiduSearchDataInfo> baiduSearchDataInfos;
    private BookSourceCatalogAdapter bookSourceCatalogAdapter;
    private BookHandler bookHandler;


    class BookHandler extends MyHandler<BookIntroducedActivity> {

        private boolean bt_flag=true;

        public BookHandler(BookIntroducedActivity bookIntroducedActivity) {
            super(bookIntroducedActivity);
        }

        @Override
        public void myHandleMessage(Message msg) {

            if(bt_flag){
                mActivity.get().setButtonState(true,"开始阅读");
                bt_flag=false;
            }
            BaiduSearchDataInfo info = (BaiduSearchDataInfo) msg.obj;
            mActivity.get().addInfo(info);
            mActivity.get().refurbishApter();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        initViews();
        initDatas();
        initEvent();
    }


    private void initViews() {

        FrameLayout fl_book_detail_root = findViewById(R.id.ll_book_detail_root);
        ll_book_detail = findViewById(R.id.ll_book_detail);
        ll_book_detail.setVisibility(View.INVISIBLE);

//      添加自定义圆形滚动
        FrameLayout.LayoutParams prigress_layoutParams = new FrameLayout.LayoutParams(100, 100);
        progressBar = new ProgressBar(this);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar_custom));
        prigress_layoutParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(prigress_layoutParams);
        fl_book_detail_root.addView(progressBar);


        iv_cover = findViewById(R.id.iv_cover);
        tv_bookName_book = findViewById(R.id.tv_bookName_book);
        tv_author_book = findViewById(R.id.tv_author_book);
        tv_type_book = findViewById(R.id.tv_type_book);
        tv_size_book = findViewById(R.id.tv_size_book);
        tv_timeUpdate_book = findViewById(R.id.tv_timeUpdate_book);
        tv_desc_book = findViewById(R.id.tv_desc_book);
        bt_start_book = findViewById(R.id.bt_start_book);
        setButtonState(false,"寻找来源");
        lv_source_book = findViewById(R.id.lv_source_book);
    }

    private void initDatas() {
        bookHandler = new BookHandler(this);
        baiduSearchDataInfos = new ArrayList<>();
        Intent intent = getIntent();
        SearchDataInfo searchDataInfo = intent.getParcelableExtra("searchDataInfo");
        //启动百度线程，查找小说来源
        String bookName = searchDataInfo.getBookName();
        String author=searchDataInfo.getAuthor();
        String url = URLUtils.BAiDU_SEARCH_URL_ + bookName;
        ThreadPool.getInstance().submitTask(new BaiduSearchSingleThread(url, bookName, author,bookHandler));
        //启动追书线程，获取字数和最新更新章节
        zhuiShuDataAsync = new ZhuiShuDataAsync(searchDataInfo);
        zhuiShuDataAsync.execute(URLUtils.ZHUISHU_URL + searchDataInfo.getBookUrl_ZhuiShu());
        zhuiShuDataAsync.setOnUpdateDataListener(this);

        new BookImageAsync(iv_cover).execute(searchDataInfo.getImgUrl());
        bookSourceCatalogAdapter = new BookSourceCatalogAdapter(this, baiduSearchDataInfos, R.layout.source_list_item);
        lv_source_book.setAdapter(bookSourceCatalogAdapter);
    }

    private void initEvent() {
        bt_start_book.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start_book:
                bookRead();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLogcat.myLog("Book-onDestroy被调用");
        //activity销毁后不再接收消息
        bookHandler.removeCallbacksAndMessages(null);
        //取消读取线程
        ThreadPool.getInstance().cancelTask();
    }

    //更新小说详情数据
    @Override
    public void setData(SearchDataInfo searchDataInfo) {
        tv_bookName_book.setText(searchDataInfo.getBookName());
        tv_author_book.setText(searchDataInfo.getAuthor() + "著");
        tv_type_book.setText(searchDataInfo.getType());
        tv_desc_book.setText(searchDataInfo.getDesc());
        tv_size_book.setText(searchDataInfo.getBookSize());
        tv_timeUpdate_book.setText(searchDataInfo.getBookUpdateTime());
        progressBar.setVisibility(View.INVISIBLE);
        ll_book_detail.setVisibility(View.VISIBLE);
    }
    /*
    * 设置按钮状态
    * */
    public void setButtonState(boolean state,String text)
    {
        bt_start_book.setEnabled(state);
        bt_start_book.setText(text);
    }


    /*
* 开始阅读
* */
    private void bookRead() {
        Intent intent=new Intent(this,BookContentActivity.class);
        BaiduSearchDataInfo baiduSearchDataInfo = baiduSearchDataInfos.get(0);
        intent.putExtra("baiduInfo", baiduSearchDataInfo);
        startActivity(intent);
    }

    /**
     * 添加小说来源数据
     */
    public void addInfo(BaiduSearchDataInfo info) {
        //没有包含同一个来源
        if (!baiduSearchDataInfos.contains(info))
            baiduSearchDataInfos.add(info);
    }

    /*
    * 刷新适配器
    * */
    public void refurbishApter() {
        if (null != bookSourceCatalogAdapter)
            bookSourceCatalogAdapter.notifyDataSetChanged();
    }


}
