package com.android.lucy.treasure.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 百度搜索数据
 */

public class BaiduSearchDataInfo implements Serializable {

    private String bookName; //书名
    private String author;   //作者
    private String sourceUrl; //书页url
    private String sourceName; //来源网站名
    private ArrayList<CatalogInfo> catalogInfos;//章节集合

    private static final long serialVersionUID = 1;

    public BaiduSearchDataInfo(String bookName, String sourceUrl, String sourceName, String author) {
        this.bookName = bookName;
        this.sourceUrl = sourceUrl;
        this.sourceName = sourceName;
        this.author = author;
        catalogInfos = new ArrayList<>();
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
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

    public ArrayList<CatalogInfo> getCatalogInfos() {
        return catalogInfos;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void addCatalogInfo(CatalogInfo catalogInfo) {
        catalogInfos.add(catalogInfo);
    }

    @Override
    public boolean equals(Object obj) {
        BaiduSearchDataInfo baiduSearchDataInfo = (BaiduSearchDataInfo) obj;
        return this.sourceName.equals(baiduSearchDataInfo.getSourceName());
    }

}
