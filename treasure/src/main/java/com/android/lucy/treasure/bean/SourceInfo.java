package com.android.lucy.treasure.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 小说来源对象
 */

public class SourceInfo extends DataSupport implements Serializable {
    private int id;
    private BookInfo bookInfo;
    private String sourceBaiduUrl; //来源百度网址
    private String sourceName; //来源名称
    private String sourceUrl;  //来源小说网址
    private String webType;    //网站类型
    private ArrayList<CatalogInfo> catalogInfos;//章节集合

    private static final long serialVersionUID = 2;


    public SourceInfo(String sourceName,String sourceBaiduUrl) {
        this.sourceName = sourceName;
        this.sourceBaiduUrl = sourceBaiduUrl;
        catalogInfos = new ArrayList<>();
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

    public ArrayList<CatalogInfo> getCatalogInfos() {
        return catalogInfos;
    }

    public List<CatalogInfo> getCatalogInfos(int id) {
        return DataSupport.where("sourceinfo_id = ?", String.valueOf(id)).find(CatalogInfo.class);
    }

    public void setCatalogInfos(ArrayList<CatalogInfo> catalogInfos) {
        this.catalogInfos = catalogInfos;
    }


    @Override
    public boolean equals(Object obj) {
        SourceInfo sourceInfo = (SourceInfo) obj;
        return sourceName.equals(sourceInfo.getSourceName());

    }

    @Override
    public String toString() {
        return "SourceInfo{" +
                "sourceUrl='" + sourceUrl + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", catalogInfos=" + catalogInfos +
                '}';
    }
}
