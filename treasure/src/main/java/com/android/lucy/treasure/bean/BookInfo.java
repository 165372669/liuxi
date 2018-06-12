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
    private String type;//小说类型
    private String updateTime;//最后更新时间
    private String newChapterName; //最新章节名
    private String readChapterName;//已读章节名
    private String sourceName;    //来源选择
    private String zhuishuUrl;//追书网页url
    private String wordCountTotal; //总字数
    private String imgUrl;//封面链接
    private int sourceIndex;//来源在集合里的位置
    private int newChapterId; //最新章节id
    private int readChapterid;//已读章节id
    private int readChapterPager;//已读章节页面
    private long closeTime;   //关闭时间
    private int chapterTotal; //章节总数
    private int unreadSeveral;//未读章节数
    private ArrayList<SourceInfo> sourceInfos;//来源集合
    private ArrayList<CatalogInfo> catalogInfos; //章节集合

    private static final long serialVersionUID = 1;

    public BookInfo() {
    }

    public BookInfo(String bookName) {
        this(bookName, null, null, 0, 0, 0, 0,
                0, 0);
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
        catalogInfos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getNewChapterName() {
        return newChapterName;
    }

    public void setNewChapterName(String newChapterName) {
        this.newChapterName = newChapterName;
    }

    public String getReadChapterName() {
        return readChapterName;
    }

    public void setReadChapterName(String readChapterName) {
        this.readChapterName = readChapterName;
    }

    public String getWordCountTotal() {
        return wordCountTotal;
    }

    public void setWordCountTotal(String wordCountTotal) {
        this.wordCountTotal = wordCountTotal;
    }

    public String getZhuishuUrl() {
        return zhuishuUrl;
    }

    public void setZhuishuUrl(String zhuishuUrl) {
        this.zhuishuUrl = zhuishuUrl;
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

    public int getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
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

    public long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(long closeTime) {
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

    public ArrayList<CatalogInfo> getCatalogInfos() {
        return catalogInfos;
    }

    public void setCatalogInfos(ArrayList<CatalogInfo> catalogInfos) {
        this.catalogInfos = catalogInfos;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", type='" + type + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", newChapterName='" + newChapterName + '\'' +
                ", readChapterName='" + readChapterName + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", zhuishuUrl='" + zhuishuUrl + '\'' +
                ", wordCountTotal='" + wordCountTotal + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", sourceIndex=" + sourceIndex +
                ", newChapterId=" + newChapterId +
                ", readChapterid=" + readChapterid +
                ", readChapterPager=" + readChapterPager +
                ", closeTime=" + closeTime +
                ", chapterTotal=" + chapterTotal +
                ", unreadSeveral=" + unreadSeveral +
                ", sourceInfos=" + sourceInfos +
                ", catalogInfos=" + catalogInfos +
                '}';
    }
}
