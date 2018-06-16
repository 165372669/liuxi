package com.android.lucy.treasure.bean;

/**
 * 文字实体类
 */

public class PagerContentTextInfo {
    private String s;//字体
    private float width;//字宽
    private float x; //要画的x坐标
    private float y; //要画的y坐标
    private boolean isStringOneLine;//是否为段落第一行首字母。


    public PagerContentTextInfo(String s) {
        this.s = s;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }


    public boolean isStringOneLine() {
        return isStringOneLine;
    }

    public void setStringOneLine(boolean stringOneLine) {
        isStringOneLine = stringOneLine;
    }

    @Override
    public String toString() {
        return s;
    }
}
