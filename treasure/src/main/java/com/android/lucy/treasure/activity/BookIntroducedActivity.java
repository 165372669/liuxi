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
import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.BookSourceInfo;
import com.android.lucy.treasure.dao.BookInfoDao;
import com.android.lucy.treasure.runnable.catalog.BiqiugeBookCatalogThread;
import com.android.lucy.treasure.runnable.catalog.DDABookCatalogThread;
import com.android.lucy.treasure.runnable.catalog.LIABookCatalogThread;
import com.android.lucy.treasure.runnable.file.WriteDataFileThread;
import com.android.lucy.treasure.runnable.source.BaiduSourceThread;
import com.android.lucy.treasure.utils.ArrayUtils;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.SystemUtils;
import com.android.lucy.treasure.utils.ThreadPool;
import com.android.lucy.treasure.utils.URLUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 小说详情页面
 */

public class BookIntroducedActivity extends Activity implements View.OnClickListener {

    public static int readChapterid = 0;
    public static int readChapterPager = 0;

    private ImageView iv_cover;
    private TextView tv_author_book;
    private TextView tv_type_book;
    private TextView tv_size_book;
    private TextView tv_timeUpdate_book;
    private TextView tv_bookName_book;
    private TextView tv_desc_book;
    private Button bt_read_book;
    private LinearLayout ll_book_detail;
    private ProgressBar progressBar;
    private BookHandler bookHandler;
    private ImageView iv_book_back;
    private Button bt_add_book;
    private BookInfo bookInfo;
    private Button bt_book_cache;
    private Button bt_book_catalog;
    private int sourceIndex;


    class BookHandler extends MyHandler<BookIntroducedActivity> {


        public BookHandler(BookIntroducedActivity bookIntroducedActivity) {
            super(bookIntroducedActivity);
        }

        @Override
        public void myHandleMessage(Message msg) {

            switch (msg.arg1) {
                case BAIDU_SEARCH_OK:
                    if (bookInfo.getBookSourceInfos().size() > 0) {
                        MyLogcat.myLog("bookName:" + bookInfo.getBookName() + ",sourceDataSize:" + bookInfo.getBookSourceInfos());
                        BookSourceInfo bookSourceInfo = bookInfo.getBookSourceInfos().get(0);
                        bookInfo.setSourceName(bookSourceInfo.getSourceName());//设置当前来源名；
                        sourceIndex = 0;
                        bookInfo.setSourceIndex(sourceIndex);
                        String webType = bookSourceInfo.getWebType();
                        switch (webType) {
                            case Key.KEY_DDA:
                                ThreadPool.getInstance().submitTask(new DDABookCatalogThread(bookSourceInfo.getSourceBaiduUrl(),
                                        bookInfo, bookHandler));
                                break;
                            case Key.KEY_LIA:
                                ThreadPool.getInstance().submitTask(new LIABookCatalogThread(bookSourceInfo.getSourceBaiduUrl(),
                                        bookInfo, bookHandler));
                                break;
                        }
                    } else {
                        //重新请求获取百度来源
                        ThreadPool.getInstance().submitTask(new BaiduSourceThread(URLUtils.BAiDU_SEARCH_URL_ + bookInfo.getBookName(),
                                bookInfo, bookHandler));
                    }
                    break;
                case BAIDU_SEARCH_NO:
                    MyLogcat.myLog("百度来源网页读取失败");
                    if (SystemUtils.isNetworkAvalible(MyApplication.getContext())) {
                        MyLogcat.myLog("有网络");
                        //重新请求获取百度来源
                        ThreadPool.getInstance().submitTask(new BaiduSourceThread(URLUtils.BAiDU_SEARCH_URL_ + bookInfo.getBookName(),
                                bookInfo, bookHandler));
                    } else {
                        MyLogcat.myLog("无网络");
                        //设置网络
                        SystemUtils.checkNetwork(mActivity.get());
                    }
                    break;
                case CATALOG_SOURCE_OK:
                    tv_size_book.setText(bookInfo.getBookCount());
                    tv_timeUpdate_book.setText(bookInfo.getUpdateTime());
                    bookInfo.setUpdateTime(bookInfo.getUpdateTime());//更新时间
                    progressBar.setVisibility(View.INVISIBLE);
                    ll_book_detail.setVisibility(View.VISIBLE);
                    setReadBookState(true);
                    setAddBookState(true, "加入书架");
                    break;
                case CATALOG_SOURCE_NO:
                    MyLogcat.myLog("章节网页未获取到数据");
                    break;
                case CATALOG_SOURCE_ERROR:
                    MyLogcat.myLog("章节网页读取失败");
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
    protected void onRestart() {
        super.onRestart();
        bookInfo.setReadChapterid(readChapterid);
        bookInfo.setReadChapterPager(readChapterPager);
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
            DataSupport.saveAll(bookInfo.getBookSourceInfos());
            DataSupport.saveAll(bookInfo.getBookCatalogInfos());
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
        bookInfo = (BookInfo) getIntent().getSerializableExtra("bookInfo");
        tv_bookName_book.setText(bookInfo.getBookName());
        tv_author_book.setText(bookInfo.getAuthor());
        tv_type_book.setText(bookInfo.getType());
        tv_desc_book.setText(bookInfo.getSynopsis());
        MyLogcat.myLog("bookInfo:" + bookInfo.toString());
        //启动详情页面线程，获取字数和最新更新章节
//        zhuiShuDataAsync = new ZhuiShuDataAsync(searchInfo);
//        //zhuiShuDataAsync.execute(URLUtils.ZHUISHU_URL + searchInfo.getBookUrl_ZhuiShu());
//        zhuiShuDataAsync.setOnUpdateDataListener(this);
        ThreadPool.getInstance().submitTask(new BiqiugeBookCatalogThread(bookInfo.getDatailsUrl(), bookInfo, bookHandler));
        //查询数据
        if (bookInfo.getId() > 0) {
            sourceIndex = ArrayUtils.getSourceIndex(bookInfo);
            bookInfo.setSourceIndex(sourceIndex);
            setReadBookState(true);
            setAddBookState(true, "移除书籍");
            MyLogcat.myLog("查询数据:" + "readchapterid:" + bookInfo.getReadChapterid() + ",pager:" + bookInfo.getReadChapterPager() + ",bookInfo:" + bookInfo);
        } else {
            //启动百度线程，查找小说来源
//            String url = URLUtils.BAiDU_SEARCH_URL_ + bookName;
//            ThreadPool.getInstance().submitTask(new BaiduSourceThread(url, bookInfo, bookHandler));
        }
//        bookInfo.setType(bookInfo.getType()); //类型
//        bookInfo.setImgUrl(bookInfo.getImgUrl());//封面链接
//        //bookInfo.setZhuishuUrl(searchInfo.getBookUrl_ZhuiShu());//追书页面链接
//        new BookImageAsync(iv_cover).execute(bookInfo.getImgUrl());
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
                        MyLogcat.myLog("删除数据！bookInfo.delete():" + delete);
                    } else {
                        //删除数据
                        BookInfo bookData = booklist.get(0);
                        delete = bookData.delete();
                        MyLogcat.myLog("删除数据！!!bookData.delete():" + delete);
                    }
                    // DataSupport.delete(BookInfo.class, bookInfo.getId());
                    bookInfo.setReadChapterid(0);
                    bookInfo.setReadChapterPager(0);
                    bookInfo.setId(0);
                    bt_add_book.setBackgroundColor(getResources().getColor(R.color.zhonghailanse));
                    bt_add_book.setText("加入书架");
                } else {
                    boolean result = bookInfo.save();
                    long bookId = 0;
                    if (!result) {
                        bookId = BookInfoDao.saveBookData(bookInfo); //存储数据
                    }
                    if (result || bookId > 0) {
                        DataSupport.saveAll(bookInfo.getBookSourceInfos());
                        DataSupport.saveAll(bookInfo.getBookCatalogInfos());
                        bt_add_book.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        bt_add_book.setText("移除书籍");
                    }

                    MyLogcat.myLog("result:" + result + ",bookId:" + bookId + ",bookName:" + bookInfo);
                }
                ThreadPool.getInstance().submitTask(new WriteDataFileThread("bookData.db"));
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
                                String baidu = "http://www.baidu.com/s?wd=心魔";
                                try {
                                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(baidu).openConnection();
                                    urlConnection.setDoInput(true); //允许输入流，即允许下载
                                    urlConnection.setDoInput(true);
                                    urlConnection.setUseCaches(false); //不使用缓冲
                                    urlConnection.setRequestMethod("GET"); //使用get请求
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                    String readData = null;
                                    String line;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    while ((line = reader.readLine()) != null) {
                                        readData = readData + line + "\n";
                                    }
                                    Document doc = Jsoup.parse(readData);
                                    //获取第一条网址
                                    Elements as = doc.select("a[class^=c-showurl]");
                                    MyLogcat.myLog("as:" + as);
                                    reader.close();
                                } catch (IOException e) {
                                    MyLogcat.myLog(pbtxt + "，网页读取失败！");
                                }
                                //openUrl(pbtxt);

                            }
                        }
                );
                break;
        }
    }


    /**
     * 设置开始阅读按钮状态
     *
     * @param state 是否可点击
     */
    public void setReadBookState(boolean state) {
        bt_read_book.setEnabled(state);
        bt_read_book.setBackgroundColor(getResources().getColor(R.color.zhonghailanse));
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
        if (text.equals("移除书籍")) {
            bt_add_book.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            bt_add_book.setBackgroundColor(getResources().getColor(R.color.zhonghailanse));
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
