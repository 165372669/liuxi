package com.android.lucy.treasure.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.android.lucy.treasure.activity.BookContentActivity;
import com.android.lucy.treasure.bean.BookCatalogInfo;
import com.android.lucy.treasure.bean.ChapterPagerContentInfo;
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
    private ArrayList<BookCatalogInfo> bookCatalogInfos;
    private ChapterViewPager viewPager;
    private int currentChapterId = 0;
    private int pagerPosition;
    private boolean isLeftPager;
    private int unreadCount;

    public BookContentPagerAdapter(List<ContentPager> contentPagers, ArrayList<BookCatalogInfo> bookCatalogInfos, ChapterViewPager viewPager,
                                   BookContentActivity activity) {
        this.contentPagers = contentPagers;
        this.bookCatalogInfos = bookCatalogInfos;
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
        if (null != bookCatalogInfos && bookCatalogInfos.size() > 0) {
            int chapterTotal= bookCatalogInfos.size();
            BookCatalogInfo bookCatalogInfo = bookCatalogInfos.get(currentChapterId);
            ArrayList<ChapterPagerContentInfo> chapterPagerContentInfos = bookCatalogInfo.getStrs();
            int tempCurrentItem = viewPager.getCurrentItem();
            int temp = position - tempCurrentItem;
            //MyLogcat.myLog("添加前：,pagerPosition:" + pagerPosition );
            if (isLeftPager) {
                if (bookCatalogInfo.getChapterPagerToatal() != 0) {
                    pagerPosition = chapterPagerContentInfos.size() - 1;
                    isLeftPager = false;
                }
            }
            if (viewPager.getIsDown()) {
                pagerPosition += temp;
                //向右翻页
                if (temp > 0) {
                    if (bookCatalogInfo.getChapterPagerToatal() != 0) {
                        activity.getCircleProgress().setVisibility(View.INVISIBLE);
                        //当前章节有内容
                        //页面位置等于章节的大小，说明翻到下一章了。
                        if (pagerPosition == chapterPagerContentInfos.size()) {
                            currentChapterId++;
                            pagerPosition = 0;
                            bookCatalogInfo = bookCatalogInfos.get(currentChapterId);
                            chapterPagerContentInfos = bookCatalogInfo.getStrs();
                        }
                    } else {
                        contentPager = currentPagerEmpty(temp, position, bookCatalogInfo);
                        if (temp > 0 && pagerPosition > 0) {
                            pagerPosition = 0;
                        }
                    }
                    //向左翻页
                } else if (temp < 0) {
                    if (bookCatalogInfo.getChapterPagerToatal() != 0) {
                        activity.getCircleProgress().setVisibility(View.INVISIBLE);
                        //当前章节有内容
                        if (pagerPosition == -1 && currentChapterId != 0 && !isLeftPager) {
                            currentChapterId--;
                            pagerPosition = -99;
                            bookCatalogInfo = bookCatalogInfos.get(currentChapterId);
                            chapterPagerContentInfos = bookCatalogInfo.getStrs();
                            if (bookCatalogInfo.getChapterPagerToatal() != 0) {
                                pagerPosition = chapterPagerContentInfos.size() - 1;
                            } else {
                                isLeftPager = true;
                            }
                        }
                        //获取当前页面
                        contentPager = getCurrentContetnPager(temp, position);
                        //页面内容不对,页面id不等于当前id，页面的章节名不等于当前章节名
                        if (bookCatalogInfo.getChapterPagerToatal() != 0 && pagerPosition == chapterPagerContentInfos.size() - 1
                                && contentPager.getCurrentPager() - 1 != pagerPosition
                                && !contentPager.getChapterName().equals(bookCatalogInfo.getChapterName())) {
                            MyLogcat.myLog("页面id:" + (contentPager.getCurrentPager() - 1) + ",当前id:" + pagerPosition +
                                    ",页面章节名:" + contentPager.getChapterName() + ",章节名：" + bookCatalogInfo.getChapterName());
                            ChapterPagerContentInfo chapterPagerContentInfo = chapterPagerContentInfos.get(pagerPosition);
                            if (null != chapterPagerContentInfo) {
                                contentPager.setChapterName(bookCatalogInfo.getChapterName());
                                contentPager.setPagerTotal(bookCatalogInfo.getChapterPagerToatal());
                                contentPager.setCurrentPager(chapterPagerContentInfo.getCurrentPager());
                                contentPager.setPagerContent(chapterPagerContentInfo.getPagerContentTextInfos());
                                contentPager.setUnreadChapterCount(bookCatalogInfos.size(), currentChapterId + 1);
                            }
                        }
                        //恢复到页面
                        contentPager = contentPagers.get(position % contentPagers.size());
                    } else {
                        contentPager = currentPagerEmpty(temp, position, bookCatalogInfo);
                        if (temp < 0 && pagerPosition < 0 && currentChapterId != 0 && !isLeftPager) {
                            currentChapterId--;
                            bookCatalogInfo = bookCatalogInfos.get(currentChapterId);
                            chapterPagerContentInfos = bookCatalogInfo.getStrs();
                            if (bookCatalogInfo.getChapterPagerToatal() != 0) {
                                pagerPosition = chapterPagerContentInfos.size() - 1;
                            } else {
                                isLeftPager = true;
                            }
                        }
                    }

                }
                if (null == chapterPagerContentInfos || bookCatalogInfo.getChapterPagerToatal() == 0) {
                    contentPager = currentPagerEmpty(temp, position, bookCatalogInfo);
                }
                viewPager.setIsDown(false);
            }
            //MyLogcat.myLog("点击后：,pagerPosition:" + pagerPosition + ",章节id：" + currentChapterId + ",章节总页面：" + bookCatalogInfo.getChapterPagerToatal());
            unreadCount = currentChapterId;
            //预加载
            if (null != chapterPagerContentInfos && chapterPagerContentInfos.size() > 0 && bookCatalogInfo.getChapterPagerToatal() != 0) {
                ChapterPagerContentInfo chapterPagerContentInfo = null;
                if (pagerPosition >= 0) {
                    if (pagerPosition > chapterPagerContentInfos.size() - 1) {
                        pagerPosition = chapterPagerContentInfos.size() - 1;
                    }
                    //10 < 12 - 1
                    if (pagerPosition < chapterPagerContentInfos.size() - 1) {
                        if (temp > 0) {
                            chapterPagerContentInfo = chapterPagerContentInfos.get(pagerPosition + 1);
                        } else if (pagerPosition != 0 && temp < 0) {
                            chapterPagerContentInfo = chapterPagerContentInfos.get(pagerPosition - 1);
                        }
                    }
                    if (temp == 0) {
                        chapterPagerContentInfo = chapterPagerContentInfos.get(pagerPosition);
                    }

                    //11= 12 - 1 ，章节的最后一页，预加载下一章的第一页。
                    if (pagerPosition == chapterPagerContentInfos.size() - 1 && chapterPagerContentInfos.size() > 1) {
                        if (currentChapterId < chapterTotal - 1 && temp > 0) {
                            bookCatalogInfo = bookCatalogInfos.get(currentChapterId + 1);
                            chapterPagerContentInfos = bookCatalogInfo.getStrs();
                            if (bookCatalogInfo.getChapterPagerToatal() != 0) {
                                chapterPagerContentInfo = chapterPagerContentInfos.get(0);
                                unreadCount++;
                            }
                        } else if (temp < 0) {
                            chapterPagerContentInfo = chapterPagerContentInfos.get(pagerPosition - 1);
                        }
                    }
                    // 0 , 章节的第一页，预加载上一章的最后一页。
                    if (pagerPosition == 0 && temp < 0 && currentChapterId != 0) {
                        bookCatalogInfo = bookCatalogInfos.get(currentChapterId - 1);
                        chapterPagerContentInfos = bookCatalogInfo.getStrs();
                        if (bookCatalogInfo.getChapterPagerToatal() != 0) {
                            chapterPagerContentInfo = chapterPagerContentInfos.get(chapterPagerContentInfos.size() - 1);
                            unreadCount--;
                        }
                    }

                    if (null != chapterPagerContentInfo) {
                        contentPager.setChapterName(bookCatalogInfo.getChapterName());
                        contentPager.setPagerTotal(bookCatalogInfo.getChapterPagerToatal());
                        contentPager.setCurrentPager(chapterPagerContentInfo.getCurrentPager());
                        contentPager.setPagerContent(chapterPagerContentInfo.getPagerContentTextInfos());
                        contentPager.setUnreadChapterCount(bookCatalogInfos.size(), unreadCount + 1);
                    }
                }
            } else {
                activity.loadChapter(currentChapterId, true);
            }
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
     * 获取当前页面
     *
     * @param temp     滑动方向
     * @param position 页面posiotn
     * @return ContentPager 页面
     */
    private ContentPager getCurrentContetnPager(int temp, int position) {
        int pager = position % contentPagers.size();

        if (temp > 0) {
            if (pager == 0) {
                pager = 4;
            } else {
                pager--;
            }
        } else if (temp < 0) {
            if (pager == 4) {
                pager = 0;
            } else {
                pager++;
            }
        }
        //获取到当前显示的页面
        return contentPagers.get(pager);
    }

    /**
     * 清空当前页面内容
     *
     * @param temp        滑动方向
     * @param position    页面坐标
     * @param bookCatalogInfo 章节对象
     * @return ContentPager
     */
    private ContentPager currentPagerEmpty(int temp, int position, BookCatalogInfo bookCatalogInfo) {
        //获取到当前显示的页面
        ContentPager contentPager = getCurrentContetnPager(temp, position);
        pagerContentEmpty(contentPager, bookCatalogInfo);
        return contentPagers.get(position % contentPagers.size());
    }

    /**
     * 清空页面内容
     *
     * @param contentPager 需要清空的页面
     * @param bookCatalogInfo  章节对象
     */
    private void pagerContentEmpty(ContentPager contentPager, BookCatalogInfo bookCatalogInfo) {
        contentPager.setChapterName(bookCatalogInfo.getChapterName());
        contentPager.setPagerTotal(1);
        contentPager.setCurrentPager(1);
        contentPager.setUnreadChapterCount(0, 0);
        contentPager.pagerContentInvali(true);

    }


    /**
     * 跳转到章节
     *
     * @param chapterId 要跳转到的章节id
     */
    public void setCurrentChapterId(int chapterId, int pagerPosition) {
        this.currentChapterId = chapterId;
        int position = viewPager.getCurrentItem();
        int pager = position % contentPagers.size();
        //获取到当前显示的页面
        ContentPager contentPager = contentPagers.get(pager);
        BookCatalogInfo bookCatalogInfo = bookCatalogInfos.get(currentChapterId);
        pagerContentEmpty(contentPager, bookCatalogInfo);
        int chapterPagerTotal = bookCatalogInfo.getChapterPagerToatal();
        if (chapterPagerTotal != 0 && pagerPosition > chapterPagerTotal) {
            this.pagerPosition = 0;
        } else {
            this.pagerPosition = pagerPosition;
        }
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
