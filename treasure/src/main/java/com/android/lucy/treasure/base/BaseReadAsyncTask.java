package com.android.lucy.treasure.base;

import android.os.AsyncTask;

import com.android.lucy.treasure.bean.SearchDataInfo;
import com.android.lucy.treasure.manager.AsyncManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * 获取网页数据AsyncTask基类
 */

public abstract class BaseReadAsyncTask<T> extends AsyncTask<String, Object, T> {

    protected OnUpdateDataLisetener onUpdateDataLisetener;
    protected OnUpdateUIListener onUpdateUIListener;

    public interface OnUpdateDataLisetener {
        void setData(SearchDataInfo searchDataInfo);
    }

    public interface OnUpdateUIListener {
        void setVisibility();
    }

    public abstract T resoloveUrl(Document doc);


    public T readUrl(String url) {
        T t = null;
        if (url.isEmpty())
            return null;
        Document doc = null;
        try {

            doc = Jsoup.connect(url).get();

        } catch (IOException e) {

            System.out.println(getClass().getName() + "读取失败");
        }
        if (null != doc) {
            t = resoloveUrl(doc);
        }
        return t;
    }

    /*
 * 执行了execute()方法后就会在UI线程上执行onPreExecute()方法
 * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AsyncManager.getInstance().addTask(this);
    }
    /*
    * 在onPreExecute()方法执行完成后立即执行
    * */

    @Override
    protected T doInBackground(String... strings) {
        if (strings.length > 0) {
            T t = null;
            for (String url : strings) {
                t = readUrl(url);
                //如果AsyncTask被调用了cancel()方法，那么任务取消，跳出for循环
                if (isCancelled()) {
                    break;
                }
            }
            return t;
        }
        return null;
    }

    /*
    *当doInBackgroud方法执行完毕后，表示任务完成了
    * */
    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        AsyncManager.getInstance().deleteTask(this);
    }

    /*
    * 设置接口
    * */
    public void setOnUpdateDataListener(OnUpdateDataLisetener onUpdateDataLisetener) {
        this.onUpdateDataLisetener = onUpdateDataLisetener;
    }

    public void setOnUpdateUIListener(OnUpdateUIListener onUpdateUIListener) {
        this.onUpdateUIListener = onUpdateUIListener;
    }
}
