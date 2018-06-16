package com.android.lucy.treasure.runnable.catalog;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookCatalogInfo;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.BookSourceInfo;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 获取解析为dd和a的小说章节目录
 * www.xxbiquge.com  新笔趣阁
 * www.pbtxt.com  平板电子书网获取小说章节。
 * www.ddbiquge.com  顶点笔趣阁
 */

public class DDABookCatalogThread extends BaseReadThread {

    private BookInfo bookInfo;

    public DDABookCatalogThread(String url, BookInfo bookInfo, MyHandler myHandler) {
        super(url, myHandler);
        this.bookInfo = bookInfo;
        flag = "DDABookCatalogThread";
    }

    @Override
    public void resoloveUrl(Document doc) {
        int sourceIndex = bookInfo.getSourceIndex();
        BookSourceInfo bookSourceInfo = bookInfo.getBookSourceInfos().get(sourceIndex);
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
                    MyLogcat.myLog("book_name:" + bookName);
                    break;
                case "og:novel:read_url":
                    url = meta.attr("content");
                    MyLogcat.myLog("read_url:" + url);
                    bookSourceInfo.setSourceUrl(url);
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
            MyLogcat.myLog("url:" + url);
        }

        if ((null != bookName && bookName.equals(bookInfo.getBookName())) || (null != author && author.equals(bookInfo.getAuthor()))) {
            Elements dds = doc.select("dd");
            Elements as = dds.select("a");
            int i = 0;
            for (Element a : as) {
                String chapterHref = url + a.attr("href");
                String chapterName = a.text();
                i++;
                BookCatalogInfo bookCatalogInfo = new BookCatalogInfo(bookInfo, i, chapterHref, chapterName, 0);
                if (!bookInfo.getBookCatalogInfos().contains(bookCatalogInfo)) {
                    bookCatalogInfo.setBookInfo(bookInfo);
                    bookInfo.getBookCatalogInfos().add(bookCatalogInfo);
                }
                if (getIsCancelled()) {
                    return;
                }
            }
            saveBookData(bookInfo);
            if (bookInfo.getBookCatalogInfos().size() > 0) {
                sendObj(MyHandler.CATALOG_SOURCE_OK);
            } else {
                sendObj(MyHandler.CATALOG_SOURCE_NO);
            }
//            MyLogcat.myLog("size:"+info.getBookCatalogInfos().size());
        }
    }

    @Override
    public void errorHandle(IOException e) {
        sendObj(MyHandler.CATALOG_SOURCE_ERROR);
    }


    @Override
    public void xxbuquge() {
        if (bookInfo.getSourceName().contains("xxbiquge")) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(baseUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();
                MyLogcat.myLog("code:" + statusCode);
                if (statusCode == 302) {
                    Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
                    Set<String> keySet = headerFields.keySet();
                    Iterator<String> it = keySet.iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        List<String> value = headerFields.get(key);
                        //key:Location,s:https://www.xxbiquge.com/79_79382/
                        if (key != null && key.equals("Location")) {
                            baseUrl = value.get(0);
                        }
                    }
                    urlConnection = (HttpURLConnection) new URL(baseUrl).openConnection();
                    urlConnection.setDoInput(true);
                    MyLogcat.myLog("statusCode:" + urlConnection.getResponseCode());
                    if (urlConnection.getResponseCode() == 200) {
                        StringBuilder sb = new StringBuilder();
                        reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        resoloveUrl(Jsoup.parse(sb.toString(), baseUrl));
                    }
                }
            } catch (IOException e) {
                MyLogcat.myLog(baseUrl + "，网页读取失败！!!!");
            } finally {
                try {
                    if (null != urlConnection) urlConnection.disconnect();
                    if (null != reader) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
