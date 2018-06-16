package com.android.lucy.treasure.bean;

import android.graphics.Paint;

/**
 * 计算字体需要的配置
 */

public class PagerConfigInfo {
    private int chapterNameHeight;    //章节名View高
    private int bookNameHeight;       //书名View高
    private int chapterContentWidth;  //章节内容View宽
    private int chapterContentHeight; //章节内容View高
    private int pagerLine;  //一页容纳多少行数
    private Paint mTextPaint;
    private int textWidth; //字体宽度
    private int textHeight; //字体高度

    public PagerConfigInfo(int chapterNameHeight, int bookNameHeight, int chapterContentWidth, int chapterContentHeight,
                           int pagerLine, Paint mTextPaint, int textWidth, int textHeight) {
        this.chapterNameHeight = chapterNameHeight;
        this.bookNameHeight = bookNameHeight;
        this.chapterContentWidth = chapterContentWidth;
        this.chapterContentHeight = chapterContentHeight;
        this.pagerLine = pagerLine;
        this.mTextPaint = mTextPaint;
        this.textWidth = textWidth;
        this.textHeight = textHeight;
    }

    public int getChapterNameHeight() {
        return chapterNameHeight;
    }

    public void setChapterNameHeight(int chapterNameHeight) {
        this.chapterNameHeight = chapterNameHeight;
    }

    public int getBookNameHeight() {
        return bookNameHeight;
    }

    public void setBookNameHeight(int bookNameHeight) {
        this.bookNameHeight = bookNameHeight;
    }

    public int getChapterContentWidth() {
        return chapterContentWidth;
    }

    public void setChapterContentWidth(int chapterContentWidth) {
        this.chapterContentWidth = chapterContentWidth;
    }

    public int getChapterContentHeight() {
        return chapterContentHeight;
    }

    public void setChapterContentHeight(int chapterContentHeight) {
        this.chapterContentHeight = chapterContentHeight;
    }

    public int getPagerLine() {
        return pagerLine;
    }

    public void setPagerLine(int pagerLine) {
        this.pagerLine = pagerLine;
    }

    public Paint getmTextPaint() {
        return mTextPaint;
    }

    public void setmTextPaint(Paint mTextPaint) {
        this.mTextPaint = mTextPaint;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public void setTextHeight(int textHeight) {
        this.textHeight = textHeight;
    }


}
