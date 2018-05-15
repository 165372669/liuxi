package com.android.lucy.treasure.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 章节详细信息
 */

public class CatalogInfo implements Comparable, Serializable {

    private int chapterId;  //章节id
    private String chapterUrl; //章节网址
    private String chapterName; //章节名
    private int chapterPagerToatal; //章节总页面数
    private ArrayList<PagerContentInfo> pagerContentInfos; //页面内容集合

    private static final long serialVersionUID = 2;


    public CatalogInfo(String chapterUrl, String chapterName, int chapterId) {
        this(chapterUrl, chapterName, chapterId, null);
    }

    public CatalogInfo(String chapterUrl, String chapterName, int chapterId, ArrayList<PagerContentInfo> pagerContentInfos) {
        this.chapterId = chapterId;
        this.chapterUrl = chapterUrl;
        this.chapterName = chapterName;
        this.pagerContentInfos = pagerContentInfos;
    }


    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getChapterPagerToatal() {
        return chapterPagerToatal;
    }

    public void setChapterPagerToatal(int chapterPagerToatal) {
        this.chapterPagerToatal = chapterPagerToatal;
    }

    public ArrayList<PagerContentInfo> getStrs() {
        return pagerContentInfos;
    }

    public void setStrs(ArrayList<PagerContentInfo> strs) {
        this.pagerContentInfos = strs;
    }

    public void clearStrs() {
        pagerContentInfos.clear();
        pagerContentInfos = null;
    }


    @Override
    public int hashCode() {
        return chapterName.hashCode() + chapterId * 9;
    }

    @Override
    public boolean equals(Object obj) {
        CatalogInfo catalogInfo = (CatalogInfo) obj;
        return chapterName.equals(catalogInfo.chapterName) && chapterId == catalogInfo.chapterId;
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        CatalogInfo catalogInfo = (CatalogInfo) obj;
        if (this.chapterId > catalogInfo.chapterId)
            return 1;
        if (this.chapterId == catalogInfo.chapterId)
            return this.chapterName.compareTo(catalogInfo.chapterName);
        return 0;
    }

}

