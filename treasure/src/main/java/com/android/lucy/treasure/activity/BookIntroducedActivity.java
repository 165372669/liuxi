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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.base.BaseReadAsyncTask;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.SearchInfo;
import com.android.lucy.treasure.bean.SourceInfo;
import com.android.lucy.treasure.runnable.BookImageAsync;
import com.android.lucy.treasure.runnable.ZhuiShuDataAsync;
import com.android.lucy.treasure.runnable.catalog.DDABookCatalogThread;
import com.android.lucy.treasure.runnable.file.WriteDataFileThread;
import com.android.lucy.treasure.runnable.source.BaiduSourceThread;
import com.android.lucy.treasure.utils.ArrayUtils;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;
import com.android.lucy.treasure.utils.URLUtils;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 小说详情页面
 */

public class BookIntroducedActivity extends Activity implements BaseReadAsyncTask.OnUpdateDataLisetener, View.OnClickListener {

    private ImageView iv_cover;
    private TextView tv_author_book;
    private TextView tv_type_book;
    private TextView tv_size_book;
    private TextView tv_timeUpdate_book;
    private TextView tv_bookName_book;
    private TextView tv_desc_book;
    private Button bt_read_book;
    private LinearLayout ll_book_detail;
    private ZhuiShuDataAsync zhuiShuDataAsync;
    private ProgressBar progressBar;
    private BookHandler bookHandler;
    private ImageView iv_book_back;
    private Button bt_add_book;
    private BookInfo bookInfo;
    private Button bt_book_cache;
    private Button bt_book_catalog;


    class BookHandler extends MyHandler<BookIntroducedActivity> {

        private boolean bt_flag = true;

        public BookHandler(BookIntroducedActivity bookIntroducedActivity) {
            super(bookIntroducedActivity);
        }

        @Override
        public void myHandleMessage(Message msg) {
            SourceInfo sourceInfo = (SourceInfo) msg.obj;
            switch (msg.arg1) {
                case KEY_SOURCE_OK:
                    MyLogcat.myLog("arg1:" + msg.arg1);
                    if (!bookInfo.getSourceInfos().contains(sourceInfo)) {
                        //没有包含同一个来源
                        sourceInfo.setBookInfo(bookInfo);
                        bookInfo.getSourceInfos().add(sourceInfo);
                        bookInfo.setSourceName(sourceInfo.getSourceName());
                    }
                    if (bt_flag) {
                        mActivity.get().setReadBookState(true);
                        mActivity.get().setAddBookState(true, "加入书架");
                        bt_flag = false;
                    }
                    break;
                case KEY_SOURCE_AGAIN_OK:
                    MyLogcat.myLog("arg1:" + msg.arg1);
                    //保存章节数据
                    DataSupport.saveAll(sourceInfo.getCatalogInfos());
                    setReadBookState(true);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLogcat.myLog("Book-onDestroy被调用");
        //activity销毁后不再接收消息
        bookHandler.removeCallbacksAndMessages(null);
        //取消读取线程
        ThreadPool.getInstance().cancelTask();
        String addText = bt_add_book.getText().toString();
        if (addText.equals("移除书籍")) {
            bookInfo.save();
            DataSupport.saveAll(bookInfo.getSourceInfos());
            DataSupport.saveAll(bookInfo.getSourceInfos().get(getSourceIndex()).getCatalogInfos());
        }
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
        bt_read_book = findViewById(R.id.bt_book_read);
        bt_add_book = findViewById(R.id.bt_book_add);
        bt_book_catalog = findViewById(R.id.bt_book_catalog);
        bt_book_cache = findViewById(R.id.bt_book_cache);
    }

    private void initDatas() {
        bookHandler = new BookHandler(this);
        Intent intent = getIntent();
        SearchInfo searchInfo = intent.getParcelableExtra("searchInfo");
        String bookName = searchInfo.getBookName();
        String author = searchInfo.getAuthor();
        //启动追书线程，获取字数和最新更新章节
        zhuiShuDataAsync = new ZhuiShuDataAsync(searchInfo);
        zhuiShuDataAsync.execute(URLUtils.ZHUISHU_URL + searchInfo.getBookUrl_ZhuiShu());
        zhuiShuDataAsync.setOnUpdateDataListener(this);
        //查询数据
        List<BookInfo> booklist = DataSupport.where("bookName=?", bookName)
                .limit(1)
                .find(BookInfo.class, true);
        if (booklist.size() > 0) {
            bookInfo = booklist.get(0);
            int sourceIndex = ArrayUtils.getArrayIndex(bookInfo.getSourceInfos(), bookInfo.getSourceName());
            SourceInfo sourceInfo = bookInfo.getSourceInfos().get(sourceIndex);
            ArrayList<CatalogInfo> catalogInfos = (ArrayList<CatalogInfo>) sourceInfo.getCatalogInfos(sourceInfo.getId());
            if (catalogInfos.size() == 0) {
                sourceInfo.setBookInfo(bookInfo);
                ThreadPool.getInstance().submitTask(new DDABookCatalogThread(sourceInfo.getSourceUrl(), sourceInfo,
                        bookName, author, bookHandler));
            }else {
                setReadBookState(true);
            }
            sourceInfo.setCatalogInfos(catalogInfos);
            setAddBookState(true, "移出书架");
            MyLogcat.myLog("查询数据:" + "readchapterid:" + bookInfo.getReadChapterid() + ",pager:" + bookInfo.getReadChapterPager() + ",bookInfo:" + bookInfo);
        } else {
            //启动百度线程，查找小说来源
            String url = URLUtils.BAiDU_SEARCH_URL_ + bookName;
            bookInfo = new BookInfo(bookName, author);
            ThreadPool.getInstance().submitTask(new BaiduSourceThread(url, bookInfo, bookHandler));
        }

        new BookImageAsync(iv_cover).execute(searchInfo.getImgUrl());
    }

    private void initEvent() {
        iv_book_back.setOnClickListener(this);
        bt_read_book.setOnClickListener(this);
        bt_add_book.setOnClickListener(this);
        bt_book_cache.setOnClickListener(this);
        bt_book_catalog.setOnClickListener(this);
        bt_book_cache.setEnabled(true);
        bt_book_catalog.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_book_back:
                finish();
                Intent intent = new Intent(this, BookSearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("visibility", true);
                startActivity(intent);
                break;
            case R.id.bt_book_read:
                bookRead();
                break;
            case R.id.bt_book_add:
                String bookName = bookInfo.getBookName();
                int sourceIndex = getSourceIndex();
                //查询数据
                List<BookInfo> booklist = DataSupport.select("bookName")
                        .where("bookName=?", bookName)
                        .limit(1)
                        .find(BookInfo.class);
                int delete;
                if (booklist.size() > 0) {
                    if (bookInfo.isSaved()) {
                        //删除数据
                        delete = bookInfo.delete();
                        MyLogcat.myLog("删除数据！bookInfo.delete()" + delete);
                    } else {
                        //删除数据
                        BookInfo bookData = booklist.get(0);
                        delete = bookData.delete();
                        MyLogcat.myLog("删除数据！!!bookData.delete()" + delete);
                    }
                    if (delete > 0) {
                        DataSupport.delete(BookInfo.class, bookInfo.getId());
                        DataSupport.delete(SourceInfo.class, bookInfo.getSourceInfos().get(sourceIndex).getId());
                        bookInfo.setReadChapterid(0);
                        bookInfo.setReadChapterPager(0);
                        bt_add_book.setBackgroundColor(getResources().getColor(R.color.hailanse));
                        bt_add_book.setText("加入书架");
                    }
                } else {
                    boolean result = bookInfo.save();
                    DataSupport.saveAll(bookInfo.getSourceInfos());
                    DataSupport.saveAll(bookInfo.getSourceInfos().get(sourceIndex).getCatalogInfos());
                    MyLogcat.myLog("插入数据成功了吗：" + result + ",sourceIndex:" + sourceIndex);
                    if (result) {
                        bt_add_book.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        bt_add_book.setText("移除书籍");
                        MyLogcat.myLog("bookName:" + bookInfo);
                    }
                }
                break;
            case R.id.bt_book_catalog:
                ThreadPool.getInstance().submitTask(new WriteDataFileThread("bookData.db"));
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

    /**
     * 获取来源数据在Source集合里面的Index
     *
     * @return 来源Index
     */
    public int getSourceIndex() {
        //获取当前来源的index
        String sourceName = bookInfo.getSourceName();
        if (null == sourceName) {
            bookInfo.setSourceName(bookInfo.getSourceInfos().get(0).getSourceName());
        }
        int sourceIndex = ArrayUtils.getArrayIndex(bookInfo.getSourceInfos(), sourceName);
        if (sourceIndex == -1) {
            sourceIndex = 0;
        }
        return sourceIndex;
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


    /**
     * 更新小说详细数据
     * 包括作者，书名，类型，更新时间，字数
     *
     * @param searchInfo 搜索的数据对象
     */
    @Override
    public void setData(SearchInfo searchInfo) {
        tv_bookName_book.setText(searchInfo.getBookName());
        tv_author_book.setText("作者：" + searchInfo.getAuthor());
        tv_type_book.setText("类型：" + searchInfo.getType());
        tv_desc_book.setText(searchInfo.getDesc());
        tv_size_book.setText("总字数：" + searchInfo.getBookSize());
        tv_timeUpdate_book.setText(searchInfo.getBookUpdateTime());
        progressBar.setVisibility(View.INVISIBLE);
        ll_book_detail.setVisibility(View.VISIBLE);

    }

    /**
     * 设置开始阅读按钮状态
     *
     * @param state 是否可点击
     */
    public void setReadBookState(boolean state) {
        bt_read_book.setEnabled(state);
        bt_read_book.setBackgroundColor(getResources().getColor(R.color.hailanse));
    }

    /**
     * 设置加入书架按钮状态
     *
     * @param state 是否可点击
     * @param text  重设按钮名
     */
    public void setAddBookState(boolean state, String text) {
        bt_add_book.setEnabled(state);
        bt_add_book.setText(text);
        if (text.equals("移出书架")) {
            bt_add_book.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            bt_add_book.setBackgroundColor(getResources().getColor(R.color.hailanse));
        }

    }


    /**
     * 开始阅读
     */
    private void bookRead() {
        Intent intent = new Intent(this, BookContentActivity.class);
        intent.putExtra("baiduInfo", bookInfo);
        startActivity(intent);
    }


}
