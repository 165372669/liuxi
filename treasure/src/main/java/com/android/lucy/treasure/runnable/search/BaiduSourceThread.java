package com.android.lucy.treasure.runnable.search;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.BookSourceInfo;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.StringUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 百度搜索
 */

public class BaiduSourceThread extends BaseReadThread {

    private BookInfo bookInfo;

    public BaiduSourceThread(String url, BookInfo bookInfo, MyHandler bookHandler) {
        super(url, bookHandler);
        this.bookInfo = bookInfo;
    }


    @Override
    public void resoloveUrl(Document doc) {
        //获取第一条网址
        Elements as = doc.select("a[class^=c-showurl]");
        writeFile(as.toString());
        for (Element a : as) {
            String sourceBaiduUrl = a.attr("href");
            String sourceNameTemp = a.text();
            String sourceName = StringUtils.shearSourceName(sourceNameTemp);
            BookSourceInfo bookSourceInfo = new BookSourceInfo(sourceName, sourceBaiduUrl);
            bookSourceInfo = selectSource(bookSourceInfo);
            MyLogcat.myLog("未过滤来源：" + sourceName);
            if (null != bookSourceInfo && !bookInfo.getBookSourceInfos().contains(bookSourceInfo)) {
                //没有包含同一个来源
                bookSourceInfo.setBookInfo(bookInfo);
                bookInfo.getBookSourceInfos().add(bookSourceInfo);
            }
        }
        sendObj(MyHandler.BAIDU_SEARCH_OK);

    }

    @Override
    public void errorHandle(IOException e) {
        sendObj(MyHandler.BAIDU_SEARCH_NO);
    }

    private BookSourceInfo selectSource(BookSourceInfo bookSourceInfo) {
        String sourceName = bookSourceInfo.getSourceName();
        if (Key.DDA_SOURCE.contains(sourceName)) {
            bookSourceInfo.setWebType("DDA");
            return bookSourceInfo;
        } else if (Key.LIA_SOURCE.contains(sourceName)) {
            bookSourceInfo.setWebType("DDA");
            return bookSourceInfo;
        }
        return null;
    }


}
