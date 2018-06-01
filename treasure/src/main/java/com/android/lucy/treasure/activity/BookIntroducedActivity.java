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
import com.android.lucy.treasure.bean.BookDataInfo;
import com.android.lucy.treasure.bean.SearchDataInfo;
import com.android.lucy.treasure.runnable.BaiduSearchSingleThread;
import com.android.lucy.treasure.runnable.BookImageAsync;
import com.android.lucy.treasure.runnable.ZhuiShuDataAsync;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;
import com.android.lucy.treasure.utils.URLUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 小说详情页面
 */

public class BookIntroducedActivity extends Activity implements BaseReadAsyncTask.OnUpdateDataLisetener, View.OnClickListener {

    private final static int KEY_SOURCE_NO = 0;

    private final static int KEY_SOURCE_OK = 1;

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
    private BookSourceCatalogAdapter bookSourceCatalogAdapter;
    private BookHandler bookHandler;
    private ImageView iv_book_back;
    private ProgressBar pb_source_book;
    private Button bt_add_book;
    private BookDataInfo bookDataInfo;
    private Button bt_book_cache;


    class BookHandler extends MyHandler<BookIntroducedActivity> {

        private boolean bt_flag = true;

        public BookHandler(BookIntroducedActivity bookIntroducedActivity) {
            super(bookIntroducedActivity);
        }

        @Override
        public void myHandleMessage(Message msg) {
            switch (msg.arg1) {
                case KEY_SOURCE_OK:
                    MyLogcat.myLog("arg1_ok:" + msg.arg1);
                    mActivity.get().refurbishApter();
                    if (bt_flag) {
                        mActivity.get().setButtonState(true);
                        lv_source_book.setVisibility(View.VISIBLE);
                        pb_source_book.setVisibility(View.INVISIBLE);
                        bt_flag = false;
                    }
                    break;
                case KEY_SOURCE_NO:
                    break;
            }
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
        iv_book_back = findViewById(R.id.iv_book_back);
        tv_bookName_book = findViewById(R.id.tv_bookName_book);
        tv_author_book = findViewById(R.id.tv_author_book);
        tv_type_book = findViewById(R.id.tv_type_book);
        tv_size_book = findViewById(R.id.tv_size_book);
        tv_timeUpdate_book = findViewById(R.id.tv_timeUpdate_book);
        tv_desc_book = findViewById(R.id.tv_desc_book);
        bt_start_book = findViewById(R.id.bt_book_read);
        bt_add_book = findViewById(R.id.bt_book_add);
        bt_book_cache = findViewById(R.id.bt_book_cache);
        lv_source_book = findViewById(R.id.lv_source_book);
        pb_source_book = findViewById(R.id.pb_source_book);
    }

    private void initDatas() {
        bookHandler = new BookHandler(this);
        Intent intent = getIntent();
        SearchDataInfo searchDataInfo = intent.getParcelableExtra("searchDataInfo");
        //启动百度线程，查找小说来源
        String bookName = searchDataInfo.getBookName();
        String author = searchDataInfo.getAuthor();
        String url = URLUtils.BAiDU_SEARCH_URL_ + bookName;
        bookDataInfo = new BookDataInfo(bookName, author);
        ThreadPool.getInstance().submitTask(new BaiduSearchSingleThread(url, bookDataInfo, bookHandler));
        //启动追书线程，获取字数和最新更新章节
        zhuiShuDataAsync = new ZhuiShuDataAsync(searchDataInfo);
        zhuiShuDataAsync.execute(URLUtils.ZHUISHU_URL + searchDataInfo.getBookUrl_ZhuiShu());
        zhuiShuDataAsync.setOnUpdateDataListener(this);

        new BookImageAsync(iv_cover).execute(searchDataInfo.getImgUrl());
        lv_source_book.setVisibility(View.INVISIBLE);
        pb_source_book.setVisibility(View.VISIBLE);
        bookSourceCatalogAdapter = new BookSourceCatalogAdapter(this, bookDataInfo.getSourceDataInfos(), R.layout.source_list_item);
        lv_source_book.setAdapter(bookSourceCatalogAdapter);

    }

    private void initEvent() {
        bt_start_book.setOnClickListener(this);
        iv_book_back.setOnClickListener(this);
        bt_add_book.setOnClickListener(this);
        bt_book_cache.setOnClickListener(this);
        bt_book_cache.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_book_read:
                bookRead();
                break;
            case R.id.iv_book_back:
                finish();
                Intent intent = new Intent(this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("visibility", true);
                startActivity(intent);
                break;
            case R.id.bt_book_add:
                String bookName = bookDataInfo.getBookName();
                //查询数据
                List<BookDataInfo> booklist = DataSupport.select("bookName")
                        .where("bookName=?", bookName)
                        .limit(1)
                        .find(BookDataInfo.class);
                int delete;
                if (booklist.size() > 0) {
                    if (bookDataInfo.isSaved()) {
                        //删除数据
                        delete = bookDataInfo.delete();
                    } else {
                        //删除数据
                        BookDataInfo bookData = booklist.get(0);
                        delete = bookData.delete();
                    }
                    MyLogcat.myLog("删除数据！" + delete);
                    if (delete > 0) {
                        bt_add_book.setBackgroundColor(getResources().getColor(R.color.hailanse));
                        bt_add_book.setText("加入书架");
                    }
                } else {
                    boolean result = bookDataInfo.save();
                    MyLogcat.myLog("插入数据成功了吗：" + result);
                    if (result) {
                        bt_add_book.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        bt_add_book.setText("移除书籍");
                        MyLogcat.myLog("bookName:" + bookDataInfo);
                    }
                }
                //ThreadPool.getInstance().submitTask(new WriteDataFileThread());
                break;
            case R.id.bt_book_cache:
                ThreadPool.getInstance().submitTask(
                        new Runnable() {
                            @Override
                            public void run() {
                                String xxbiquge = "http://www.baidu.com/link?url=ELobHFFkKKAs5_5rQhQIlQgf7stOj5WLuNOzbyJHjRaK3u3zC02ntERXObEvbQpF";
                                String pbtxt = " https://www.xxbiquge.com/79_79382/324919.html";
                                try {
                                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(pbtxt).openConnection();
                                    urlConnection.setDoInput(true);
                                    MyLogcat.myLog("statusCode:" + urlConnection.getResponseCode());
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        MyLogcat.myLog("line:" + line);
                                    }
                                    reader.close();
                                } catch (IOException e) {
                                    MyLogcat.myLog(pbtxt + "，网页读取失败！");
                                }
                                openUrl(pbtxt);
                                //openUrl(pbtxt);

                            }
                        }
                );
                break;
        }
    }

    public void openUrl(String htmlUrl) {
        try {
            URL url = new URL(htmlUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int statusCode = connection.getResponseCode();
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            MyLogcat.myLog("code:" + statusCode + ",headerFields:" + headerFields.toString());

        } catch (IOException e) {
            MyLogcat.myLog(htmlUrl + "，网页读取失败！");
        }
    }

//    /**
//     * 保存书籍数据到数据库
//     *
//     * @param bookDataInfo 书籍对象
//     * @return 返回书籍的id号
//     */
//    public long saveBookData(BookDataInfo bookDataInfo) {
//        SQLiteDatabase db = Connector.getDatabase();
//        ContentValues values = new ContentValues();
//        values.put("bookName", bookDataInfo.getBookName());
//        values.put("author", bookDataInfo.getAuthor());
//        values.put("sourceUrl", bookDataInfo.getSourceUrl());
//        values.put("sourceName", bookDataInfo.getSourceName());
//        values.put("readChapterid", 0);
//        return db.insert("bookdatainfo", null, values);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLogcat.myLog("Book-onDestroy被调用");
        //activity销毁后不再接收消息
        bookHandler.removeCallbacksAndMessages(null);
        //取消读取线程
        ThreadPool.getInstance().cancelTask();
    }

    /**
     * 更新小说详细数据
     * 包括作者，书名，类型，更新时间，字数
     *
     * @param searchDataInfo 搜索的数据对象
     */
    @Override
    public void setData(SearchDataInfo searchDataInfo) {
        tv_bookName_book.setText(searchDataInfo.getBookName());
        tv_author_book.setText("作者：" + searchDataInfo.getAuthor());
        tv_type_book.setText("类型：" + searchDataInfo.getType());
        tv_desc_book.setText(searchDataInfo.getDesc());
        tv_size_book.setText("总字数：" + searchDataInfo.getBookSize());
        tv_timeUpdate_book.setText(searchDataInfo.getBookUpdateTime());
        progressBar.setVisibility(View.INVISIBLE);
        ll_book_detail.setVisibility(View.VISIBLE);

    }

    /**
     * 设置按钮状态
     *
     * @param state 是否可点击
     */
    public void setButtonState(boolean state) {
        bt_start_book.setEnabled(state);
        bt_start_book.setBackgroundColor(getResources().getColor(R.color.hailanse));
        String bookName = bookDataInfo.getBookName();
        //查询数据
        List<BookDataInfo> booklist = DataSupport.select("bookName")
                .where("bookName=?", bookName)
                .limit(1)
                .find(BookDataInfo.class);
        if (booklist.size() > 0) {
            bt_add_book.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            bt_add_book.setText("移除书籍");
        } else {
            bt_add_book.setBackgroundColor(getResources().getColor(R.color.hailanse));
            bt_add_book.setText("加入书架");
        }
        bt_add_book.setEnabled(state);
    }


    /**
     * 开始阅读
     */
    private void bookRead() {
        Intent intent = new Intent(this, BookContentActivity.class);
        //查询数据
        List<BookDataInfo> booklist = DataSupport.select("bookName")
                .where("bookName=?", bookDataInfo.getBookName())
                .limit(1)
                .find(BookDataInfo.class);
        if (booklist.size() > 0) {
            int readchapterid = booklist.get(0).getReadChapterid();
            String bookName = booklist.get(0).getBookName();
            bookDataInfo.setReadChapterid(readchapterid);
            MyLogcat.myLog("readchapterid:" + readchapterid + ",bookName:" + bookName);
        } else {
            bookDataInfo.setSourceid(0);
        }
        intent.putExtra("baiduInfo", bookDataInfo);
        startActivity(intent);
    }

    /**
     * 刷新适配器
     */
    public void refurbishApter() {
        if (null != bookSourceCatalogAdapter)
            bookSourceCatalogAdapter.notifyDataSetChanged();
    }


}
