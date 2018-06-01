package com.android.lucy.treasure.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 小说对象
 */

public class BookDataInfo extends DataSupport implements Serializable {
    private int id;
    private String bookName; //小说名称
    private String author;   //作者名称
    private int sourceid;    //来源选择
    private int newChapterId; //最新章节id
    private int readChapterid;//已读章节id
    private int closeTime;   //关闭时间
    private int chapterTotal; //章节总数
    private int unreadSeveral;//未读章节数
    private ArrayList<SourceDataInfo> sourceDataInfos;//来源集合

    private static final long serialVersionUID = 1;

    public BookDataInfo() {
    }

    public BookDataInfo(String bookName, String author) {
        this(bookName, author, 0, 0, 0, 0, 0, 0);
    }

    public BookDataInfo(String bookName, String author, int sourceid, int newChapterId, int readChapterid, int closeTime, int chapterTotal, int unreadSeveral) {
        this.bookName = bookName;
        this.author = author;
        this.sourceid = sourceid;
        this.newChapterId = newChapterId;
        this.readChapterid = readChapterid;
        this.closeTime = closeTime;
        this.chapterTotal = chapterTotal;
        this.unreadSeveral = unreadSeveral;
        sourceDataInfos = new ArrayList<>();
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

    public int getSourceid() {
        return sourceid;
    }

    public void setSourceid(int sourceid) {
        this.sourceid = sourceid;
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

    public ArrayList<SourceDataInfo> getSourceDataInfos() {
        return sourceDataInfos;
    }

    public void setSourceDataInfos(ArrayList<SourceDataInfo> sourceDataInfos) {
        this.sourceDataInfos = sourceDataInfos;
    }

    @Override
    public String toString() {
        return "BookDataInfo{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", sourceid=" + sourceid +
                ", newChapterId=" + newChapterId +
                ", readChapterid=" + readChapterid +
                ", closeTime=" + closeTime +
                ", chapterTotal=" + chapterTotal +
                ", unreadSeveral=" + unreadSeveral +
                ", sourceDataInfos=" + sourceDataInfos +
                '}';
    }
}
