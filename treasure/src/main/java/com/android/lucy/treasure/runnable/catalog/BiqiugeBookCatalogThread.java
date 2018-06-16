package com.android.lucy.treasure.runnable.catalog;

import android.os.Handler;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookCatalogInfo;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 笔趣阁章节默认读取
 * www.biqiuge.com
 */

public class BiqiugeBookCatalogThread extends BaseReadThread {
    private BookInfo bookInfo;

    public BiqiugeBookCatalogThread(String baseUrl, BookInfo bookInfo, Handler myHandler) {
        super(baseUrl, myHandler);
        this.bookInfo = bookInfo;
    }

    @Override
    public void resoloveUrl(Document doc) {
        Elements small = doc.getElementsByClass("small");
        String state = small.get(2).text();
        String bookCount = small.get(3).text();
        String updateTime = small.get(4).text();
        String newChapterName = small.get(5).text();
        bookInfo.setState(state);
        bookInfo.setBookCount(bookCount);
        bookInfo.setUpdateTime(updateTime);
        bookInfo.setNewChapterName(newChapterName);
        //章节目录获取
        Elements dd = doc.select("dd");
        Elements as = dd.select("a");
        int i = 0;
        for (Element a : as) {
            String chapterHref = Key.KEY_BIQIUGE + a.attr("href");
            String chapterName = a.text();
            i++;
            BookCatalogInfo bookCatalogInfo = new BookCatalogInfo(bookInfo, i, chapterHref, chapterName, 0);
            if (!bookInfo.getBookCatalogInfos().contains(bookCatalogInfo)) {
                bookCatalogInfo.setBookInfo(bookInfo);
                bookInfo.getBookCatalogInfos().add(bookCatalogInfo);
            }
            if (getIsCancelled()) {
                return;
            }
        }
        saveBookData(bookInfo);
        if (bookInfo.getBookCatalogInfos().size() > 0) {
            sendObj(MyHandler.CATALOG_SOURCE_OK);
        } else {
            sendObj(MyHandler.CATALOG_SOURCE_NO);
        }
    }

    @Override
    public void errorHandle(IOException e) {
        sendObj(MyHandler.CATALOG_SOURCE_ERROR);
    }
}
