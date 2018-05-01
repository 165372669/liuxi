package com.android.lucy.treasure.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.android.lucy.treasure.bean.PagerContentInfo;
import com.android.lucy.treasure.pager.ContentPager;
import com.android.lucy.treasure.utils.MyLogcat;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义章节内容的ViewPager
 */

public class ChapterContentViewPager extends ViewGroup {

    private List<ContentPager> contentPagers;
    private GestureDetector mGestureDetector;
    private int index;
    private Scroller mScroller;
    private int startX;
    private long lastDownTime;
    private boolean reversal;
    private boolean isNextPager;
    private LinkedList<PagerContentInfo> datas;
    private int dataIndex;

    public ChapterContentViewPager(Context context) {
        this(context, null, 0);
    }

    public ChapterContentViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChapterContentViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mScroller = new Scroller(getContext());
        mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (dataIndex == 0 && distanceX < 0)
                    return super.onScroll(e1, e2, distanceX, distanceY);
                scrollBy((int) distanceX, 0);//scrollBy会自动调用invalidate方法,invalidate自动调用onDraw.
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    /*
    * 设置页面内容
    * */
    public void setContentPagers(List<ContentPager> contentPagers) {
        this.contentPagers = contentPagers;
        for (int i = 0; i < contentPagers.size(); i++) {
            ContentPager contentPager = contentPagers.get(i);
            View view = contentPager.getmRootView();
            this.addView(view);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 要求父容器一定要测量子容器,如果不测量子容器宽和高都是0,子容器由于挂载到父容器可以正常显示,但是孙子就不能显示
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //MyLogcat.myLog("viewpager---onMeasure");
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //MyLogcat.myLog("viewpager---onLayout");
        if (null != contentPagers) {
            int width = getWidth();
            for (int i = 0; i < contentPagers.size(); i++) {
                this.getChildAt(i).layout(i * width, t, (i + 1) * width, b);
            }
        }
    }

    public void initChapterContetn(PagerContentInfo pagerContentInfo) {
        ContentPager contentPager = contentPagers.get(0);
    }

    private void setChapterContent(int dataIndex, ContentPager contentPager) {
        if (null != datas && dataIndex < datas.size()) {
            PagerContentInfo pagerContentInfo = datas.get(dataIndex);
            contentPager.setPagerTotal(pagerContentInfo.getChapterPagerToatal());
        }
    }

    /**
     * 设置章节数据集合。
     *
     * @param datas 章节每页数据
     */
    public void setDatas(LinkedList<PagerContentInfo> datas) {
        this.datas = datas;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 当手指按下的时候 记录开始的坐标
                startX = (int) event.getX();
                lastDownTime = event.getDownTime();
                break;
            case MotionEvent.ACTION_UP:
                // 当手指抬起的时候 记录结束的坐标
                int endX = (int) event.getX();
                int moveX = startX - endX;
                long eventTime = event.getEventTime();
                long moveTime = eventTime - lastDownTime;
                //MyLogcat.myLog("moveTime：" + moveTime);
                if ((moveTime > 10 && moveX > 30) || moveX > getWidth() / 3) {
                    //进入下一个界面
                    index++;
                    dataIndex++;
                } else if ((moveTime > 10 && Math.abs(moveX) > 30) || Math.abs(moveX) > getWidth() / 3) {
                    // 进入上一个界面
                    index--;
                    dataIndex--;
                }
                moveToIndex();
                break;
            default:
                break;

        }
        return true;
    }

    /*
    * 根据手势滑动的距离，确定页面跳转
    * */
    private void moveToIndex() {
        if (index < 0) {
            index = 0;
        }
        if (index == getChildCount()) {
            index = getChildCount() - 1;
        }
        if (mOnPageChangedListener != null) {
            mOnPageChangedListener.onChange(index);
        }
        ContentPager contentPager = contentPagers.get(index);
        setChapterContent(dataIndex, contentPager);
        MyLogcat.myLog("dataIndex:" + dataIndex + ",size:" + datas.size() + ",index:" + index);
        if (dataIndex < datas.size()) {
            //绝对滑动，直接滑到指定的x值
            mScroller.startScroll(getScrollX(), getScrollY(), getWidth() * index - getScrollX(), 0);
            isNextPager = true;
            invalidate();
        }
    }

    //计算移动   每次刷新界面 该方法都会被调用
    //scroller.computeScrollOffset()   返回值是true 情况下  代表动作没有结束
    @Override
    public void computeScroll() {
        if (mScroller != null) {
            if (mScroller.computeScrollOffset()) {
                scrollTo(mScroller.getCurrX(), 0);  //scrollTo这个方法一执行 会调用invalidate();,异步执行
                postInvalidate();
            } else {
                MyLogcat.myLog("computeScroll");
                if (dataIndex < datas.size() && isNextPager) {
                    if (index == 4) {
                        index = 1;
                        ContentPager contentPager = contentPagers.get(index);
                        setChapterContent(dataIndex + 1, contentPager);
                        scrollTo(index * getWidth(), 0);
                    } else if (index == 0) {
                        index = 3;
                        ContentPager contentPager = contentPagers.get(index);
                        setChapterContent(dataIndex + 1, contentPager);
                        scrollTo(index * getWidth(), 0);
                    }
                    isNextPager = false;
                }
            }
        }
        super.computeScroll();
    }

    // 定义一个公开接口，设置回调方法
    public interface OnPageChangedListener {
        void onChange(int index);
    }

    private OnPageChangedListener mOnPageChangedListener;

    //定义一个公开的注册页面改变的方法
    public void setOnPageChangedListener(OnPageChangedListener listener) {
        mOnPageChangedListener = listener;
    }
}
