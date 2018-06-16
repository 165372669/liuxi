package com.android.lucy.treasure.bean;

import android.graphics.drawable.BitmapDrawable;

/**
 * 搜索小说数据对象
 */

public class SearchInfo{

    private String datailsUrl; //详情页面
    private String imgUrl;//图片链接
    private String bookName;//书名
    private String author;//作者
    private String type;//类型
    private String desc;//介绍
    private String newChapter;//最新章节
    private BitmapDrawable image;

    public SearchInfo(String datailsUrl, String imgUrl, String bookName, String author, String type, String desc, String newChapter) {
        this.datailsUrl = datailsUrl;
        this.imgUrl = imgUrl;
        this.bookName = bookName;
        this.author = author;
        this.type = type;
        this.desc = desc;
        this.newChapter = newChapter;
    }

    public String getDatailsUrl() {
        return datailsUrl;
    }

    public void setDatailsUrl(String datailsUrl) {
        this.datailsUrl = datailsUrl;
    }

    public String getNewChapter() {
        return newChapter;
    }

    public void setNewChapter(String newChapter) {
        this.newChapter = newChapter;
    }

    public BitmapDrawable getImage() {
        return image;
    }

    public void setImage(BitmapDrawable image) {
        this.image = image;
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

}
