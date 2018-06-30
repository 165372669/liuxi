package com.android.lucy.treasure.runnable.catalog;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.BookCatalogInfo;
import com.android.lucy.treasure.bean.BookSourceInfo;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 获取解析为li和a标签的小说目录
 * www.80txt.com  八零电子书
 * www.88dus.com  88读书网
 */

public class LIABookCatalogThread extends BaseReadThread {


    private BookInfo bookInfo;
    private BookSourceInfo bookSourceInfo;

    public LIABookCatalogThread(String url, BookInfo bookInfo, MyHandler myHandler) {
        super(url, myHandler);
        this.bookInfo = bookInfo;
        flag = "LIABookCatalogThread";
    }

    @Override
    public void resoloveUrl(Document doc) {
        Elements metas = doc.select("meta[property]");
        int sourceIndex = bookInfo.getSourceIndex();
        bookSourceInfo = bookInfo.getBookSourceInfos().get(sourceIndex);
        if (metas.size() > 0) {
            parseChapter(doc, metas);
        } else {
            parseTextDownload(doc);
        }

    }


    @Override
    public void errorHandle(IOException e) {
        sendObj(MyHandler.CATALOG_SOURCE_ERROR);
    }


    public void parseTextDownload(Document doc) {
        Elements as = doc.select("a[class^=tools]");
        String chapterUrl = null;
        for (Element a : as) {
            if (a.text().equals("在线阅读_目录"))
                chapterUrl = a.attr("href");
        }
        if (null != chapterUrl) {
            try {
                Document document = Jsoup.connect(chapterUrl).get();
                Elements metas = document.select("meta[property]");
                parseChapter(document, metas);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void parseChapter(Document doc, Elements metas) {
        if (metas.size() > 0) {
            for (Element meta : metas) {
                String property = meta.attr("property");
                switch (property) {
                    case "og:novel:book_name":
                        bookName = meta.attr("content");
                        break;
                    case "og:novel:read_url":
                        url = meta.attr("content");
                        bookSourceInfo.setSourceUrl(url);
                        break;
                    case "og:novel:author":
                        author = meta.attr("content");
                        break;

                }
            }
        }
    }

    @Override
    public void getCatalogs(Document doc) {
        String sourceName = bookSourceInfo.getSourceName();
        if (null != bookName && bookName.equals(bookInfo.getBookName())) {
            Elements catalogs = null;
            boolean is88dushu = false;
            Elements lis = doc.select("li");
            switch (sourceName) {
                case Key.KEY_88DUSHU:
                    //处理88读书网
                    is88dushu = true;
                    catalogs = lis.select("a[href]");
                    break;
                case Key.KEY_80TXT:
                    catalogs = lis.select("a[rel]");
                    break;
            }
            int i = 0;
            for (Element cahpter : catalogs) {
                String chapterUrl = cahpter.attr("href");
                if (is88dushu) {
                    chapterUrl = url + chapterUrl;
                }
                if (chapterUrl.endsWith("html")) {
                    String chapterName = cahpter.text();
                    i++;
                    BookCatalogInfo bookCatalogInfo = new BookCatalogInfo(bookInfo, i, chapterUrl, chapterName, 0);
                    if (!bookInfo.getBookCatalogInfos().contains(bookCatalogInfo)) {
                        bookCatalogInfo.setBookInfo(bookInfo);
                        bookInfo.getBookCatalogInfos().add(bookCatalogInfo);
                    }
                }
                if (getIsCancelled())
                    return;
            }
            saveBookData(bookInfo);
            if (bookInfo.getBookCatalogInfos().size() > 0) {
                sendObj(MyHandler.CATALOG_SOURCE_OK);
            } else {
                sendObj(MyHandler.CATALOG_SOURCE_NO);
            }
        }
    }
}
