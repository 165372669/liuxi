package com.android.lucy.treasure.runnable.catalog;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BaiduSearchDataInfo;
import com.android.lucy.treasure.bean.CatalogInfo;
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
 */

public class LIABookCatalogThread extends BaseReadThread {

    private BaiduSearchDataInfo info;

    public LIABookCatalogThread(String url, BaiduSearchDataInfo info, MyHandler myHandler) {
        super(url, myHandler);
        this.info = info;
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
        String bookName = null;
        if (metas.size() > 0) {
            for (Element meta : metas) {
                String property = meta.attr("property");
                if (property.equals("og:title"))
                    bookName = meta.attr("content");
            }
        }
        if (null != bookName && bookName.equals(info.getBookName())) {
            Elements lis = doc.select("li");
            Elements rels = lis.select("a[rel]");
            int i = 1;
            for (Element rel : rels) {
                String chapterUrl = rel.attr("href");
                String chapterName = rel.text();
                CatalogInfo catalogInfo = new CatalogInfo(chapterUrl, chapterName, i);
                info.addCatalogInfo(catalogInfo);
                i++;
            }
            sendObj(info);
        }
    }
}
