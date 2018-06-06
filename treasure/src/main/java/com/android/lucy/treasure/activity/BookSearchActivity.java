package com.android.lucy.treasure.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.adapter.SearchDataAdapter;
import com.android.lucy.treasure.adapter.SearchHistoryAdapter;
import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.base.BaseReadAsyncTask;
import com.android.lucy.treasure.bean.SearchInfo;
import com.android.lucy.treasure.manager.AsyncManager;
import com.android.lucy.treasure.runnable.async.BookSearchAsync;
import com.android.lucy.treasure.runnable.file.ReadSearchHistoryThread;
import com.android.lucy.treasure.runnable.file.WriteSearchHistoryThread;
import com.android.lucy.treasure.utils.ImageLruCache;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.SDCardHelper;
import com.android.lucy.treasure.utils.URLUtils;

import java.io.File;
import java.util.LinkedList;

/**
 * 搜索页面
 */

public class BookSearchActivity extends Activity implements BaseReadAsyncTask.OnUpdateUIListener, AdapterView.OnItemClickListener {

    private EditText et_search_content;
    private ListView lv_search;
    private BookSearchAsync sdThread;
    private FrameLayout fl_search_list;
    private ProgressBar progressBar;
    private View rl_search_history;
    private ListView lv_search_history;
    private LinkedList<String> historys;
    private SearchHistoryAdapter searchHistoryAdapter;


    static class SearchHandler extends MyHandler<BookSearchActivity> {
        private BookSearchActivity bookSearchActivity;

        SearchHandler(BookSearchActivity bookSearchActivity) {
            super(bookSearchActivity);
            this.bookSearchActivity = bookSearchActivity;
        }

        @Override
        public void myHandleMessage(Message msg) {

            bookSearchActivity.historys = (LinkedList<String>) msg.obj;
            bookSearchActivity.searchHistoryAdapter = new SearchHistoryAdapter(bookSearchActivity, bookSearchActivity.historys,
                    R.layout.search_history_list_item);
            bookSearchActivity.lv_search_history.setAdapter(bookSearchActivity.searchHistoryAdapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initData();
        initEvent();
    }

    /*
     *
     * 初始化控件
     * */
    private void initView() {
        fl_search_list = findViewById(R.id.fl_search_list);

        //增加自定义滚动
        FrameLayout.LayoutParams prigress_layoutParams = new FrameLayout.LayoutParams(80, 80);
        progressBar = new ProgressBar(this);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar_custom));
        prigress_layoutParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(prigress_layoutParams);
        fl_search_list.addView(progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        lv_search = findViewById(R.id.lv_search);
        lv_search.setVisibility(View.INVISIBLE);
        et_search_content = findViewById(R.id.et_seatch_content);
        rl_search_history = findViewById(R.id.rl_search_history);
        lv_search_history = findViewById(R.id.lv_search_history);
        lv_search_history.setDividerHeight(0);//ListView去掉分割线
    }

    /*
     *
     * 初始化数据
     * */
    private void initData() {
        //读取历史搜索数据
        new Thread(new ReadSearchHistoryThread(new SearchHandler(this))).start();
    }

    /*
     *
     * 初始化监听事件
     * */
    private void initEvent() {
        //EditView设置软键盘搜索监听
        et_search_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        lv_search_history.setOnItemClickListener(this);
        lv_search.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyLogcat.myLog("view:" + parent.getId() + ",id:" + id);
        switch (parent.getId()) {
            case R.id.lv_search_history:
                String historyName = historys.get(position);
                if (!historyName.isEmpty()) {
                    et_search_content.setText(historyName);
                    et_search_content.setSelection(historyName.length());//光标移到文字末尾
                    rl_search_history.setVisibility(View.INVISIBLE);
                    sdThread = new BookSearchAsync(lv_search);
                    sdThread.execute(URLUtils.ZHUISHU_SEARCH_URL + historyName);
                    sdThread.setOnUpdateUIListener(this);
                    progressBar.setVisibility(View.VISIBLE);
                    updateSearchHistory(historyName);
                }
                keyboardBack();
                break;
            case R.id.lv_search:
                SearchInfo searchInfo = ((SearchDataAdapter) lv_search.getAdapter()).getDatas(position);
                Context context = MyApplication.getContext();
                Intent intent = new Intent(context, BookIntroducedActivity.class);
                intent.putExtra("searchInfo", searchInfo);
                startActivity(intent);
                break;
        }
    }

    public void iv_click(View v) {
        switch (v.getId()) {
            case R.id.iv_search_back:
                finish();
                break;
            case R.id.iv_serach:
                search();
                break;
            case R.id.tv_history_clear:
                clearHistory();
                break;
        }
    }


    /**
     * 开启线程搜索小说
     */
    private void search() {
        //获取搜索关键字
        String searchBookName = et_search_content.getText().toString().trim();
        if (!searchBookName.isEmpty()) {
            rl_search_history.setVisibility(View.INVISIBLE);
            updateSearchHistory(searchBookName);
            sdThread = new BookSearchAsync(lv_search);
            sdThread.execute(URLUtils.ZHUISHU_SEARCH_URL + searchBookName);
            sdThread.setOnUpdateUIListener(this);
            lv_search.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        keyboardBack();
    }

    /*
     * 清空历史搜索数据
     * */
    private void clearHistory() {
        if (!SDCardHelper.isSDCardMounted())
            return;
        if (null != historys && historys.size() > 0) {
            String filesDir = SDCardHelper.getSDCardPrivateFilesDir(MyApplication.getContext(), "history");
            File historyFile = new File(filesDir + File.separator + "search_history");
            SDCardHelper.removeFileFromSDCard(historyFile);
            historys.clear();
            searchHistoryAdapter.notifyDataSetChanged();
        }
    }

    /*
     * 更新历史搜索数据
     * */
    public void updateSearchHistory(String searchBookName) {
        if (null == historys)
            historys = new LinkedList<>();
        if (searchBookName.length() > 0) {

            if (historys.size() > 0) {
                if (historys.contains(searchBookName)) {
                    int index = historys.indexOf(searchBookName);
                    historys.remove(index);
                }
            }
            historys.addFirst(searchBookName);
            if (historys.size() > 6)
                historys.removeLast();
            new Thread(new WriteSearchHistoryThread(historys)).start();
            if (null != searchHistoryAdapter)
                searchHistoryAdapter.notifyDataSetChanged();
        }
    }

    /*
     * 软键盘收回
     * */
    public void keyboardBack() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != inputMethodManager)
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MyLogcat.myLog("Search-onResume被调用");
    }

    //恢复时首先调用
    @Override
    protected void onRestart() {
        super.onRestart();
//        MyLogcat.myLog("Search-onRestart被调用");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        MyLogcat.myLog("Search-onStop被调用");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MyLogcat.myLog("Search-onPause被调用");
    }

    /*
     * 设置进度条隐藏，ListView显示
     * */
    @Override
    public void setVisibility() {
        progressBar.setVisibility(View.INVISIBLE);
        lv_search.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLogcat.myLog("Search-onDestroy被调用");
        //退出时取消搜索任务
        if (null != sdThread)
            sdThread.cancel(true);
        //取消下载图片任务
        AsyncManager.getInstance().cancelTask("BookImageAsync");
        //清空缓存
        ImageLruCache.getInstance().clearCache();

        AsyncManager.getInstance().cancelTask("ZhuiShuDataAsync");
    }
}
