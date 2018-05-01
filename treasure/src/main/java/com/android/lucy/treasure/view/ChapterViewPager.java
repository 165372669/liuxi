package com.android.lucy.treasure.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.android.lucy.treasure.adapter.BookContentPagerAdapter;
import com.android.lucy.treasure.bean.PagerContentInfo;
import com.android.lucy.treasure.utils.MyLogcat;

import java.util.List;

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
    private List<PagerContentInfo> pagerContentInfos;
    private int chapterTotal;

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
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downTime = ev.getDownTime();
                startX = ev.getX();
                slideX = ev.getX();
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
                long time = ev.getEventTime() - downTime;
                if (x < 30 && time < 200) {
                    if (startX > getWidth() / 2 + getWidth() / 10) {
                        if (isRightSlide)
                            return false;
                        setCurrentItem(getCurrentItem() + 1, false);
                    } else if (startX < getWidth() / 2 - getWidth() / 10) {
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


    public void setChapterDatas(List<PagerContentInfo> pagerContentInfos) {
        this.pagerContentInfos = pagerContentInfos;
    }

    public void setChapterTotal(int chapterTotal) {
        this.chapterTotal = chapterTotal;
    }

    public void setCurrentPagerPosition(int currentPagerPosition) {
        this.currentPagerPosition = currentPagerPosition;
    }


    public void onClickChangePager() {
        if (null != pagerContentInfos && pagerContentInfos.size() > 0) {
            PagerContentInfo pagerContentInfo = pagerContentInfos.get(((BookContentPagerAdapter) getAdapter()).getPagerPosition());
            int chapterId = pagerContentInfo.getChapterId();
            int currentPager = pagerContentInfo.getCurrentPager();
            int chapterPagerTotal = pagerContentInfo.getChapterPagerToatal();
            //如果是第一章并且是章节第一页不能往左滑动
            isLeftSlide = chapterId == 1 && currentPager == 1;
            //如果是最后章节并且是章节的最后一页不能往右滑动,或者是集合的最后一个数据
            //MyLogcat.myLog("viewPager----" + "currentPagerPosition：" + currentPagerPosition);
            isRightSlide = (chapterId == chapterTotal && currentPager == chapterPagerTotal) || (currentPagerPosition == pagerContentInfos.size() - 1);
        }

    }


}
