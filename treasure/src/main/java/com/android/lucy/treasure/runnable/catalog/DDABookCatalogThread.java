package com.android.lucy.treasure.runnable.catalog;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookCatalogInfo;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.BookSourceInfo;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 获取解析为dd和a的小说章节目录
 * www.xxbiquge.com  新笔趣阁
 * www.pbtxt.com  平板电子书网获取小说章节。
 * www.ddbiquge.com  顶点笔趣阁
 * www.biqiuge.com  笔趣阁
 */

public class DDABookCatalogThread extends BaseReadThread {

    private BookInfo bookInfo;

    public DDABookCatalogThread(String url, BookInfo bookInfo, MyHandler myHandler) {
        super(url, myHandler);
        this.bookInfo = bookInfo;
        flag = "DDABookCatalogThread";
    }

    @Override
    public void resoloveUrl(Document doc) {
        int sourceIndex = bookInfo.getSourceIndex();
        BookSourceInfo bookSourceInfo = bookInfo.getBookSourceInfos().get(sourceIndex);
        Elements metas = doc.select("meta[property]");
        //og:title     书名
        //og:novel:read_url  书页Url
        //og:novel:author  作者
        bookName = null;
        url = null;
        author = null;
        for (Element meta : metas) {
            String property = meta.attr("property");
            switch (property) {
                case "og:novel:book_name":
                    bookName = meta.attr("content");
                    MyLogcat.myLog("book_name:" + bookName);
                    break;
                case "og:novel:read_url":
                    url = meta.attr("content");
                    MyLogcat.myLog("read_url:" + url);
                    bookSourceInfo.setSourceUrl(url);
                    break;
                case "og:novel:author":
                    author = meta.attr("content");
                    break;
                default:
                    break;
            }
        }

        if (null != url && url.startsWith("https://www.xxbiquge.com")) {
            url = url.substring(0, 24);
            MyLogcat.myLog("url:" + url);
        }
        if (null != url && url.startsWith("http://www.biqiuge.com")) {
            String flag = "com/";
            url = url.substring(0, url.indexOf(flag) + flag.length() - 1);
            MyLogcat.myLog("url:" + url);
        }

    }

    @Override
    public void getCatalogs(Document doc) {
        if ((null != bookName && bookName.equals(bookInfo.getBookName())) || (null != author && author.equals(bookInfo.getAuthor()))) {
            Elements dds = doc.select("dd");
            Elements as = dds.select("a");
            int i = 0;
            for (Element a : as) {
                String chapterHref = url + a.attr("href");
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
//            MyLogcat.myLog("size:"+info.getBookCatalogInfos().size());
        }
    }

    @Override
    public void errorHandle(IOException e) {
        if (count < 5) {
            readUrl();
            count++;
        } else {
            sendObj(MyHandler.CATALOG_SOURCE_ERROR);
        }
    }
}
