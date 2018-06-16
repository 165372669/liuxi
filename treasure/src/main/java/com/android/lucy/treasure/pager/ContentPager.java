package com.android.lucy.treasure.pager;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.bean.PagerContentTextInfo;
import com.android.lucy.treasure.view.ChapterContentTextView;

import java.util.List;

/**
 * 自定义内容界面View
 */

public class ContentPager {

    private Activity mActivity;
    private View mRootView;
    private ChapterContentTextView tv_chapter_content;
    private TextView tv_chapter_name;
    private TextView tv_chapter_time;
    private TextView tv_chapter_bookName;
    private TextView tv_chapter_pagerTotal;
    private TextView tv_chapter_pager;
    private TextView tv_chapter_unread;


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
        tv_chapter_unread = mRootView.findViewById(R.id.tv_chapter_unread);
    }


    private void initEvent() {

    }

    public View getmRootView() {
        return mRootView;
    }

    /**
     * 设置章节名称
     *
     * @param chapterName 章节名称
     */
    public void setChapterName(String chapterName) {
        tv_chapter_name.setText(chapterName);
    }

    /**
     * 获取章节名称
     *
     * @return
     */
    public String getChapterName() {
        return tv_chapter_name.getText().toString();
    }

    /**
     * 设置章节页面总数
     *
     * @param total 章节页面总数
     */
    public void setPagerTotal(int total) {
        tv_chapter_pagerTotal.setText(String.valueOf(total));
    }


    /**
     * 设置页面内容字体大小
     *
     * @param mTextSize 字体大小
     */
    public void setTextSize(int mTextSize) {
        tv_chapter_content.setTextSize(mTextSize);
    }

    /**
     * 设置章节页面内容显示
     *
     * @param pagerContentTextInfos 章节页面内容
     */
    public void setPagerContent(List<PagerContentTextInfo> pagerContentTextInfos) {
        tv_chapter_content.setContentArrays(pagerContentTextInfos);
    }

    /**
     * 设置本章的当前页面数。
     *
     * @param currentPager 当前页面数
     */
    public void setCurrentPager(int currentPager) {
        tv_chapter_pager.setText(String.valueOf(currentPager));
    }

    /**
     * 设置未读章节数
     *
     * @param chapterTotal     总章节数
     * @param readChapterCount 已读章节数
     */
    public void setUnreadChapterCount(int chapterTotal, int readChapterCount) {
        if (chapterTotal == 0) {
            tv_chapter_unread.setText("");
            tv_chapter_bookName.setText("");
        } else {
            tv_chapter_unread.setText(readChapterCount + "/" + chapterTotal);
            tv_chapter_bookName.setText((chapterTotal - readChapterCount) + "章未读");
        }
    }

    public int getCurrentPager() {
        String pager = tv_chapter_pager.getText().toString();
        if (!pager.isEmpty()) {
            return Integer.parseInt(pager);
        }
        return -1;
    }

    /**
     * 页面内容重绘
     */
    public void pagerContentInvali(boolean isInvalidate) {
        tv_chapter_content.contentInvali(isInvalidate);
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
