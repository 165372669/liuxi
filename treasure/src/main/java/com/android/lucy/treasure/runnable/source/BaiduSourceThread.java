package com.android.lucy.treasure.runnable.source;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.SourceInfo;
import com.android.lucy.treasure.runnable.catalog.DDABookCatalogThread;
import com.android.lucy.treasure.runnable.catalog.LIABookCatalogThread;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 百度搜索
 */

public class BaiduSourceThread extends BaseReadThread {

    private BookInfo bookInfo;
    private MyHandler bookHandler;

    public BaiduSourceThread(String url, BookInfo bookInfo, MyHandler bookHandler) {
        super(url, bookHandler);
        this.bookInfo = bookInfo;
        this.bookHandler = bookHandler;
    }


    @Override
    public void resoloveUrl(Document doc) {
        //获取第一条网址
        Elements as = doc.select("a[class^=c-showurl]");
        for (Element a : as) {
            String sourceBaiduUrl = a.attr("href");
            String sourceNameTemp = a.text();
            String sourceName = shearSourceName(sourceNameTemp);
            SourceInfo sourceInfo = new SourceInfo(sourceName, sourceBaiduUrl);
            sourceInfo = selectSource(sourceInfo);
            if (null != sourceInfo && !bookInfo.getSourceInfos().contains(sourceInfo)) {
                //没有包含同一个来源
                sourceInfo.setBookInfo(bookInfo);
                bookInfo.getSourceInfos().add(sourceInfo);
            }
        }
        sendObj(MyHandler.BAIDU_SEARCH_OK);
        MyLogcat.myLog("bookName:" + bookInfo.getBookName() + ",sourceDataSize:" + bookInfo.getSourceInfos());
    }

    @Override
    public void errorHandle(IOException e) {
        sendObj(MyHandler.BAIDU_SEARCH_NO);
    }

    private SourceInfo selectSource(SourceInfo sourceInfo) {
        String sourceName = sourceInfo.getSourceName();
        String[] dda_sources = Key.DDA_SOURCE.split(",");
        String[] lia_sources = Key.LIA_SOURCE.split(",");
        for (int i = 0; i < dda_sources.length; i++) {
            if (sourceName.equals(dda_sources[i])) {
                sourceInfo.setWebType("DDA");
                return sourceInfo;
            }
        }
        for (int i = 0; i < lia_sources.length; i++) {
            if (sourceName.equals(lia_sources[i])) {
                sourceInfo.setWebType("LIA");
                return sourceInfo;
            }
        }
        return null;
    }


    /**
     * 截取来源名称
     *
     * @param sourceName 来源网址
     * @return
     */
    private String shearSourceName(String sourceName) {
        if (sourceName.startsWith("http")) {
            int start = sourceName.indexOf("//") + 2;
            int end = sourceName.indexOf("/", start);
            return sourceName.substring(start, end);
        } else
            return sourceName.substring(0, sourceName.indexOf("/"));
    }

}
