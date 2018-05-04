package com.android.lucy.treasure.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.android.lucy.treasure.activity.BookContentActivity;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.PagerContentInfo;
import com.android.lucy.treasure.pager.ContentPager;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.view.ChapterViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 小说章节内容ViewPager适配器
 */

public class BookContentPagerAdapter extends PagerAdapter {
    private BookContentActivity activity;
    private List<ContentPager> contentPagers;
    private ArrayList<CatalogInfo> catalogInfos;
    private ChapterViewPager viewPager;
    private int currentChapterId = 0;
    private int pagerPosition;

    public BookContentPagerAdapter(List<ContentPager> contentPagers, ArrayList<CatalogInfo> catalogInfos, ChapterViewPager viewPager,
                                   BookContentActivity activity) {
        this.contentPagers = contentPagers;
        this.catalogInfos = catalogInfos;
        this.viewPager = viewPager;
        this.activity = activity;
    }

    /*
    * 返回要滑动的VIew的个数
    * */
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /*
    * 从当前container中删除指定位置（position）的View;
    * */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /*
    * 1.将当前视图添加到container中
    * 2.返回当前View
    * */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ContentPager contentPager = contentPagers.get(position % contentPagers.size());
        CatalogInfo catalogInfo = catalogInfos.get(currentChapterId);
        ArrayList<PagerContentInfo> pagerContentInfos = catalogInfo.getStrs();
        int tempCurrentItem = viewPager.getCurrentItem();
        int temp = position - tempCurrentItem;
        MyLogcat.myLog("计算之前:" + pagerPosition + "，现在的位置：" + position + "，之前的位置：" + tempCurrentItem);
        pagerPosition += temp;
        MyLogcat.myLog("计算之前的位置:" + pagerPosition + ",当前章节id:" + currentChapterId + ",temp:" + temp);
        if (null != pagerContentInfos && pagerContentInfos.size() > 0) {
            if (temp > 0 && pagerPosition == pagerContentInfos.size() - 1) {
                currentChapterId++;
                pagerPosition = 0;
                catalogInfo = catalogInfos.get(currentChapterId);
                pagerContentInfos = catalogInfo.getStrs();
            }
            PagerContentInfo pagerContentInfo = null;
            if (pagerPosition >= 0) {
                //10 < 12 - 1
                if (pagerPosition < pagerContentInfos.size() - 1) {
                    pagerContentInfo = pagerContentInfos.get(pagerPosition + 1);
                    MyLogcat.myLog("10 < 12 - 1:" + (pagerPosition + 1) + ",当前章节id:" + currentChapterId + ",集合大小：" + pagerContentInfos.size());
                }
                //11= 12 - 1
                if (pagerPosition == pagerContentInfos.size() - 1) {
                    catalogInfo = catalogInfos.get(currentChapterId + 1);
                    pagerContentInfos = catalogInfo.getStrs();
                    if (null == pagerContentInfos) {
                        activity.loadChapter(currentChapterId + 2, false);
                    }
                    MyLogcat.myLog("11=12-1:" + pagerPosition + ",当前章节id:" + currentChapterId + ",集合大小：" + pagerContentInfos.size());
                }
                if (temp == 0)
                    pagerContentInfo = pagerContentInfos.get(pagerPosition);
                if (pagerPosition != 0 && position < tempCurrentItem) {
                    pagerContentInfo = pagerContentInfos.get(pagerPosition - 1);
                }
                if (null != pagerContentInfo) {
                    contentPager.setChapterName(catalogInfo.getChapterName());
                    contentPager.setPagerTotal(catalogInfo.getChapterPagerToatal());
                    contentPager.setCurrentPager(pagerContentInfo.getCurrentPager());
                    contentPager.setPagerContent(pagerContentInfo.getTextInfos());
                }
            }
            if (pagerPosition >= 0)
                viewPager.setCurrentPagerPosition(pagerPosition);
        }

        View view = contentPager.getmRootView();
        if (null != view.getParent()) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            viewGroup.removeView(view);

        }
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public int getPagerPosition() {
        return this.pagerPosition;
    }


    public int getCurrentChapterId() {
        return this.currentChapterId;
    }

    /**
     * 是否有下一章节
     *
     * @return
     */
    public boolean isHaveNextChapter() {
        CatalogInfo catalogInfo = catalogInfos.get(currentChapterId + 1);
        ArrayList<PagerContentInfo> pagerContentInfos = catalogInfo.getStrs();
        if (null != pagerContentInfos && pagerContentInfos.size() > 0) {
            return true;
        }
        return false;
    }
}
