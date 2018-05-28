package com.android.lucy.treasure.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;

/**
 * 小说对象
 */

public class BookDataInfo extends DataSupport implements Serializable {

    private String bookName; //小说名称
    private String author;   //作者名称
    private String sourceUrl; //来源网址
    private String sourceName; //来源名称
    private int newChapterId; //最新章节id
    private int readChapterid;//已读章节id
    private Date closeTime;   //关闭时间
    private int chapterTotal; //章节总数
    private int unreadSeveral;//未读章节数
    private ArrayList<CatalogInfo> catalogInfos;//章节集合

    private static final long serialVersionUID = 1;

    public BookDataInfo(String bookName, String sourceUrl, String sourceName, String author) {
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

    public int getNewChapterId() {
        return newChapterId;
    }

    public void setNewChapterId(int newChapterId) {
        this.newChapterId = newChapterId;
    }

    public int getReadChapterid() {
        return readChapterid;
    }

    public void setReadChapterid(int readChapterid) {
        this.readChapterid = readChapterid;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public int getChapterTotal() {
        return chapterTotal;
    }

    public void setChapterTotal(int chapterTotal) {
        this.chapterTotal = chapterTotal;
    }

    public int getUnreadSeveral() {
        return unreadSeveral;
    }

    public void setUnreadSeveral(int unreadSeveral) {
        this.unreadSeveral = unreadSeveral;
    }

    @Override
    public boolean equals(Object obj) {
        BookDataInfo bookDataInfo = (BookDataInfo) obj;
        return this.sourceName.equals(bookDataInfo.getSourceName());
    }

}
