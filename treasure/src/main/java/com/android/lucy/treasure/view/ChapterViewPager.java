package com.android.lucy.treasure.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.lucy.treasure.activity.BookContentActivity;
import com.android.lucy.treasure.adapter.BookContentPagerAdapter;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.PagerContentInfo;
import com.android.lucy.treasure.utils.MyLogcat;

import java.util.ArrayList;

/**
 * 章节内容ViewPager，实现点击翻页。
 */

public class ChapterViewPager extends ViewPager {
    private float startX;
    private long downTime;
    //是否往右滑动
    private boolean isRightSlide = false;
    //是否往右滑动
    private boolean isLeftSlide = false;
    private float slideX;
    private int currentPagerPosition;
    private int chapterTotal;
    private float startY;
    private View titleBar;
    private View setings;
    private Activity activity;
    private ArrayList<CatalogInfo> catalogInfos;

    public ChapterViewPager(Context context) {
        super(context);
    }

    public ChapterViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float motionValue;
        int disparityWidth = getWidth() / 7;
        int disparityHeight = getHeight() / 7;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downTime = ev.getDownTime();
                startX = ev.getX();
                startY = ev.getY();
                slideX = ev.getX();
                popUpMenu(disparityWidth, disparityHeight);
                onClickChangePager();
                // MyLogcat.myLog("按下：" + ev.getX() + ",Id:" + posotion + ",width:" + getWidth());
                break;
            case MotionEvent.ACTION_MOVE:
                motionValue = slideX - ev.getX();
                //禁止往右或往左滑动
                if (motionValue > 0 && isRightSlide)
                    return false;
                if (motionValue < 0 && isLeftSlide)
                    return false;
                slideX = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
                int x = (int) Math.abs(ev.getX() - startX);
                int y = (int) Math.abs(ev.getY() - startY);
                long time = ev.getEventTime() - downTime;
                if (x < 30 && time < 200) {
                    if (startX > getWidth() / 2 + disparityWidth) {
                        if (isRightSlide)
                            return false;
                        setCurrentItem(getCurrentItem() + 1, false);
                    } else if (startX < getWidth() / 2 - disparityWidth) {
                        if (isLeftSlide)
                            return false;
                        setCurrentItem(getCurrentItem() - 1, false);
                    }
                    //MyLogcat.myLog("onTouchEvent::"+"currentPagerPosition：" + currentPagerPosition + ",tempPosition:" + tempPosition);
                }
                break;
            default:
                return super.onTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    /**
     * //弹出标题栏和设置栏
     *
     * @param disparityWidth  宽间距
     * @param disparityHeight 高间距
     */
    public void popUpMenu(float disparityWidth, float disparityHeight) {
        if (startX > (getWidth() / 2 - disparityWidth) && startX < (getWidth() / 2 + disparityWidth)
                && startY > (getHeight() / 2 - disparityHeight) && startY < (getHeight() / 2 + disparityHeight)) {
            ((BookContentActivity) activity).flagsVisibility(true);
            if (titleBar.getVisibility() == VISIBLE) {
                ((BookContentActivity) activity).flagsVisibility(false);
            }
            titleBar.setVisibility(titleBar.getVisibility() == INVISIBLE ? VISIBLE : INVISIBLE);
            setings.setVisibility(setings.getVisibility() == INVISIBLE ? VISIBLE : INVISIBLE);
        } else {
            ((BookContentActivity) activity).flagsVisibility(false);
            titleBar.setVisibility(INVISIBLE);
            setings.setVisibility(INVISIBLE);
        }
    }

    public void setOtherView(View setings, View titleBar) {
        this.titleBar = titleBar;
        this.setings = setings;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setChapterDatas(ArrayList<CatalogInfo> catalogInfos) {
        this.catalogInfos = catalogInfos;
    }

    public void setChapterTotal(int chapterTotal) {
        this.chapterTotal = chapterTotal;
    }

    public void setCurrentPagerPosition(int currentPagerPosition) {
        this.currentPagerPosition = currentPagerPosition;
    }


    public void onClickChangePager() {
        if (null != catalogInfos && catalogInfos.size() > 0) {
            BookContentPagerAdapter adapter = ((BookContentPagerAdapter) getAdapter());
            int currentChapterId = adapter.getCurrentChapterId();
            CatalogInfo catalogInfo = catalogInfos.get(currentChapterId);
            int chapterId = catalogInfo.getChapterId();
            ArrayList<PagerContentInfo> pagerContentInfos = catalogInfo.getStrs();
            if (null != pagerContentInfos && pagerContentInfos.size() > 0) {
                int pagerPosition = adapter.getPagerPosition();
                if (pagerPosition >= pagerContentInfos.size()) {
                    pagerPosition = pagerContentInfos.size() - 1;
                }
                int currentPager = catalogInfo.getStrs().get(pagerPosition).getCurrentPager();
                int chapterPagerTotal = catalogInfo.getChapterPagerToatal();
                //如果是第一章并且是章节第一页不能往左滑动
                isLeftSlide = chapterId == 1 && currentPager == 1;
                //如果是最后章节并且是章节的最后一页不能往右滑动
                isRightSlide = (chapterId == chapterTotal && pagerPosition == chapterPagerTotal - 1);
            }
        }
    }


}
