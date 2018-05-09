package com.android.lucy.treasure.runnable.catalog;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BaiduSearchDataInfo;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 获取解析为dd和a的小说目录
 * www.xxbiquge.com  获取小说章节
 * www.pbtxt.com  平板电子书网获取小说章节。
 */

public class DDABookCatalogThread extends BaseReadThread {

    private BaiduSearchDataInfo info;

    public DDABookCatalogThread(String url, BaiduSearchDataInfo info, MyHandler myHandler) {
        super(url, myHandler);
        this.info = info;
    }

    @Override
    public void resoloveUrl(Document doc) {
        Elements metas = doc.select("meta[property]");
        //og:title     书名
        //og:novel:read_url  书页Url
        //og:novel:author  作者
        String bookName = null;
        String url = null;
        String author = null;
        for (Element meta : metas) {
            String property = meta.attr("property");
            switch (property) {
                case "og:novel:book_name":
                    bookName = meta.attr("content");
                    break;
                case "og:novel:read_url":
                    url = meta.attr("content");
                    MyLogcat.myLog("read_url:" + url);
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
        }
        if ((null != bookName && bookName.equals(info.getBookName())) || (null != author && author.equals(info.getAuthor()))) {
            MyLogcat.myLog("url:" + url + "author:" + author + "，info.author:" + info.getAuthor());
            Elements dds = doc.select("dd");
            Elements as = dds.select("a");
            info.setSourceUrl(url);
            int i = 1;
            for (Element a : as) {
                String chapterHref = url + a.attr("href");
                String chapterName = a.text();
                CatalogInfo catalogInfo = new CatalogInfo(chapterHref, chapterName, i);
                info.addCatalogInfo(catalogInfo);
                i++;
            }
            sendObj(info);
//            MyLogcat.myLog("size:"+info.getCatalogInfos().size());
        }

    }
}
