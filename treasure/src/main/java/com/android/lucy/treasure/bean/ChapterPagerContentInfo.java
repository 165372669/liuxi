package com.android.lucy.treasure.bean;

import java.util.ArrayList;

/**
 * 页面内容对象
 */

public class ChapterPagerContentInfo {
    private ArrayList<PagerContentTextInfo> pagerContentTextInfos;  //页面内容数据
    private int currentPager; //页面Id

    public ChapterPagerContentInfo(ArrayList<PagerContentTextInfo> pagerContentTextInfos, int currentPager) {
        this.pagerContentTextInfos = pagerContentTextInfos;
        this.currentPager = currentPager;
    }

    public ArrayList<PagerContentTextInfo> getPagerContentTextInfos() {
        return pagerContentTextInfos;
    }

    public void setPagerContentTextInfos(ArrayList<PagerContentTextInfo> pagerContentTextInfos) {
        this.pagerContentTextInfos = pagerContentTextInfos;
    }

    public int getCurrentPager() {
        return currentPager;
    }

    public void setCurrentPager(int currentPager) {
        this.currentPager = currentPager;
    }

    @Override
    public String toString() {
        return "ChapterPagerContentInfo{" +
                "pagerContentTextInfos=" + pagerContentTextInfos +
                ", currentPager=" + currentPager +
                '}';
    }
}
