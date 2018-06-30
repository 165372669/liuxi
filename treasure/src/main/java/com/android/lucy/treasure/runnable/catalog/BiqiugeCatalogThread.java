package com.android.lucy.treasure.runnable.catalog;

import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.StringUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 笔趣阁
 * www.biqiuge.com
 */

public class BiqiugeCatalogThread extends DDABookCatalogThread {
    private BookInfo bookInfo;

    public BiqiugeCatalogThread(String baseUrl, BookInfo bookInfo, MyHandler myHandler) {
        super(baseUrl, bookInfo, myHandler);
        this.bookInfo = bookInfo;
    }

    @Override
    public void resoloveUrl(Document doc) {
        Elements small = doc.getElementsByClass("small");
        Elements span = small.select("span");
        String state = StringUtils.splitString(span.get(2).text(), "：", 1);
        String bookCount = StringUtils.splitString(span.get(3).text(), "：", 1);
        String updateTime = StringUtils.splitString(span.get(4).text(), "：", 1);
        String newChapterName = span.get(5).text();
        MyLogcat.myLog("state：" + state + ",bookCount:" + bookCount + ",updateTime:" + updateTime + ",newChapterName:" + newChapterName);
        bookInfo.setState(state);
        bookInfo.setBookCount(bookCount);
        bookInfo.setUpdateTime(updateTime);
        bookInfo.setNewChapterName(newChapterName);
        if (bookInfo.getId() > 0) {
            bookInfo.saveOrUpdate("bookName=?", bookInfo.getBookName());
        }
    }

    @Override
    public void getCatalogs(Document doc) {
        Elements lists = doc.getElementsByClass("listmain");
        boolean isText;
        for (Element list : lists) {
            MyLogcat.myLog("text:" + list.text());
        }
        sendObj(MyHandler.CATALOG_SOURCE_OK);
    }

    @Override
    public void errorHandle(IOException e) {
        if (count < 5) {
            readUrl();
            count++;
        } else {
            sendObj(MyHandler.CATALOG_SOURCE_ERROR);
        }
        //如果5次都搜索不到转入追书
    }
}
