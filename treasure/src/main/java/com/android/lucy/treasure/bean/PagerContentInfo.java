package com.android.lucy.treasure.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面内容
 */

public class PagerContentInfo {
    private ArrayList<TextInfo> textInfos;  //页面内容数据
    private int currentPager; //页面Id

    public PagerContentInfo(ArrayList<TextInfo> textInfos, int currentPager) {
        this.textInfos = textInfos;
        this.currentPager = currentPager;
    }

    public ArrayList<TextInfo> getTextInfos() {
        return textInfos;
    }

    public void setTextInfos(ArrayList<TextInfo> textInfos) {
        this.textInfos = textInfos;
    }

    public int getCurrentPager() {
        return currentPager;
    }

    public void setCurrentPager(int currentPager) {
        this.currentPager = currentPager;
    }

    @Override
    public String toString() {
        return "PagerContentInfo{" +
                "textInfos=" + textInfos +
                ", currentPager=" + currentPager +
                '}';
    }
}
