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
    private String state;//状态
    private String bookCount; //总字数
    private String newChapterName; //最新章节名
    private String synopsis;//书籍简介
    private String readChapterName;//已读章节名
    private String sourceName;    //来源选择
    private String datailsUrl;//详情网页url
    private String imgUrl;//封面链接
    private byte[] image; //封面
    private int sourceIndex;//来源在集合里的位置
    private int newChapterId; //最新章节id
    private int readChapterid;//已读章节id
    private int readChapterPager;//已读章节页面
    private long closeTime;   //关闭时间
    private int chapterTotal; //章节总数
    private int unreadSeveral;//未读章节数
    private ArrayList<BookSourceInfo> bookSourceInfos;//来源集合
    private ArrayList<BookCatalogInfo> bookCatalogInfos; //章节集合
    private static final long serialVersionUID = 1;

    public BookInfo() {

    }

    public BookInfo(String bookName, String author, String imgUrl, String type, String synopsis, String newChapterName, byte[] image, String datailsUrl) {
        this(bookName, author, null, 0, 0, 0, 0,
                0, 0, imgUrl, image, type, synopsis, newChapterName, datailsUrl);
    }


    public BookInfo(String bookName, String author, String sourceName, int newChapterId, int readChapterid,
                    int readChapterPager, int closeTime, int chapterTotal, int unreadSeveral, String imgUrl,
                    byte[] image, String type, String synopsis, String newChapterName, String datailsUrl) {
        this.bookName = bookName;
        this.author = author;
        this.sourceName = sourceName;
        this.newChapterId = newChapterId;
        this.readChapterid = readChapterid;
        this.readChapterPager = readChapterPager;
        this.closeTime = closeTime;
        this.chapterTotal = chapterTotal;
        this.unreadSeveral = unreadSeveral;
        this.image = image;
        this.imgUrl = imgUrl;
        this.type = type;
        this.synopsis = synopsis;
        this.newChapterName = newChapterName;
        this.datailsUrl = datailsUrl;
        bookSourceInfos = new ArrayList<>();
        bookCatalogInfos = new ArrayList<>();
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



    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    public String getBookCount() {
        return bookCount;
    }

    public void setBookCount(String bookCount) {
        this.bookCount = bookCount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getDatailsUrl() {
        return datailsUrl;
    }

    public void setDatailsUrl(String datailsUrl) {
        this.datailsUrl = datailsUrl;
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

    public ArrayList<BookSourceInfo> getBookSourceInfos() {
        return bookSourceInfos;
    }

    public List<BookSourceInfo> getSourceInfos(int id) {
        return DataSupport.where("bookinfo_id = ?", String.valueOf(id)).find(BookSourceInfo.class);
    }

    public void setBookSourceInfos(ArrayList<BookSourceInfo> bookSourceInfos) {
        this.bookSourceInfos = bookSourceInfos;
    }

    public ArrayList<BookCatalogInfo> getBookCatalogInfos() {
        return bookCatalogInfos;
    }

    public void setBookCatalogInfos(ArrayList<BookCatalogInfo> bookCatalogInfos) {
        this.bookCatalogInfos = bookCatalogInfos;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", type='" + type + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", state='" + state + '\'' +
                ", bookCount='" + bookCount + '\'' +
                ", newChapterName='" + newChapterName + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", readChapterName='" + readChapterName + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", datailsUrl='" + datailsUrl + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", sourceIndex=" + sourceIndex +
                ", newChapterId=" + newChapterId +
                ", readChapterid=" + readChapterid +
                ", readChapterPager=" + readChapterPager +
                ", closeTime=" + closeTime +
                ", chapterTotal=" + chapterTotal +
                ", unreadSeveral=" + unreadSeveral +
                ", bookSourceInfos=" + bookSourceInfos +
                ", bookCatalogInfos=" + bookCatalogInfos +
                '}';
    }
}
