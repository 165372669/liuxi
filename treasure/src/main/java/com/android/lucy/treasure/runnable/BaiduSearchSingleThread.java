package com.android.lucy.treasure.runnable;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookDataInfo;
import com.android.lucy.treasure.runnable.catalog.DDABookCatalogThread;
import com.android.lucy.treasure.runnable.catalog.LIABookCatalogThread;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 百度搜索
 */

public class BaiduSearchSingleThread extends BaseReadThread {


    private String bookName;
    private MyHandler bookHandler;
    private String author;

    public BaiduSearchSingleThread(String url, String bookName, String author, MyHandler bookHandler) {
        super(url, bookHandler);
        this.bookName = bookName;
        this.bookHandler = bookHandler;
        this.author = author;
        this.author = author;
    }


    @Override
    public void resoloveUrl(Document doc) {
        if (null == doc)
            return;
        //获取第一条网址
        Elements as = doc.select("a[class^=c-showurl]");
        MyLogcat.myLog("bookName:" + bookName);
        for (Element a : as) {
            String sourceUrl = a.attr("href");
            String sourceNameTemp = a.text();
            String sourceName = shearSourceName(sourceNameTemp);
            BookDataInfo bookDataInfo = new BookDataInfo(bookName, sourceUrl, sourceName, author);
            //MyLogcat.myLog("sourceUrl:" + sourceUrl);
            //MyLogcat.myLog("sourceName:" + sourceName);
            selectSource(bookDataInfo);
        }
    }

    private void selectSource(BookDataInfo bookDataInfo) {
        String sourceName = bookDataInfo.getSourceName();
        String sourceUrl = bookDataInfo.getSourceUrl();
        String[] dda_sources = Key.DDA_SOURCE.split(",");
        String[] lia_sources = Key.LIA_SOURCE.split(",");
        for (int i = 0; i < dda_sources.length; i++) {
            if (sourceName.equals(dda_sources[i]))
                ThreadPool.getInstance().submitTask(new DDABookCatalogThread(sourceUrl, bookDataInfo, bookHandler));
        }
        for (int i = 0; i < lia_sources.length; i++) {
            if (sourceName.equals(lia_sources[i]))
                ThreadPool.getInstance().submitTask(new LIABookCatalogThread(sourceUrl, bookDataInfo, bookHandler));
        }

    }


    /*
    * 来源网址截取名称。
    * */
    private String shearSourceName(String sourceName) {
        if (sourceName.startsWith("http")) {
            int start = sourceName.indexOf("//") + 2;
            int end = sourceName.indexOf("/", start);
            return sourceName.substring(start, end);
        } else
            return sourceName.substring(0, sourceName.indexOf("/"));
    }

}
