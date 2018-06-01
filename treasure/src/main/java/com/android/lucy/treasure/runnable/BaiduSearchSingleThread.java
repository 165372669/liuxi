package com.android.lucy.treasure.runnable;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookDataInfo;
import com.android.lucy.treasure.bean.SourceDataInfo;
import com.android.lucy.treasure.runnable.catalog.DDABookCatalogThread;
import com.android.lucy.treasure.runnable.catalog.LIABookCatalogThread;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 百度搜索
 */

public class BaiduSearchSingleThread extends BaseReadThread {


    private MyHandler bookHandler;
    private BookDataInfo bookDataInfo;

    public BaiduSearchSingleThread(String url, BookDataInfo bookDataInfo, MyHandler bookHandler) {
        super(url, bookHandler);
        this.bookHandler = bookHandler;
        this.bookDataInfo = bookDataInfo;
    }


    @Override
    public void resoloveUrl(Document doc) {
        //获取第一条网址
        Elements as = doc.select("a[class^=c-showurl]");
        for (Element a : as) {
            String sourceUrl = a.attr("href");
            String sourceNameTemp = a.text();
            String sourceName = shearSourceName(sourceNameTemp);
            SourceDataInfo sourceDataInfo = new SourceDataInfo(sourceName, sourceUrl);
            //MyLogcat.myLog("sourceUrl:" + sourceUrl);
            //MyLogcat.myLog("sourceName:" + sourceName);
            selectSource(sourceDataInfo);
        }
        MyLogcat.myLog("bookName:" + bookDataInfo.getBookName() + ",sourceDataSize:" + bookDataInfo.getSourceDataInfos());
    }

    private void selectSource(SourceDataInfo sourceDataInfo) {
        String sourceName = sourceDataInfo.getSourceName();
        String sourceUrl = sourceDataInfo.getSourceUrl();
        String[] dda_sources = Key.DDA_SOURCE.split(",");
        String[] lia_sources = Key.LIA_SOURCE.split(",");
        if (bookDataInfo.getSourceDataInfos().contains(sourceDataInfo)) {
            //没有包含同一个来源
            return;
        }
        for (int i = 0; i < dda_sources.length; i++) {
            if (sourceName.equals(dda_sources[i])) {
                bookDataInfo.getSourceDataInfos().add(sourceDataInfo);
                ThreadPool.getInstance().submitTask(new DDABookCatalogThread(sourceUrl, sourceDataInfo,
                        bookDataInfo.getBookName(), bookDataInfo.getAuthor(), bookHandler));
            }
        }
        for (int i = 0; i < lia_sources.length; i++) {
            if (sourceName.equals(lia_sources[i])) {
                bookDataInfo.getSourceDataInfos().add(sourceDataInfo);
                ThreadPool.getInstance().submitTask(new LIABookCatalogThread(sourceUrl, sourceDataInfo,
                        bookDataInfo.getBookName(), bookDataInfo.getAuthor(), bookHandler));
            }
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
