package com.android.lucy.treasure.runnable.search;

import android.os.Handler;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.SearchInfo;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.StringUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;

/**
 * 笔趣阁搜索小说
 * www.biqiuge.com
 */

public class BiqiugeSearchThread extends BaseReadThread {
    private LinkedList<SearchInfo> searchInfos;
    private String bookName;

    public BiqiugeSearchThread(String baseUrl, String bookName, LinkedList<SearchInfo> searchInfos, Handler myHandler) {
        super(baseUrl, myHandler);
        this.searchInfos = searchInfos;
        this.bookName = bookName;
    }

    @Override
    public void resoloveUrl(Document doc) {
        Elements ps = doc.getElementsByClass("p10");
        for (Element p : ps) {
            Element img = p.getElementsByClass("bookimg").first();
            Elements urls = img.children();
            //图片网址和书页网址
            String detailUrl = "http://www.biqiuge.com" + urls.get(0).attr("href");
            MyLogcat.myLog("detailUrl:" + detailUrl);
            String imgUrl = "http://www.biqiuge.com" + urls.get(0).getElementsByTag("img").attr("src");
            //书名
            Elements name = p.getElementsByClass("bookname");
            String bookName = name.text();
            //作者
            Elements author = p.getElementsByClass("author");
            String bookAuthor = StringUtils.splitString(author.text(), "：", 1);
            //类型
            Elements cats = p.getElementsByClass("cat");
            String bookType = StringUtils.splitString(cats.text(), "：", 1);
            //最新章节
            Elements update = p.getElementsByClass("update");
            Elements a = update.select("a");
            String bookNewChapterName = a.text();
            //简介
            Elements desc = p.getElementsByTag("p");
            String bookDesc = desc.text();
            //建立搜索对象
            SearchInfo searchInfo = new SearchInfo(detailUrl, imgUrl, bookName, bookAuthor, bookType, bookDesc, bookNewChapterName);
            if (bookName.equals(this.bookName)) {
                //搜索出来的书名排在第一个.
                searchInfos.addFirst(searchInfo);
            } else {
                searchInfos.add(searchInfo);
            }
        }

        if (searchInfos.size() > 0) {
            sendObj(MyHandler.SEARCH_DETAILS_OK);
        } else {
            sendObj(MyHandler.SEARCH_DETAILS_NO);
        }
    }

    @Override
    public void errorHandle(IOException e) {
        if (count < 5) {
            readUrl();
            count++;
        } else {
            sendObj(MyHandler.SEARCH_DETAILS_ERROR);
        }

    }
}
