package com.android.lucy.treasure.runnable.catalog;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.SourceInfo;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;

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

    public LIABookCatalogThread(String url, BookInfo bookInfo, MyHandler myHandler) {
        super(url, myHandler);
        this.bookInfo = bookInfo;
    }

    @Override
    public void resoloveUrl(Document doc) {
        Elements metas = doc.select("meta[property]");
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
        int sourceIndex = bookInfo.getSourceIndex();
        SourceInfo sourceInfo = bookInfo.getSourceInfos().get(sourceIndex);
        String sourceName = sourceInfo.getSourceName();
        String bookName = null;
        String url = null;
        String author = null;
        if (metas.size() > 0) {
            for (Element meta : metas) {
                String property = meta.attr("property");
                switch (property) {
                    case "og:novel:book_name":
                        bookName = meta.attr("content");
                        break;
                    case "og:novel:read_url":
                        url = meta.attr("content");
                        sourceInfo.setSourceUrl(url);
                        break;
                    case "og:novel:author":
                        author = meta.attr("content");
                        break;

                }
            }
        }
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
            int i = 1;
            for (Element cahpter : catalogs) {
                String chapterUrl = cahpter.attr("href");
                if (is88dushu) {
                    chapterUrl = url + chapterUrl;
                }
                if (chapterUrl.endsWith("html")) {
                    String chapterName = cahpter.text();
                    CatalogInfo catalogInfo = new CatalogInfo(bookInfo, i, chapterUrl, chapterName, 0);
                    catalogInfo.setBookInfo(bookInfo);
                    bookInfo.getCatalogInfos().add(catalogInfo);
                    i++;
                }
                if (getIsCancelled())
                    return;
            }
            bookInfo.setChapterTotal(i - 1);
            bookInfo.setNewChapterId(i - 1); //最新章节id
            if (bookInfo.getCatalogInfos().size() > 0) {
                sendObj(MyHandler.CATALOG_SOURCE_OK);
            } else {
                sendObj(MyHandler.CATALOG_SOURCE_NO);
            }
        }
    }
}
