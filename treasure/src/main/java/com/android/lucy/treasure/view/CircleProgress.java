package com.android.lucy.treasure.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.android.lucy.treasure.R;

/**
 * 自定义圆形进度条
 */

public class CircleProgress extends View {

    // 圆形颜色
    private int mCircleColor;
    // 圆环颜色
    private int mRingColor;
    // 当前进度
    private int mProgress;
    private float roundWidth;
    private Paint mPaint;
    private int mCenterX;
    private int mCenterY;
    private float mRadius;
    private RectF mRectF;

    public CircleProgress(Context context) {
        super(context);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 获取自定义的属性
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CircleProgress, 0, 0);
        // 进度条及背景圆形的宽度
        roundWidth = typeArray.getDimension(R.styleable.CircleProgress_roundWidth, 10);
        //圆形颜色
        mCircleColor = typeArray.getColor(R.styleable.CircleProgress_bgColor, 0xFFFFFFFF);
        //圆环颜色
        mRingColor = typeArray.getColor(R.styleable.CircleProgress_circleColor, 0xFFFFFFFF);
        typeArray.recycle();
        initVariable();
    }

    private void initVariable() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 当layout大小变化后会回调次方法
     * 通过这方法获取宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;//控宽的中心点
        mCenterY = h / 2;//控件高的中心点
        //防止宽高不一致
        int min = Math.min(mCenterX, mCenterY);
        //半径
        mRadius = min - roundWidth / 2;
        //为画圆弧准备
        mRectF = new RectF(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = widthSize / 2;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = heightSize / 2;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //1、先画背景圆环
        mPaint.setColor(mCircleColor);
        mPaint.setStrokeWidth(roundWidth);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        //2、画动态圆弧
        mPaint.setColor(mRingColor);
        canvas.drawArc(mRectF, 0, (float) (3.6 * mProgress), false, mPaint);
    }

    //设置进度
    public void setProgress(int progress) {
        if (progress > mProgress) {
            int tempProgress = progress - mProgress;
            for (int i = 0; i < tempProgress; i++) {
                mProgress += i;
                postInvalidate();//重绘
            }
        } else {
            mProgress = progress;
        }
    }


    /**
     * 获取进度
     *
     * @return
     */
    public int getProgress() {
        return this.mProgress;
    }
}
