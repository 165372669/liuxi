package com.android.lucy.treasure.runnable;

import com.android.lucy.treasure.base.BaseReadAsyncTask;
import com.android.lucy.treasure.bean.SearchDataInfo;
import com.android.lucy.treasure.manager.AsyncManager;
import com.android.lucy.treasure.utils.MyLogcat;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * 获取追书页面小说的总字数和最后更新时间，最新章节
 */

public class ZhuiShuDataAsync extends BaseReadAsyncTask<SearchDataInfo> {


    private SearchDataInfo searchDataInfo;

    public ZhuiShuDataAsync(SearchDataInfo searchDataInfo) {
        this.searchDataInfo = searchDataInfo;
    }

    @Override
    public SearchDataInfo resoloveUrl(Document doc) {
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
        searchDataInfo.setBookSize(bookSize);
        searchDataInfo.setBookUpdateTime(bookUpdateTime);
        searchDataInfo.setNewChapter(newChapter);
        return searchDataInfo;
    }

    @Override
    protected void onPostExecute(SearchDataInfo searchDataInfo) {
        if (isCancelled())
            return;
        if (null != onUpdateDataLisetener)
            onUpdateDataLisetener.setData(searchDataInfo);
        super.onPostExecute(searchDataInfo);
    }


}
