package com.android.lucy.treasure.runnable.detalis;

import android.os.Handler;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.utils.MyLogcat;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 起点小说详情页面
 */
public class QiDianDetailsThread extends BaseReadThread {
    private BookInfo bookInfo;

    public QiDianDetailsThread(String baseUrl, BookInfo bookInfo, Handler myHandler) {
        super(baseUrl, myHandler);
        this.bookInfo = bookInfo;
    }

    @Override
    public void resoloveUrl(Document doc) {
        Elements divs = doc.getElementsByClass("book-detail-wrap center990");
        for (Element div : divs) {
            //图片链接
            Element bookImg = div.getElementById("bookImg");
            Elements imgs = bookImg.getElementsByTag("img");
            String imgUrl = "http:" + imgs.attr("src");
            MyLogcat.myLog("img=" + imgUrl);
            //作品信息
            Elements bookInfos = div.getElementsByClass("book-info");

        }
    }

    @Override
    public void errorHandle(IOException e) {

    }
}
