package com.android.lucy.treasure.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.adapter.SearchDataAdapter;
import com.android.lucy.treasure.adapter.SearchHistoryAdapter;
import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.SearchInfo;
import com.android.lucy.treasure.manager.AsyncManager;
import com.android.lucy.treasure.manager.ImageDownloadManager;
import com.android.lucy.treasure.runnable.file.ReadSearchHistoryThread;
import com.android.lucy.treasure.runnable.file.WriteDataFileThread;
import com.android.lucy.treasure.runnable.file.WriteSearchHistoryThread;
import com.android.lucy.treasure.runnable.search.BiqiugeSearchThread;
import com.android.lucy.treasure.utils.BitmapUtils;
import com.android.lucy.treasure.utils.ImageLruCache;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.SDCardHelper;
import com.android.lucy.treasure.utils.ThreadPool;
import com.android.lucy.treasure.utils.URLUtils;
import com.android.lucy.treasure.view.SearchEditTextView;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.LinkedList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * 搜索页面
 */

public class BookSearchActivity extends Activity implements AdapterView.OnItemClickListener {

    private SearchEditTextView et_search_content;
    private ListView lv_search;
    private FrameLayout fl_search_list;
    private ProgressBar progressBar;
    private View rl_search_history;
    private ListView lv_search_history;
    private LinkedList<String> historys;
    private SearchHistoryAdapter searchHistoryAdapter;
    private SearchHandler searchHandler;
    private LinkedList<SearchInfo> searchInfos;


    static class SearchHandler extends MyHandler<BookSearchActivity> {
        private BookSearchActivity activity;

        SearchHandler(BookSearchActivity bookSearchActivity) {
            super(bookSearchActivity);
            this.activity = bookSearchActivity;
        }

        @Override
        public void myHandleMessage(Message msg) {

            switch (msg.arg1) {
                case MyHandler.SEARCH_HISTORYS_OK:
                    //获取历史搜索数据
                    activity.historys = (LinkedList<String>) msg.obj;
                    activity.searchHistoryAdapter = new SearchHistoryAdapter(activity, activity.historys,
                            R.layout.search_history_list_item);
                    activity.lv_search_history.setAdapter(activity.searchHistoryAdapter);
                    break;
                case MyHandler.SEARCH_HISTORYS_NOTFOUND:
                    MyLogcat.myLog("找不到文件");
                    break;
                case MyHandler.SEARCH_HISTORYS_ERROR:
                    MyLogcat.myLog("连接错误");
                    break;
                //搜索书籍成功
                case MyHandler.SEARCH_DETAILS_OK:
                    //加载图片
                    ImageDownloadManager imageDownloadManager = new ImageDownloadManager(activity.lv_search);
                    SearchDataAdapter searchDataAdapter = new SearchDataAdapter(MyApplication.getContext(), activity.searchInfos,
                            R.layout.search_list_item, imageDownloadManager);
                    activity.lv_search.setAdapter(searchDataAdapter);
                    activity.lv_search.setOnScrollListener(searchDataAdapter);
                    activity.progressBar.setVisibility(INVISIBLE);
                    activity.lv_search.setVisibility(VISIBLE);
                    break;
                case MyHandler.SEARCH_DETAILS_NO:
                    MyLogcat.myLog("未搜索到书籍");
                    break;
                case MyHandler.SEARCH_DETAILS_ERROR:
                    MyLogcat.myLog("搜索错误");
                    break;
            }
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
        progressBar.setVisibility(INVISIBLE);

        lv_search = findViewById(R.id.lv_search);
        lv_search.setVisibility(INVISIBLE);
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
        searchInfos = new LinkedList<>();
        //进入时不弹出键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //读取历史搜索数据
        searchHandler = new SearchHandler(this);
        ThreadPool.getInstance().submitTask(new ReadSearchHistoryThread(searchHandler));
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
                    //获取搜索关键字
                    String searchBookName = et_search_content.getText().toString().trim();
                    search(searchBookName);
                    return true;
                }
                return false;
            }
        });

        //EditView内容发生改变监听
        et_search_content.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //编辑框的内容发生改变之前的回调方法
            }

            /**
             * 编辑框的内容正在发生改变时的回调方法 >>用户正在输入
             * 我们可以在这里实时地 通过搜索匹配用户的输入
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
                //编辑框的内容改变以后,用户没有继续输入时 的回调方法
                if (s.length() == 0) {
                    lv_search.setVisibility(INVISIBLE);
                    rl_search_history.setVisibility(VISIBLE);
                }
            }
        });
        lv_search_history.setOnItemClickListener(this);
        lv_search.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_search_history:
                String historyName = historys.get(position);
                if (!historyName.isEmpty()) {
                    et_search_content.setText(historyName);
                    et_search_content.setSelection(historyName.length());//光标移到文字末尾
                    rl_search_history.setVisibility(INVISIBLE);
                    search(historyName);
                    progressBar.setVisibility(VISIBLE);
                    updateSearchHistory(historyName);
                }
                keyboardBack();
                break;
            case R.id.lv_search:
                SearchInfo searchInfo = ((SearchDataAdapter) lv_search.getAdapter()).getDatas(position);
                BookInfo bookInfo = DataSupport.where("bookName=?", searchInfo.getBookName()).findFirst(BookInfo.class, true);
                if (null == bookInfo) {
                    //创建小说对象
                    bookInfo = new BookInfo(searchInfo.getBookName(), searchInfo.getAuthor(), searchInfo.getImgUrl(),
                            searchInfo.getType(), searchInfo.getDesc(), searchInfo.getNewChapter(), searchInfo.getDatailsUrl());
                }
                ThreadPool.getInstance().submitTask(new WriteDataFileThread("bookData.db"));
                Intent intent = new Intent(this, BookIntroducedActivity.class);
                intent.putExtra("bookInfo", bookInfo);
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
                search(et_search_content.getText().toString().trim());
                break;
            case R.id.tv_history_clear:
                clearHistory();
                break;
        }
    }


    /**
     * 开启线程搜索小说
     */
    private void search(String searchBookName) {
        if (!searchBookName.isEmpty()) {
            searchInfos.clear();
            rl_search_history.setVisibility(INVISIBLE);
            //更新搜索数据
            updateSearchHistory(searchBookName);
            String searchUrl = URLUtils.URL_BIQIUGE_SEARCH + searchBookName;
            ThreadPool.getInstance().submitTask(new BiqiugeSearchThread(searchUrl, searchBookName,
                    searchInfos, searchHandler));
            lv_search.setVisibility(INVISIBLE);
            progressBar.setVisibility(VISIBLE);
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

//    /*
//     * 设置进度条隐藏，ListView显示
//     * */
//    @Override
//    public void setVisibility() {
//        progressBar.setVisibility(INVISIBLE);
//        lv_search.setVisibility(VISIBLE);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLogcat.myLog("Search-onDestroy被调用");
        //退出时取消搜索任务
//        if (null != sdThread)
//            sdThread.cancel(true);
        //取消下载图片任务
        AsyncManager.getInstance().cancelTask("BookImageAsync");
        //清空缓存
        ImageLruCache.getInstance().clearCache();

        AsyncManager.getInstance().cancelTask("ZhuiShuDataAsync");
    }
}
