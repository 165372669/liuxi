package com.android.lucy.treasure.bean;

import java.util.List;

/**
 * 页面内容
 */

public class PagerContentInfo {
    private List<TextInfo> textInfos;  //页面内容数据
    private int currentPager; //页面Id

    public PagerContentInfo(List<TextInfo> textInfos, int currentPager) {
        this.textInfos = textInfos;
        this.currentPager = currentPager;
    }

    public List<TextInfo> getTextInfos() {
        return textInfos;
    }

    public void setTextInfos(List<TextInfo> textInfos) {
        this.textInfos = textInfos;
    }

    public int getCurrentPager() {
        return currentPager;
    }

    public void setCurrentPager(int currentPager) {
        this.currentPager = currentPager;
    }

}
