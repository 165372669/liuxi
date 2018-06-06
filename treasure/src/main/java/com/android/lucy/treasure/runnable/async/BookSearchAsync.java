package com.android.lucy.treasure.runnable.async;

import android.widget.ListView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.adapter.SearchDataAdapter;
import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.base.BaseReadAsyncTask;
import com.android.lucy.treasure.bean.SearchInfo;
import com.android.lucy.treasure.manager.ImageDownloadManager;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取搜索数据线程
 */

public class BookSearchAsync extends BaseReadAsyncTask<List<SearchInfo>> {


    private ListView lv_search;

    public BookSearchAsync(ListView lv_search) {
        super();
        this.lv_search = lv_search;
    }

    @Override
    public List<SearchInfo> resoloveUrl(Document doc) {
        List<SearchInfo> sdInfos = null;
        if (null != doc) {
            sdInfos = new ArrayList<>();
            Elements divs = doc.select("div[data-href]");
            for (Element div : divs) {
                Elements img = div.select("img[src]");
                String imgSrc = img.attr("src");
                String bookName = img.attr("alt");
                Elements a = div.select("a[href]");
                String bookUrl_ZhuShu = a.get(0).attr("href");
                Elements spans = div.select("span");
                String author = spans.get(0).text();
                String type = spans.get(2).text();
                Elements ps = div.select("p[class^=desc]");
                String desc = ps.get(0).text();
                SearchInfo sdInfo = new SearchInfo(imgSrc, bookName, author, type, desc, bookUrl_ZhuShu);
                sdInfos.add(sdInfo);
                if (isCancelled())
                    break;
            }
        }
        return sdInfos;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /*
    获取搜索数据后放入ListView适配器
    */
    @Override
    protected void onPostExecute(List<SearchInfo> searchInfos) {
        if (isCancelled())
            return;
        //隐藏滚动
        if (null != onUpdateUIListener)
            onUpdateUIListener.setVisibility();
        if (null != searchInfos && searchInfos.size() > 0) {
            ImageDownloadManager imageDownloadManager = new ImageDownloadManager(lv_search, searchInfos);
            SearchDataAdapter searchDataAdapter = new SearchDataAdapter(MyApplication.getContext(), searchInfos, R.layout.search_list_item, imageDownloadManager);
            //设置动画
            searchDataAdapter.setIsAnimation(true, "translationY", 300, 0, 200);
            lv_search.setAdapter(searchDataAdapter);
            lv_search.setOnScrollListener(searchDataAdapter);
        }
        super.onPostExecute(searchInfos);
    }
}
