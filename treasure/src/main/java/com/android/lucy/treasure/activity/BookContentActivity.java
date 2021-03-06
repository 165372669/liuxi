package com.android.lucy.treasure.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.adapter.BookContentPagerAdapter;
import com.android.lucy.treasure.bean.BookCatalogInfo;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.ChapterIDAndName;
import com.android.lucy.treasure.bean.ChapterPagerContentInfo;
import com.android.lucy.treasure.pager.ContentPager;
import com.android.lucy.treasure.service.ChapterContentService;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.MyMathUtils;
import com.android.lucy.treasure.view.ChapterViewPager;
import com.android.lucy.treasure.view.CircleProgress;

import java.util.ArrayList;

/**
 * 小说内容页面,包含一个ViewPage，ViewPage里面添加ContentPagerView
 * 将一个章节的内容分割成一个若干个页面。
 */

public class BookContentActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ServiceConnection connection;
    private ChapterContentService service;
    private BookInfo bookInfo;
    private ChapterViewPager viewPager;
    private BookContentPagerAdapter adapter;
    private ArrayList<ContentPager> contentPagers;
    private TextView tv_chapter_catalog;
    private CircleProgress cv_chapter_progress;
    private ArrayList<BookCatalogInfo> bookCatalogInfos;
    private BookContentActivity activity;

    public class ChapterContentHandler extends MyHandler<BookContentActivity> {

        public ChapterContentHandler(BookContentActivity bookContentActivity) {
            super(bookContentActivity);
        }

        @Override
        public void myHandleMessage(Message msg) {
            int loadChapterId = msg.arg2 - 1;
            switch (msg.arg1) {
                case MyHandler.CHAPTER_LOADING_OK://成功下载到章节并添加到页面集合
                    MyLogcat.myLog("下载的章节id:" + (loadChapterId) + "，适配器的章节id:" + adapter.getCurrentChapterId());
                    if (loadChapterId == adapter.getCurrentChapterId()) {
                        adapter.notifyDataSetChanged();
                    }
                    cv_chapter_progress.setVisibility(View.GONE);
                    cv_chapter_progress.setProgress(0);
                    break;
                case MyHandler.CHAPTER_LOADING_NO:
                    //获取章节内容失败
                case MyHandler.CHAPTER_LOADING_ERROR:
                    //获取章节网页读取失败
                    MyLogcat.myLog("失败章节:" + (msg.arg2 - 1));
                    BookCatalogInfo bookCatalogInfo = bookCatalogInfos.get(loadChapterId);
                    bookCatalogInfo.setChapterPagerContentInfos(null);
                    //5次后处理
                    break;
                default:
                    break;

            }
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_content);
        if (Build.VERSION.SDK_INT > 21) {
            //设置为透明色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            flagsVisibility(false);
            MyLogcat.myLog("状态栏的高度：" + getStatusBarHeight(this.getBaseContext()));
        }
        MyLogcat.myLog("BookContentActivity-启动activity");
        activity = this;
        initViews();
        initDatas();
        initEvent();
    }

    //后台切回前台并接收数据
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int chapterId = intent.getExtras().getInt("chapterid", -1);
        MyLogcat.myLog("BookContentActivity-切回前台" + chapterId);
        viewPager.popUpMenu();
        if (chapterId != -1) {
            adapter.setCurrentChapterId(chapterId, 0);
            adapter.notifyDataSetChanged();
        }
    }

    //活动不可见
    @Override
    protected void onPause() {
        super.onPause();
        if (bookInfo.getId() > 0) {
            BookIntroducedActivity.readChapterid = adapter.getCurrentChapterId();
            BookIntroducedActivity.readChapterPager = adapter.getPagerPosition();
            int readChapterid = adapter.getCurrentChapterId();
            int readChapterPager = adapter.getPagerPosition();
            if (readChapterid == 0) {
                bookInfo.setToDefault("readChapterid");
            } else {
                bookInfo.setReadChapterid(readChapterid);
            }
            if (readChapterPager == 0) {
                bookInfo.setToDefault("readChapterPager");
            } else {
                bookInfo.setReadChapterPager(readChapterPager);
            }
            bookInfo.setReadChapterName(bookCatalogInfos.get(readChapterid).getChapterName());
            long closeTime = System.currentTimeMillis();
            bookInfo.setCloseTime(closeTime);
            int update = bookInfo.update(bookInfo.getId());
            MyLogcat.myLog("onPause：id:" + bookInfo.getId() + ",readChapterid:" +
                    bookInfo.getReadChapterid() + ",closeTime" + bookInfo.getCloseTime() + ",update:" + update);
        } else {
            BookIntroducedActivity.readChapterid = 0;
            BookIntroducedActivity.readChapterPager = 0;
            MyLogcat.myLog("无保存书籍");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLogcat.myLog("BookContentActivity---销毁");
        //解绑service
        if (null != connection)
            unbindService(connection);
        //停止服务
/*        Intent intent = new Intent(this, ChapterContentService.class);
        stopService(intent);*/
    }

    private void initViews() {
        viewPager = findViewById(R.id.vp_book_content);
        LinearLayout ll_chapter_titleBar = findViewById(R.id.ll_chapter_titleBar);
        LinearLayout ll_chapter_setings = findViewById(R.id.ll_chapter_setings);
        ll_chapter_titleBar.setPadding(0, getStatusBarHeight(this.getBaseContext()), 0, 0);
        viewPager.setOtherView(ll_chapter_titleBar, ll_chapter_setings);
        viewPager.setActivity(this);
        tv_chapter_catalog = findViewById(R.id.tv_chapter_catalog);
        cv_chapter_progress = findViewById(R.id.cv_chapter_progress);
        cv_chapter_progress.setVisibility(View.GONE);
    }

    private void initDatas() {
        contentPagers = new ArrayList<>();
        ChapterContentHandler chapterContentHandler = new ChapterContentHandler(this);
        bookInfo = (BookInfo) getIntent().getSerializableExtra("baiduInfo");
        bookCatalogInfos = bookInfo.getBookCatalogInfos();
        //启动服务
        Intent intent = new Intent(this, ChapterContentService.class);
        startService(intent);
        //绑定service服务
        connection = new ServiceConnection() {
            //当activity跟service成功连接之后会调用这个方法
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                ChapterContentService.MyBinder mBinder = (ChapterContentService.MyBinder) iBinder;
                service = mBinder.getService();
                MyLogcat.myLog("Activity-onServiceConnected");
                service.setData(bookInfo, activity, chapterContentHandler);
                int readChapterid = bookInfo.getReadChapterid();
                int readChapterPager = bookInfo.getReadChapterPager();
                adapter.setCurrentChapterId(readChapterid, readChapterPager);
                service.startThreadProgress(readChapterid);
                cv_chapter_progress.setVisitiby(true);
            }

            //当activity跟service的连接意外丢失的时候会调用
            //比如service崩溃了，或被强行杀死了
            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        //启动绑定服务
        bindService(intent, connection, BIND_AUTO_CREATE);
        for (int i = 0; i < 5; i++) {
            ContentPager contentPager = new ContentPager(this);
            contentPager.setTextSize(MyMathUtils.dip2px(this, 20));
            contentPagers.add(contentPager);
        }
        adapter = new BookContentPagerAdapter(contentPagers, bookCatalogInfos, viewPager, activity);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(contentPagers.size() * 100, false);
        viewPager.setChapterDatas(bookCatalogInfos);
        viewPager.setChapterTotal(bookCatalogInfos.size());
    }

    private void initEvent() {
        viewPager.addOnPageChangeListener(this);
        tv_chapter_catalog.setOnClickListener(this);
    }

    /**
     * 获取状态栏高度
     *
     * @param context 全局
     * @return 状态栏高度
     */
    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 下载章节
     *
     * @param chapterId 章节id
     * @param flag      是否显示圆形加载
     */
    public void loadChapter(int chapterId, boolean flag) {
        if (flag) {
            cv_chapter_progress.setVisibility(View.VISIBLE);
            cv_chapter_progress.setProgress(0);
        }
        if (null != service) {
            service.startThreadProgress(chapterId);
        }
    }


    /**
     * 预加载章节
     *
     * @param chapterId 当前章节id
     */
    public void preLoading(int chapterId) {
        int nextChapterid = chapterId + 1;
        int lastChapterid = chapterId - 1;
        if (nextChapterid < bookCatalogInfos.size()) {
            //预加载章节
            BookCatalogInfo bookCatalogInfo = bookCatalogInfos.get(nextChapterid);
            if (bookCatalogInfo.getChapterPagerToatal() == 0) {
                loadChapter(nextChapterid, false);
            }
        }
        if (lastChapterid >= 0) {
            //预加载章节
            BookCatalogInfo bookCatalogInfo = bookCatalogInfos.get(lastChapterid);
            if (bookCatalogInfo.getChapterPagerToatal() == 0) {
                loadChapter(lastChapterid, false);
            }
        }

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //MyLogcat.myLog("onPageScrolled >");
        //滑动：onPageScrolled > instantiateItem
        //点击：instantiateItem > onPageSelected >onPageScrolled

    }

    @Override
    public void onPageSelected(int position) {
        int chapterid = adapter.getCurrentChapterId();
        int currentPagerPosition = adapter.getPagerPosition();
        if (currentPagerPosition > 1) {
            preLoading(chapterid);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * 设置栏点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //目录点击
            case R.id.tv_chapter_catalog:
                ArrayList<ChapterIDAndName> chapterIDAndNames = new ArrayList<>();
                for (int i = 0; i < bookCatalogInfos.size(); i++) {
                    BookCatalogInfo bookCatalogInfo = bookCatalogInfos.get(i);
                    chapterIDAndNames.add(new ChapterIDAndName(bookCatalogInfo.getChapterId()
                            , bookCatalogInfo.getChapterName()));
                }
                Intent intent = new Intent(this, BookChapterListActivity.class);
                intent.putParcelableArrayListExtra("chapterIDAndNames", chapterIDAndNames);
                intent.putExtra("readChapterId", adapter.getCurrentChapterId());
                startActivity(intent);
                break;
        }
    }


    /**
     * 隐藏显示状态栏
     *
     * @param flag 隐藏或者显示
     */
    public void flagsVisibility(boolean flag) {
        if (flag)
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 获取圆形进度
     *
     * @return
     */
    public CircleProgress getCircleProgress() {
        return cv_chapter_progress;
    }

}
