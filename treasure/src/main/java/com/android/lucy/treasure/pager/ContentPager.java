package com.android.lucy.treasure.pager;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.bean.TextInfo;
import com.android.lucy.treasure.view.BookContentTextView;

import java.util.List;

/**
 * 自定义内容界面View
 */

public class ContentPager {

    private Activity mActivity;
    private View mRootView;
    private BookContentTextView tv_chapter_content;
    private TextView tv_chapter_name;
    private TextView tv_chapter_time;
    private TextView tv_chapter_bookName;
    private TextView tv_chapter_pagerTotal;
    private TextView tv_chapter_pager;


    public ContentPager(Activity mActivity) {
        this.mActivity = mActivity;
        initViews();
        initEvent();
    }


    private void initViews() {
        mRootView = View.inflate(mActivity, R.layout.pager_content, null);
        tv_chapter_content = mRootView.findViewById(R.id.tv_chapter_content);
        tv_chapter_name = mRootView.findViewById(R.id.tv_chapter_name);
        tv_chapter_time = mRootView.findViewById(R.id.tv_chapter_time);
        tv_chapter_bookName = mRootView.findViewById(R.id.tv_chapter_bookName);
        tv_chapter_pagerTotal = mRootView.findViewById(R.id.tv_chapter_pagerTotal);
        tv_chapter_pager = mRootView.findViewById(R.id.tv_chapter_pager);
    }


    private void initEvent() {

    }

    public View getmRootView() {
        return mRootView;
    }

    public void setChapterName(String chapterName) {
        tv_chapter_name.setText(chapterName);
    }

    public void setPagerTotal(int total) {
        tv_chapter_pagerTotal.setText(String.valueOf(total));
    }

    public void setBookName(String bookName) {
        tv_chapter_bookName.setText(bookName);
    }

    public void setTextSize(int mTextSize) {
        tv_chapter_content.setTextSize(mTextSize);
    }

    public void setPagerContent(List<TextInfo> textInfos) {
        tv_chapter_content.setContentArrays(textInfos);
    }

    public void setCurrentPager(int currentPager) {

        tv_chapter_pager.setText(currentPager + "/");
    }


    /*
    * 返回章节名称TextView
    * */
    public View getChapterNameView() {
        return tv_chapter_name;
    }

    /*
    * 返回章节内容TextView
    * */
    public View getChapterContentView() {
        return tv_chapter_content;
    }

    /*
* 返回书名TextView
* */
    public View getBookNameView() {
        return tv_chapter_bookName;
    }

}
