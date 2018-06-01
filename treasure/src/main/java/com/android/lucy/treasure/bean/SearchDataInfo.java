package com.android.lucy.treasure.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 搜索小说数据对象
 */

public class SearchDataInfo implements Parcelable {


    private String imgUrl;//图片链接
    private String bookName;//书名
    private String author;//作者
    private String type;//类型
    private String desc;//介绍
    private String bookUrl_ZhuiShu;//追书页面介绍
    private String bookSize;//小说字数
    private String bookUpdateTime;//最后更新时间
    private String newChapter;//最新章节

    public SearchDataInfo(String imgUrl, String bookName, String author, String type, String desc, String bookUrl_ZhuiShu) {
        this.imgUrl = imgUrl;
        this.bookName = bookName;
        this.author = author;
        this.type = type;
        this.desc = desc;
        this.bookUrl_ZhuiShu = bookUrl_ZhuiShu;
    }

    protected SearchDataInfo(Parcel in) {
        imgUrl = in.readString();
        bookName = in.readString();
        author = in.readString();
        type = in.readString();
        desc = in.readString();
        bookUrl_ZhuiShu = in.readString();
        bookSize = in.readString();
        bookUpdateTime = in.readString();
        newChapter = in.readString();

    }

    public String getNewChapter() {
        return newChapter;
    }

    public void setNewChapter(String newChapter) {
        this.newChapter = newChapter;
    }

    public String getBookSize() {
        return bookSize;
    }

    public void setBookSize(String bookSize) {
        this.bookSize = bookSize;
    }

    public String getBookUpdateTime() {
        return bookUpdateTime;
    }

    public void setBookUpdateTime(String bookUpdateTime) {
        this.bookUpdateTime = bookUpdateTime;
    }

    public String getBookUrl_ZhuiShu() {
        return bookUrl_ZhuiShu;
    }

    public void setBookUrl_ZhuiShu(String bookUrl_ZhuiShu) {
        this.bookUrl_ZhuiShu = bookUrl_ZhuiShu;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return imgUrl + bookName + author + type + desc + bookUrl_ZhuiShu;
    }

    public static final Creator<SearchDataInfo> CREATOR = new Creator<SearchDataInfo>() {
        @Override
        public SearchDataInfo createFromParcel(Parcel in) {
            return new SearchDataInfo(in);
        }

        @Override
        public SearchDataInfo[] newArray(int size) {
            return new SearchDataInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getImgUrl());
        parcel.writeString(getBookName());
        parcel.writeString(getAuthor());
        parcel.writeString(getType());
        parcel.writeString(getDesc());
        parcel.writeString(getBookUrl_ZhuiShu());
        parcel.writeString(getBookSize());
        parcel.writeString(getBookUpdateTime());
        parcel.writeString(getNewChapter());
    }
}
