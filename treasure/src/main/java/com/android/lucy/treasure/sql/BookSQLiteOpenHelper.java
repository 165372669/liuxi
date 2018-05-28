package com.android.lucy.treasure.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 书架书籍数据库
 */

public class BookSQLiteOpenHelper extends SQLiteOpenHelper {

    public static String CREATE_BOOK = "create table book ("
            + "id integer primary key autoincrement,"
            + "bookName text,"
            + "author text,"
            + "sourceUrl text,"
            + "sourceName text)";

    public BookSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
