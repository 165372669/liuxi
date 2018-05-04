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
    private int chapterPagerToatal; //章节总页面
    private ArrayList<PagerContentInfo> strs; //页面内容集合

    private static final long serialVersionUID = 2;

    public CatalogInfo(int chapterId, String chapterUrl, String chapterName) {
        this(chapterId, chapterUrl, chapterName, null);
    }

    public CatalogInfo(int chapterId, String chapterUrl, String chapterName, ArrayList<PagerContentInfo> strs) {
        this.chapterId = chapterId;
        this.chapterUrl = chapterUrl;
        this.chapterName = chapterName;
        this.strs = strs;
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
        return strs;
    }

    public void setStrs(ArrayList<PagerContentInfo> strs) {
        this.strs = strs;
    }


    @Override
    public String toString() {
        return "CatalogInfo{" +
                "chapterId=" + chapterId +
                ", chapterUrl='" + chapterUrl + '\'' +
                ", chapterName='" + chapterName + '\'' +
                ", strs=" + strs.toString() +
                '}';
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
    public int compareTo(@NonNull Object o) {
        CatalogInfo catalogInfo = (CatalogInfo) o;
        if (this.chapterId > catalogInfo.chapterId)
            return 1;
        if (this.chapterId == catalogInfo.chapterId)
            return this.chapterName.compareTo(catalogInfo.chapterName);
        return 0;
    }

}

