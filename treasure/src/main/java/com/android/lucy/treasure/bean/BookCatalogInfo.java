package com.android.lucy.treasure.bean;

import android.support.annotation.NonNull;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 章节详细信息
 */

public class BookCatalogInfo extends DataSupport implements Comparable, Serializable {
    private BookInfo bookInfo;
    private int id;
    private int chapterId;  //章节id
    private String chapterUrl; //章节网址
    private String chapterName; //章节名
    private int chapterPagerToatal; //章节总页面数
    private ArrayList<ChapterPagerContentInfo> chapterPagerContentInfos; //页面内容集合

    private static final long serialVersionUID = 3;

    public BookCatalogInfo(BookInfo bookInfo, int chapterId, String chapterUrl, String chapterName, int chapterPagerToatal) {
        this.bookInfo = bookInfo;
        this.chapterId = chapterId;
        this.chapterUrl = chapterUrl;
        this.chapterName = chapterName;
        this.chapterPagerToatal = chapterPagerToatal;
    }

    public BookInfo getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(BookInfo bookInfo) {
        this.bookInfo = bookInfo;
    }

    public ArrayList<ChapterPagerContentInfo> getChapterPagerContentInfos() {
        return chapterPagerContentInfos;
    }

    public void setChapterPagerContentInfos(ArrayList<ChapterPagerContentInfo> chapterPagerContentInfos) {
        this.chapterPagerContentInfos = chapterPagerContentInfos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ArrayList<ChapterPagerContentInfo> getStrs() {
        return chapterPagerContentInfos;
    }

    public void setStrs(ArrayList<ChapterPagerContentInfo> strs) {
        this.chapterPagerContentInfos = strs;
    }

    public void clearStrs() {
        chapterPagerContentInfos.clear();
        chapterPagerContentInfos = null;
    }


    @Override
    public int hashCode() {
        return chapterName.hashCode() + chapterId * 9;
    }

    @Override
    public boolean equals(Object obj) {
        BookCatalogInfo bookCatalogInfo = (BookCatalogInfo) obj;
        return chapterName.equals(bookCatalogInfo.chapterName) && chapterId == bookCatalogInfo.chapterId;
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        BookCatalogInfo bookCatalogInfo = (BookCatalogInfo) obj;
        if (this.chapterId > bookCatalogInfo.chapterId)
            return 1;
        if (this.chapterId == bookCatalogInfo.chapterId)
            return this.chapterName.compareTo(bookCatalogInfo.chapterName);
        return 0;
    }

}

