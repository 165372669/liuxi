package com.android.lucy.treasure.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 小说来源对象
 */

public class SourceDataInfo extends DataSupport implements Serializable {
    private BookDataInfo bookDataInfo;
    private String sourceUrl; //来源网址
    private String sourceName; //来源名称
    private ArrayList<CatalogInfo> catalogInfos;//章节集合

    private static final long serialVersionUID = 2;


    public SourceDataInfo(String sourceName, String sourceUrl) {
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        catalogInfos = new ArrayList<>();
    }

    public BookDataInfo getBookDataInfo() {
        return bookDataInfo;
    }

    public void setBookDataInfo(BookDataInfo bookDataInfo) {
        this.bookDataInfo = bookDataInfo;
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

    public void setCatalogInfos(ArrayList<CatalogInfo> catalogInfos) {
        this.catalogInfos = catalogInfos;
    }

    @Override
    public boolean equals(Object obj) {
        SourceDataInfo sourceDataInfo = (SourceDataInfo) obj;
        return sourceName.equals(sourceDataInfo.getSourceName());

    }

    @Override
    public String toString() {
        return "SourceDataInfo{" +
                "sourceUrl='" + sourceUrl + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", catalogInfos=" + catalogInfos +
                '}';
    }
}
