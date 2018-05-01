package com.android.lucy.treasure.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.adapter.BookContentPagerAdapter;
import com.android.lucy.treasure.bean.BaiduSearchDataInfo;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.PagerContentInfo;
import com.android.lucy.treasure.pager.ContentPager;
import com.android.lucy.treasure.service.ChapterContentService;
import com.android.lucy.treasure.service.ChapterContentService.OnChapterContentListener;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.MyMathUtils;
import com.android.lucy.treasure.utils.ThreadPool;
import com.android.lucy.treasure.view.ChapterViewPager;
import com.android.lucy.treasure.view.MyFrameLayout;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 小说内容页面,包含一个ViewPage，ViewPage里面添加ContentPagerView
 * 将一个章节的内容分割成一个若干个View。
 */

public class BookContentActivity extends Activity implements OnChapterContentListener, ViewPager.OnPageChangeListener {

    private ServiceConnection connection;
    private ChapterContentService service;
    private ContentPager contentPagerView;
    private BaiduSearchDataInfo bookDataInfo;
    private ChapterViewPager viewPager;
    private BookContentPagerAdapter<PagerContentInfo> adapter;
    private MyFrameLayout frameLayout;
    private ArrayList<ContentPager> contentPagers;
    private LinkedList<PagerContentInfo> pagerContentInfos;
    private int chapterTotal;
    private int loadChapterId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_book_content);
        initViews();
        initDatas();
        initEvent();
    }


    private void initViews() {
        viewPager = findViewById(R.id.vp_book_content);
        frameLayout = findViewById(R.id.fl_book_content);
    }

    private void initDatas() {
        contentPagerView = new ContentPager(this);
        contentPagers = new ArrayList<>();
        pagerContentInfos = new LinkedList<>();
        bookDataInfo = (BaiduSearchDataInfo) getIntent().getSerializableExtra("baiduInfo");
        String bookName = bookDataInfo.getBookName();
        chapterTotal = bookDataInfo.getCatalogInfos().size();
        MyLogcat.myLog("chapterTotal:" + chapterTotal);
        if (null != bookDataInfo) {
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
                    service.setData(bookDataInfo, contentPagerView, pagerContentInfos);
                    service.setOnChapterContentListener(BookContentActivity.this);
                    service.startThread(1, 0);
                }

                //当activity跟service的连接意外丢失的时候会调用
                //比如service崩溃了，或被强行杀死了
                @Override
                public void onServiceDisconnected(ComponentName componentName) {

                }
            };
            //启动绑定服务
            bindService(intent, connection, BIND_AUTO_CREATE);
        }
        for (int i = 0; i < 5; i++) {
            ContentPager contentPager = new ContentPager(this);
            contentPager.setTextSize(MyMathUtils.dip2px(this, 20));
            contentPager.setBookName(bookName);
            contentPagers.add(contentPager);
        }
        adapter = new BookContentPagerAdapter<>(contentPagers, pagerContentInfos, viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(contentPagers.size() * 100, false);
        viewPager.setChapterDatas(pagerContentInfos);
        viewPager.setChapterTotal(chapterTotal);
    }

    private void initEvent() {
        viewPager.addOnPageChangeListener(this);
    }


    /*
    *加载章内容数据。和章节名等，是ChapterContentService的回调方法
    * */
    @Override
    public void setChapterContent() {

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        if (null != pagerContentInfos && pagerContentInfos.size() > 0) {
//            currentPagerPosition = adapter.getPagerPosition();
//            viewPager.setCurrentPagerPosition(currentPagerPosition);
//            MyLogcat.myLog("onPageScrolled::" + "currentPagerPosition：" + currentPagerPosition);
//        }
    }

    @Override
    public void onPageSelected(int position) {
        int currentPagerPosition = adapter.getPagerPosition();
        viewPager.setCurrentPagerPosition(currentPagerPosition);
        MyLogcat.myLog("activiaty----" + "当前页面集合位置：" + currentPagerPosition);
        loadNextChapter(currentPagerPosition);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 功能：加载章节a
     *
     * @param currentPagerPosition 页面集合当前位置
     */
    public void loadNextChapter(int currentPagerPosition) {
        PagerContentInfo pagerContentInfo = pagerContentInfos.get(currentPagerPosition);
        int currentChapterId = pagerContentInfo.getChapterId();
        int currentChapterPager = pagerContentInfo.getCurrentPager();
        int chapterPagerTotal = pagerContentInfo.getChapterPagerToatal();
        MyLogcat.myLog("当前章节id:" + currentChapterId + ",上一次在哪个章节下载:" + loadChapterId + "，当前章节页面id:" + currentChapterPager);
        //章节id等于当前页面id的最后一页下载后二章
        if (currentChapterPager == chapterPagerTotal) {
            CatalogInfo catalogInfo = bookDataInfo.getCatalogInfos().get(currentChapterId + 1);
            if (null == catalogInfo) {
                service.startThread(currentChapterId + 1, currentChapterId);
            } else {
                service.startThread(currentChapterId + 2, currentChapterId);
            }
        }
        //章节id大于1并且当前页面id为第一页下载前 二章
        if (currentChapterId > 1 && currentChapterPager == 1) {
            CatalogInfo catalogInfo = bookDataInfo.getCatalogInfos().get(currentChapterId - 1);
            if (null == catalogInfo) {
                service.startThread(currentChapterId - 1, currentChapterId);
            }
            if (currentChapterId > 2) {
                service.startThread(currentChapterId - 2, currentChapterId);
            }
        }
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
        ThreadPool.getInstance().cancelTask("chapter-temp");
    }


    //
//    public void setCurrentItem(int index) {
//        //先强制设定跳转到指定页面
//        try {
//            Field field = viewPager.getClass().getField("mCurItem");//参数mCurItem是系统自带的
//            field.setAccessible(true);
//            field.setInt(viewPager, index);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //然后调用下面的函数刷新数据
//        adapter.notifyDataSetChanged();
//        //再调用setCurrentItem()函数设置一次
//        viewPager.setCurrentItem(index, false);
//    }

}
