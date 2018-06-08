package com.android.lucy.treasure.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.android.lucy.treasure.R;

/**
 * 自定义带清除图标的EditText
 */

public class SearchEditTextView extends android.support.v7.widget.AppCompatEditText implements TextWatcher {

    private Drawable mClearDrawable;

    public SearchEditTextView(Context context) {
        this(context, null, 0);
    }

    public SearchEditTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        //设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //请求获得焦点
        requestFocus();
        //调用系统输入法
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != inputMethodManager)
            inputMethodManager.showSoftInput(this, 0);
        mClearDrawable = getCompoundDrawables()[2];
        if (null == mClearDrawable) {
            mClearDrawable = getResources().getDrawable(R.mipmap.abc_ic_clear_mtrl_alpha);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        setClearIconVisible(false);
    }

    /*
    * 设置图标是否显示
    * */
    public void setClearIconVisible(boolean clearIconVisible) {
        Drawable right = clearIconVisible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /*
    * 点击后清空内容
    * */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /*
    * 当输入框里面内容发生变化的时候回调的方法
    * */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setClearIconVisible(getText().length() > 0);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
