package com.android.lucy.treasure.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 小说对象
 */

public class BookInfo extends DataSupport implements Serializable {
    private int id;
    private String bookName; //小说名称
    private String author;   //作者名称
    private String sourceName;    //来源选择
    private int newChapterId; //最新章节id
    private int readChapterid;//已读章节id
    private int readChapterPager;//已读章节页面
    private int closeTime;   //关闭时间
    private int chapterTotal; //章节总数
    private int unreadSeveral;//未读章节数
    private ArrayList<SourceInfo> sourceInfos;//来源集合

    private static final long serialVersionUID = 1;

    public BookInfo() {
    }

    public BookInfo(String bookName, String author) {
        this(bookName, author, null, 0, 0, 0, 0,
                0, 0);
    }

    public BookInfo(String bookName, String author, String sourceName, int newChapterId, int readChapterid,
                    int readChapterPager, int closeTime, int chapterTotal, int unreadSeveral) {
        this.bookName = bookName;
        this.author = author;
        this.sourceName = sourceName;
        this.newChapterId = newChapterId;
        this.readChapterid = readChapterid;
        this.readChapterPager = readChapterPager;
        this.closeTime = closeTime;
        this.chapterTotal = chapterTotal;
        this.unreadSeveral = unreadSeveral;
        sourceInfos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public int getReadChapterPager() {
        return readChapterPager;
    }

    public void setReadChapterPager(int readChapterPager) {
        this.readChapterPager = readChapterPager;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public int getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(int closeTime) {
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

    public ArrayList<SourceInfo> getSourceInfos() {
        return sourceInfos;
    }

    public List<SourceInfo> getSourceInfos(int id) {
        return DataSupport.where("bookinfo_id = ?", String.valueOf(id)).find(SourceInfo.class);
    }

    public void setSourceInfos(ArrayList<SourceInfo> sourceInfos) {
        this.sourceInfos = sourceInfos;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", sourceName=" + sourceName +
                ", newChapterId=" + newChapterId +
                ", readChapterid=" + readChapterid +
                ", closeTime=" + closeTime +
                ", chapterTotal=" + chapterTotal +
                ", unreadSeveral=" + unreadSeveral +
                ", sourceInfos=" + sourceInfos +
                '}';
    }
}
