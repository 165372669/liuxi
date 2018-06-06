package com.android.lucy.treasure.runnable.async;

import com.android.lucy.treasure.base.BaseReadAsyncTask;
import com.android.lucy.treasure.bean.SearchInfo;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * 获取追书页面小说的总字数和最后更新时间，最新章节
 */

public class ZhuiShuDataAsync extends BaseReadAsyncTask<SearchInfo> {


    private SearchInfo searchInfo;

    public ZhuiShuDataAsync(SearchInfo searchInfo) {
        this.searchInfo = searchInfo;
    }

    @Override
    public SearchInfo resoloveUrl(Document doc) {
        Elements divs = doc.select("div[class^=book]");
        if (isCancelled())
            return null;
        Elements ps = divs.select("p[class^=sup]");
        String bookSize = null;
        String bookUpdateTime = null;
        String newChapter = null;
        if (ps.size() > 0) {
            String size = ps.get(0).text();
            int position = size.lastIndexOf("|");
            bookSize = size.substring(position + 1, size.length());
            bookUpdateTime = ps.get(1).text();
        }
        Elements as = divs.select("a[href]");
        if (as.size() > 0) {
            newChapter = as.get(2).text();
        }
        searchInfo.setBookSize(bookSize);
        searchInfo.setBookUpdateTime(bookUpdateTime);
        searchInfo.setNewChapter(newChapter);
        return searchInfo;
    }

    @Override
    protected void onPostExecute(SearchInfo searchInfo) {
        if (isCancelled())
            return;
        if (null != onUpdateDataLisetener)
            onUpdateDataLisetener.setData(searchInfo);
        super.onPostExecute(searchInfo);
    }


}
