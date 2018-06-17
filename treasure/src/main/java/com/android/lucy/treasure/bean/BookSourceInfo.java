package com.android.lucy.treasure.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 小说来源对象
 */

public class BookSourceInfo extends DataSupport implements Serializable {
    private int id;
    private BookInfo bookInfo;
    private String sourceBaiduUrl; //来源百度网址
    private String sourceName; //来源名称
    private String sourceUrl;  //来源小说网址
    private String webType;    //网站类型

    private static final long serialVersionUID = 2;


    public BookSourceInfo(String sourceName, String sourceBaiduUrl) {
        this(sourceBaiduUrl, sourceName, null, null);
    }


    public BookSourceInfo(String sourceBaiduUrl, String sourceName, String sourceUrl, String webType) {
        this(null, sourceBaiduUrl, sourceName, sourceUrl, webType);
    }

    public BookSourceInfo(BookInfo bookInfo, String sourceBaiduUrl, String sourceName, String sourceUrl, String webType) {
        this.bookInfo = bookInfo;
        this.sourceBaiduUrl = sourceBaiduUrl;
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.webType = webType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BookInfo getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(BookInfo bookInfo) {
        this.bookInfo = bookInfo;
    }

    public String getSourceBaiduUrl() {
        return sourceBaiduUrl;
    }

    public void setSourceBaiduUrl(String sourceBaiduUrl) {
        this.sourceBaiduUrl = sourceBaiduUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getWebType() {
        return webType;
    }

    public void setWebType(String webType) {
        this.webType = webType;
    }


    @Override
    public boolean equals(Object obj) {
        BookSourceInfo bookSourceInfo = (BookSourceInfo) obj;
        return sourceName.equals(bookSourceInfo.getSourceName());

    }

    @Override
    public String toString() {
        return "BookSourceInfo{" +
                "id=" + id +
                ", bookInfo=" + bookInfo +
                ", sourceBaiduUrl='" + sourceBaiduUrl + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", webType='" + webType + '\'' +
                '}';
    }
}
