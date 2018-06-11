package com.android.lucy.treasure.runnable.source;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.SourceInfo;
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
            String sourceName = StringUtils.shearSourceName(sourceNameTemp);
            SourceInfo sourceInfo = new SourceInfo(sourceName, sourceBaiduUrl);
            sourceInfo = selectSource(sourceInfo);
            MyLogcat.myLog("未过滤来源：" + sourceName);
            if (null != sourceInfo && !bookInfo.getSourceInfos().contains(sourceInfo)) {
                //没有包含同一个来源
                sourceInfo.setBookInfo(bookInfo);
                bookInfo.getSourceInfos().add(sourceInfo);
            }
        }
        sendObj(MyHandler.BAIDU_SEARCH_OK);

    }

    @Override
    public void errorHandle(IOException e) {
        sendObj(MyHandler.BAIDU_SEARCH_NO);
    }

    private SourceInfo selectSource(SourceInfo sourceInfo) {
        String sourceName = sourceInfo.getSourceName();
        if (Key.DDA_SOURCE.contains(sourceName)) {
            sourceInfo.setWebType("DDA");
            return sourceInfo;
        } else if (Key.LIA_SOURCE.contains(sourceName)) {
            sourceInfo.setWebType("DDA");
            return sourceInfo;
        }
        return null;
    }


}
