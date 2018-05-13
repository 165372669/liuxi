package com.android.lucy.treasure.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.android.lucy.treasure.R;
import com.android.lucy.treasure.bean.TextInfo;

import java.util.List;

/**
 * 重写TextView,显示章节内容。
 * 功能：
 * 根据字体大小分页
 */

public class ChapterContentTextView extends View {

    private Paint textPaint;
    private List<TextInfo> books; //章节内容
    private boolean isInvalidate;

    public ChapterContentTextView(Context context) {
        this(context, null, 0);
    }

    public ChapterContentTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChapterContentTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    /*
    * 初始化View
    * */
    private void initView(AttributeSet attrs) {
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.MyTextView);
        int mTextColor = arr.getColor(R.styleable.MyTextView_color, Color.BLACK);
        int mTextSize = (int) arr.getDimension(R.styleable.MyTextView_text_size, 18);
        arr.recycle();//回收资源
        textPaint = new TextPaint();
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(mTextColor);
        textPaint.setAntiAlias(true);
        //textPaint.setFakeBoldText(true);//设置粗体
    }

    /*
    * 测量控件，告诉父控件需要的宽高
    * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //保存测量宽度和测量高度
        //MyLogcat.myLog("onMeasure---" + "width:" + width + ",height" + height);
    }


    /*
        * 绘画方法
        * */
    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInvalidate && null != books && books.size() > 0) {
            for (int i = 0; i < books.size(); i++) {
                TextInfo textInfo = books.get(i);
                canvas.drawText(textInfo.getS(), textInfo.getX(), textInfo.getY(), textPaint);
            }
        } else {
            canvas.drawText("", 0, 0, textPaint);
            isInvalidate = false;
        }
    }


    public void setTextSize(int mTextSize) {
        textPaint.setTextSize(mTextSize);
    }

    /**
     * 清空内容
     */
    public void contentInvali(boolean isInvalidate) {
        this.isInvalidate = isInvalidate;
        invalidateView();
    }


    /*
    * 设置字符串数组
    * */
    public void setContentArrays(List<TextInfo> strs) {
        isInvalidate = false;
        this.books = strs;
        invalidateView();
    }


    /**
     * 重绘
     */
    public void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }


}
