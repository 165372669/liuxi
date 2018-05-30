package com.android.lucy.treasure.runnable.catalog;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * 新笔趣阁2 www.xxbiquge.com获取小说目录
 */

public class BookXxbiqugeCatalogThread extends BaseReadThread {


    public BookXxbiqugeCatalogThread(String url, MyHandler myHandler) {
        super(url, myHandler);
    }

    @Override
    public void resoloveUrl(Document doc) {
        Elements dds = doc.select("dd");
        int i = 1;
        for (Element dd : dds) {
            String href = dd.toString();
            int start = href.indexOf('"');
            int end = href.lastIndexOf('"');
            String chapterHref = href.substring(start + 1, end);
            String chapterName = dd.text();
            i++;
            if (i < 5)
                MyLogcat.myLog(i + ":" + "chapterHref:" + chapterHref + "\n" + "chapterName:" + chapterName);
        }

    }
}
