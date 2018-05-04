package com.android.lucy.treasure.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;

import com.android.lucy.treasure.activity.BookContentActivity;
import com.android.lucy.treasure.bean.BaiduSearchDataInfo;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.ConfigInfo;
import com.android.lucy.treasure.bean.PagerContentInfo;
import com.android.lucy.treasure.pager.ContentPager;
import com.android.lucy.treasure.runnable.chapter.PbtxtChapterContentThread;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.MyMathUtils;
import com.android.lucy.treasure.utils.ThreadPool;
import com.android.lucy.treasure.view.CircleProgress;

import java.util.ArrayList;

/**
 * 章节内容后台服务
 */

public class ChapterContentService extends Service {

    private MyBinder mBinder;
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
    private BookContentActivity.ChapterContentHandler chapterContentHandler;
    private CircleProgress cv_chapter_progress;


    /*
    * 该方法在创建service时只调用一次
    * */
    @Override
    public void onCreate() {
        mBinder = new MyBinder();
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
     * 带进度的下载章节
     *
     * @param ChapterID 下载的章节id
     */
    public void startThreadProgress(int ChapterID) {
        this.loadChapterId = ChapterID;
        MyLogcat.myLog("调用下载章节线程：" + "章节Id:" + ChapterID);
        CatalogInfo catalogInfo = info.getCatalogInfos().get(ChapterID - 1);
        ArrayList<PagerContentInfo> pagerContentInfos = catalogInfo.getStrs();
        if (null == pagerContentInfos) {
            pagerContentInfos = new ArrayList<>();
            catalogInfo.setStrs(pagerContentInfos);
            String chapterUrl = catalogInfo.getChapterUrl();
            ConfigInfo configInfo = new ConfigInfo(chapterNameHeight, bookNameHeight, chapterContentWidth,
                    chapterContentHeight, pagerLine, mTextPaint, textWidth, textHeight);
            MyLogcat.myLog("排版需要的配置:" + configInfo.toString());
            cv_chapter_progress.setProgress(10);
            ThreadPool.getInstance().submitTask(new PbtxtChapterContentThread(chapterUrl, chapterContentHandler, catalogInfo, configInfo,
                    cv_chapter_progress));
        }
    }

    public void setOnChapterContentListener(OnChapterContentListener onChapterContentListener) {
        this.onChapterContentListener = onChapterContentListener;
    }

    public void setView(CircleProgress cv_chapter_progress) {
        this.cv_chapter_progress = cv_chapter_progress;
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
     * @param info                  小说对象
     * @param contentPagerView      章节内容View
     * @param chapterContentHandler
     */
    public void setData(BaiduSearchDataInfo info, ContentPager contentPagerView, BookContentActivity.ChapterContentHandler chapterContentHandler) {
        this.info = info;
        this.chapterContentHandler = chapterContentHandler;
        textInfoCount();
        myMeasured(contentPagerView);
        MyLogcat.myLog("NameHeight:" + chapterNameHeight + ",chapterContentHeight:" + chapterContentHeight + ",bookNameHeight:" + bookNameHeight);
    }

    /**
     * Viewpager集合添加数据
     */
    public void addPagerCotentInfo(int loadChapterId) {
//        if (loadChapterId > currentChapterId) {
//            int number = loadChapterId - 4;
//            if (number > 0) {
//                ArrayList<PagerContentInfo> pagerContentInfos = info.getCatalogInfos().get(number).getStrs();
//                if (null != pagerContentInfos) {
//                    pagerContentInfos.clear();
//                    MyLogcat.myLog("删除章节：" + number);
//                }
//            }
//        } else {
//            int number = loadChapterId + 4;
//            if (number < info.getCatalogInfos().size() - 1) {
//                ArrayList<PagerContentInfo> pagerContentInfos = info.getCatalogInfos().get(number).getStrs();
//                if (null != pagerContentInfos)
//                    pagerContentInfos.clear();
//            }
//        }
    }


    /*
     *计算字体的宽和高
     */
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
