package com.android.lucy.treasure.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.android.lucy.treasure.bean.PagerContentInfo;
import com.android.lucy.treasure.pager.ContentPager;
import com.android.lucy.treasure.view.ChapterViewPager;

import java.util.LinkedList;
import java.util.List;

/**
 * 小说章节内容ViewPager适配器
 */

public class BookContentPagerAdapter<T> extends PagerAdapter {
    private List<ContentPager> contentPagers;
    private LinkedList<PagerContentInfo> pagerContentInfos;
    private ChapterViewPager viewPager;
    private int currentPosition;

    public BookContentPagerAdapter(List<ContentPager> contentPagers, LinkedList<PagerContentInfo> pagerContentInfos, ChapterViewPager viewPager) {
        this.contentPagers = contentPagers;
        this.pagerContentInfos = pagerContentInfos;
        this.viewPager = viewPager;
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
        if (pagerContentInfos.size() > 0) {
            int tempCurrentItem = viewPager.getCurrentItem();
            int temp = position - tempCurrentItem;
            //MyLogcat.myLog("instantiateItem----" + "position:" + position + ",viewPager.getCurrentItem():" + viewPager.getCurrentItem());
            currentPosition += temp;
            if (currentPosition >= 0 && currentPosition < pagerContentInfos.size() - 1) {
                PagerContentInfo pagerContentInfo = pagerContentInfos.get(currentPosition + 1);
                if (temp == 0)
                    pagerContentInfo = pagerContentInfos.get(currentPosition);
                if (currentPosition != 0 && position < tempCurrentItem) {
                    pagerContentInfo = pagerContentInfos.get(currentPosition - 1);
                }
                contentPager.setChapterName(pagerContentInfo.getChapterName());
                contentPager.setPagerTotal(pagerContentInfo.getChapterPagerToatal());
                contentPager.setCurrentPager(pagerContentInfo.getCurrentPager());
                contentPager.setPagerContent(pagerContentInfo.getTextInfos());
            }
            if (currentPosition > pagerContentInfos.size() - 1) {
                currentPosition = pagerContentInfos.size() - 1;
            }
            if (currentPosition >= 0)
                viewPager.setCurrentPagerPosition(currentPosition);
            //MyLogcat.myLog("adapter----" + "currentPosition:" + currentPosition);
        }
        View view = contentPager.getmRootView();
        if (null != view.getParent()) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            viewGroup.removeView(view);

        }
        container.addView(view);
        //MyLogcat.myLog("instantiateItem---" + "position:" + position + ",i:" + i);
        //MyLogcat.myLog("instantiateItem---" + "i:" + i + ",size:" + dates.size() + ",position:" + position);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public int getPagerPosition() {
        return this.currentPosition;
    }


}
