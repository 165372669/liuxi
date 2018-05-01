package com.android.lucy.treasure.bean;

import java.util.List;

/**
 * 页面内容
 */

public class PagerContentInfo {
    private String chapterName;  //章节名
    private int chapterId;  //章节Id
    private List<TextInfo> textInfos;  //页面内容数据
    private int currentPager; //页面Id
    private int chapterPagerToatal; //章节总页面

    public PagerContentInfo(String chapterName, int chapterId, List<TextInfo> textInfos, int currentPager) {
        this(chapterName, chapterId, textInfos, currentPager, 0);
    }

    public PagerContentInfo(String chapterName, int chapterId, List<TextInfo> textInfos, int currentPager, int chapterPagerToatal) {
        this.chapterName = chapterName;
        this.chapterId = chapterId;
        this.textInfos = textInfos;
        this.currentPager = currentPager;
        this.chapterPagerToatal = chapterPagerToatal;
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

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getChapterPagerToatal() {
        return chapterPagerToatal;
    }

    public void setChapterPagerToatal(int chapterPagerToatal) {
        this.chapterPagerToatal = chapterPagerToatal;
    }
}
