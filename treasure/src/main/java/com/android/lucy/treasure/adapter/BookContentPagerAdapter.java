package com.android.lucy.treasure.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.android.lucy.treasure.activity.BookContentActivity;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.PagerContentInfo;
import com.android.lucy.treasure.pager.ContentPager;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.ThreadPool;
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
    private int chapterTotal;
    private int currentChapterId = 0;
    private int pagerPosition;
    private boolean isLeftPager;

    public BookContentPagerAdapter(List<ContentPager> contentPagers, ArrayList<CatalogInfo> catalogInfos, ChapterViewPager viewPager,
                                   BookContentActivity activity, int chapterTotal) {
        this.contentPagers = contentPagers;
        this.catalogInfos = catalogInfos;
        this.viewPager = viewPager;
        this.activity = activity;
        this.chapterTotal = chapterTotal;
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
        //MyLogcat.myLog("添加前：,pagerPosition:" + pagerPosition );
        if (isLeftPager) {
            if (null != pagerContentInfos) {
                pagerPosition = pagerContentInfos.size() - 1;
            }
            isLeftPager = false;
        }
        if (viewPager.getIsDown()) {
            pagerPosition += temp;
            //向右翻页
            if (temp > 0) {
                //页面位置等于章节的大小，说明翻到下一章了。章节的总页数等于0，说明刚才的一章没有下载完，继续翻下一章节。
                if (null != pagerContentInfos && pagerPosition == pagerContentInfos.size() && pagerContentInfos.size() > 0 ||
                        catalogInfo.getChapterPagerToatal() == 0 && pagerPosition == 1) {
                    //取消前面的下载任务
                    ThreadPool.getInstance().removeTask("chapter-" + currentChapterId);
                    currentChapterId++;
                    pagerPosition = 0;
                    catalogInfo = catalogInfos.get(currentChapterId);
                    pagerContentInfos = catalogInfo.getStrs();
                    int pager = position % contentPagers.size();
                    if (pager == 0) {
                        pager = 4;
                    } else {
                        pager--;
                    }
                    //获取到当前显示的页面
                    contentPager = contentPagers.get(pager);
                    contentPager.pagerContentInvali(true);
                    contentPager.setChapterName(catalogInfo.getChapterName());
                    contentPager.setPagerTotal(1);
                    contentPager.setCurrentPager(1);
                    //恢复下一页面
                    contentPager = contentPagers.get(position % contentPagers.size());
                }
            }
            if (temp < 0) {
                if (null != pagerContentInfos && pagerContentInfos.size() > 0 && pagerPosition == -1 && currentChapterId != 0) {
                    //取消前面的下载任务
                    ThreadPool.getInstance().removeTask("chapter-" + currentChapterId);
                    currentChapterId--;
                    catalogInfo = catalogInfos.get(currentChapterId);
                    pagerContentInfos = catalogInfo.getStrs();
                    if (null != pagerContentInfos) {
                        pagerPosition = pagerContentInfos.size() - 1;
                    } else {
                        int pager = position % contentPagers.size();
                        if (pager == 4) {
                            pager = 0;
                        } else {
                            pager++;
                        }
                        //获取到当前显示的页面
                        contentPager = contentPagers.get(pager);
                        contentPager.pagerContentInvali(true);
                        contentPager.setChapterName(catalogInfo.getChapterName());
                        contentPager.setPagerTotal(1);
                        contentPager.setCurrentPager(1);
                        //恢复下一页面
                        contentPager = contentPagers.get(position % contentPagers.size());
                        isLeftPager = true;
                    }
                }
            }
            viewPager.setIsDown(false);
            //MyLogcat.myLog("点击后：,pagerPosition:" + pagerPosition + ",temp:" + temp + ",章节id：" + currentChapterId);
        }
        MyLogcat.myLog("添加后：,pagerPosition:" + pagerPosition + ",temp:" + temp + ",章节id：" + currentChapterId);
        if (null != pagerContentInfos && pagerContentInfos.size() > 0) {
            PagerContentInfo pagerContentInfo = null;
            if (pagerPosition >= 0) {
                //10 < 12 - 1
                if (pagerPosition < pagerContentInfos.size() - 1) {
                    if (temp > 0) {
                        pagerContentInfo = pagerContentInfos.get(pagerPosition + 1);
                    } else if (pagerPosition != 0 && temp < 0) {
                        pagerContentInfo = pagerContentInfos.get(pagerPosition - 1);
                    }
                }
                if (temp == 0) {
                    pagerContentInfo = pagerContentInfos.get(pagerPosition);
                }

                //11= 12 - 1 ，向右翻动，章节的最后一章了。
                if (pagerPosition == pagerContentInfos.size() - 1 && pagerContentInfos.size() > 1) {
                    if (currentChapterId < chapterTotal - 1 && temp > 0) {
                        pagerContentInfos = catalogInfos.get(currentChapterId + 1).getStrs();
                        if (null != pagerContentInfos && pagerContentInfos.size() > 0) {
                            pagerContentInfo = pagerContentInfos.get(0);
                        }
                    } else if (temp < 0) {
                        pagerContentInfo = pagerContentInfos.get(pagerPosition - 1);
                    }
                }
                // 0,章节的第一页，并且是往左翻。
                if (pagerPosition == 0 && temp < 0 && currentChapterId != 0) {
                    pagerContentInfos = catalogInfos.get(currentChapterId - 1).getStrs();
                    if (null != pagerContentInfos && pagerContentInfos.size() > 0) {
                        pagerContentInfo = pagerContentInfos.get(pagerContentInfos.size() - 1);
                    }
                }

                if (null != pagerContentInfo) {
                    contentPager.setChapterName(catalogInfo.getChapterName());
                    contentPager.setPagerTotal(catalogInfo.getChapterPagerToatal());
                    contentPager.setCurrentPager(pagerContentInfo.getCurrentPager());
                    contentPager.setPagerContent(pagerContentInfo.getTextInfos());
                }
            }
        } else {
            activity.loadChapter(currentChapterId + 1, true);
        }
        View view = contentPager.getmRootView();
        if (null != view.getParent()) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            viewGroup.removeView(view);

        }
        container.addView(view);
        return view;
    }


    /**
     * 跳转到章节
     *
     * @param chapterId 章节id
     */
    public void setCurrentChapterId(int chapterId) {
        this.currentChapterId = chapterId;
        pagerPosition = 0;
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
}
