package com.android.lucy.treasure.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.android.lucy.treasure.bean.BookInfo;

import org.litepal.tablemanager.Connector;

/**
 * BookInfo数据库操作
 */

public class BookInfoDao {

    /**
     * 插入一条新的小说信息
     * @param bookInfo  小说对象
     * @return
     */
    public static long saveBookData(BookInfo bookInfo) {
        SQLiteDatabase db = Connector.getDatabase();
        ContentValues values = new ContentValues();
        values.put("author", bookInfo.getAuthor());
        values.put("bookname", bookInfo.getBookName());
        values.put("chaptertotal", bookInfo.getChapterTotal());
        values.put("closetime", bookInfo.getCloseTime());
        values.put("imgurl", bookInfo.getImgUrl());
        values.put("newchapterid", bookInfo.getNewChapterId());
        values.put("newchaptername", bookInfo.getNewChapterName());
        values.put("readchapterid", bookInfo.getReadChapterid());
        values.put("readchaptername", bookInfo.getReadChapterName());
        values.put("readchapterpager", bookInfo.getReadChapterPager());
        values.put("sourceindex", bookInfo.getSourceIndex());
        values.put("sourcename", bookInfo.getSourceName());
        values.put("type", bookInfo.getType());
        values.put("unreadseveral", bookInfo.getUnreadSeveral());
        values.put("updatetime", bookInfo.getUpdateTime());
        values.put("wordcounttotal", bookInfo.getWordCountTotal());
        values.put("zhuishuurl", bookInfo.getZhuishuUrl());
        return db.insert("bookinfo", null, values);
    }
}
