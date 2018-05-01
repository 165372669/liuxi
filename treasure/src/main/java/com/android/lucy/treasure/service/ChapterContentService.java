package com.android.lucy.treasure.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;

import com.android.lucy.treasure.bean.BaiduSearchDataInfo;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.ConfigInfo;
import com.android.lucy.treasure.bean.PagerContentInfo;
import com.android.lucy.treasure.pager.ContentPager;
import com.android.lucy.treasure.runnable.chapter.PbtxtChapterContentThread;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.MyMathUtils;
import com.android.lucy.treasure.utils.ThreadPool;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 章节内容后台服务
 */

public class ChapterContentService extends Service {

    private MyBinder mBinder;
    private ChapterContentHandler contentHandler;
    private BaiduSearchDataInfo info;
    private OnChapterContentListener onChapterContentListener;
    private int chapterNameHeight;    //章节名View高
    private int bookNameHeight;       //书名View高
    private int chapterContentWidth;  //章节内容View宽
    private int chapterContentHeight; //章节内容View高
    private int pagerLine;  //一页容纳多少行数
    private Paint mTextPaint;
    private int textWidth; //字体宽度
    private int textHeight; //字体高度
    private int loadChapterId; //下载的章节ID
    private int currentChapterId; //当前的章节ID
    private LinkedList<PagerContentInfo> pagerContentInfos;

    class ChapterContentHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            MyLogcat.myLog("收到数据：" + msg.arg1);
            switch (msg.arg1) {
                case 0:
                    //获取数据失败
                    break;
                case 1://成功下载到章节并添加到页面集合
                    addPagerCotentInfo(msg.arg2);
                    //下载第二章
                    if (loadChapterId == 1)
                        startThread(2, 1);
                    break;
                case 2:
                    //处理好数据通知刷新
                    MyLogcat.myLog("通知更新");
                    onChapterContentListener.setChapterContent();
                    break;
            }
            // onChapterContentListener.setChapterContent();
        }
    }

    /*
    * 该方法在创建service时只调用一次
    * */
    @Override
    public void onCreate() {
        mBinder = new MyBinder();
        contentHandler = new ChapterContentHandler();
        MyLogcat.myLog("Myservice:" + "启动");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLogcat.myLog("Myservice:" + "执行");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyLogcat.myLog("Myservice:" + "绑定");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MyLogcat.myLog("Myservice:" + "解绑");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        MyLogcat.myLog("Myservice:" + "再次绑定");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLogcat.myLog("Myservice:" + "销毁");
    }

    /**
     * 启动下载章节
     *
     * @param number 下载的章节id
     */
    public void startThread(int number, int currentChapterId) {
        this.loadChapterId = number;
        this.currentChapterId = currentChapterId;
        MyLogcat.myLog("调用下载章节线程");
        CatalogInfo catalogInfo = info.getCatalogInfos().get(number - 1);
        String chapterUrl = catalogInfo.getChapterUrl();
        ConfigInfo configInfo = new ConfigInfo(chapterNameHeight, bookNameHeight, chapterContentWidth,
                chapterContentHeight, pagerLine, mTextPaint, textWidth, textHeight);
        MyLogcat.myLog(configInfo.toString());
        ThreadPool.getInstance().submitTask(new PbtxtChapterContentThread(chapterUrl, this.contentHandler, catalogInfo, configInfo));
    }

    public void setOnChapterContentListener(OnChapterContentListener onChapterContentListener) {
        this.onChapterContentListener = onChapterContentListener;
    }

    public interface OnChapterContentListener {
        void setChapterContent();
    }


    public class MyBinder extends Binder {

        public ChapterContentService getService() {
            return ChapterContentService.this;
        }
    }

    /**
     * 获取数据
     *
     * @param info              小说对象
     * @param contentPagerView  章节内容View
     * @param pagerContentInfos 章节页面内容结合
     */
    public void setData(BaiduSearchDataInfo info, ContentPager contentPagerView, LinkedList<PagerContentInfo> pagerContentInfos) {
        this.info = info;
        this.pagerContentInfos = pagerContentInfos;
        textInfoCount();
        myMeasured(contentPagerView);
        MyLogcat.myLog("NameHeight:" + chapterNameHeight + ",chapterContentHeight:" + chapterContentHeight + ",bookNameHeight:" + bookNameHeight);
    }

    /**
     * Viewpager集合添加数据
     */
    public void addPagerCotentInfo(int loadChapterId) {
        MyLogcat.myLog("loadChapterId:" + loadChapterId + ",currentChapterId:" + currentChapterId);
        ArrayList<PagerContentInfo> strs = info.getCatalogInfos().get(loadChapterId - 1).getStrs();
        int size = strs.size() - 1;
        for (int i = 0; i < strs.size(); i++) {
            if (loadChapterId > currentChapterId) {
                pagerContentInfos.addLast(strs.get(i));
            } else {
                pagerContentInfos.addFirst(strs.get(size - i));
            }
        }
        Message message = Message.obtain();
        message.arg1 = 2;
        contentHandler.handleMessage(message);
    }

    /*
 * 计算字体的宽和高
 * */
    public void textInfoCount() {
        mTextPaint = new Paint();
        mTextPaint.setTextSize(MyMathUtils.dip2px(this, 20));
        Rect rect = new Rect();
        //获得18sp大小字体的宽和高。
        mTextPaint.getTextBounds("我", 0, 1, rect);
        textWidth = rect.width();
        textHeight = rect.height();
    }

    /*
     * 手动测量页面的宽和高,根据字体大小计算页面最大行数
     * */
    public void myMeasured(ContentPager contentPagerView) {
        //获取手机屏幕宽高
        Resources resources = this.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int phoneWidth = displayMetrics.widthPixels;
        int phoneHeight = displayMetrics.heightPixels;
        //测量上下的View宽高
        View tv_chapter_name = contentPagerView.getChapterNameView();
        View tv_book_name = contentPagerView.getBookNameView();
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tv_chapter_name.measure(w, h);
        tv_book_name.measure(w, h);
        chapterNameHeight = tv_chapter_name.getMeasuredHeight();
        bookNameHeight = tv_book_name.getMeasuredHeight();
        //计算章节内容宽高
        View tv_chapter_content = contentPagerView.getChapterContentView();
        tv_chapter_content.measure(w, h);
        chapterContentWidth = phoneWidth - MyMathUtils.dip2px(this, 20);
        chapterContentHeight = phoneHeight - (chapterNameHeight + bookNameHeight + MyMathUtils.dip2px(this, 40));
        //一页面最多容纳几行
        pagerLine = pagerLineCount(chapterContentHeight, textHeight);
        //MyLogcat.myLog("NameHeight:" + chapterNameHeight  + ",chapterContentHeight:" + chapterContentHeight + ",bookNameHeight:" + bookNameHeight);
        MyLogcat.myLog("viewWidth:" + chapterContentWidth + ",pagerLine:" + pagerLine);
    }

    /*
* 计算一个页面根据字体高度能容纳多少行.
* 页面行数等于控件高度/(字体高度*2)
* */
    public int pagerLineCount(int chapterTextHeight, int textHeight) {
        //控件高度,1750,18sp字体能容纳21行。
        return chapterTextHeight / (textHeight * 2);
    }

}
