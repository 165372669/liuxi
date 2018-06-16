package com.android.lucy.treasure.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */

public class StringUtils {

    /*
    *判断是否是字母或者数字，是返回true，否返回false
     */
    public static boolean isNumOrLetters(String str) {
        String regEx = "^[A-Za-z0-9_]+$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /*
    *String数组转换为字符串
     */
    public static String arrayToString(String[] strs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            sb.append(strs[i]);
        }
        return sb.toString();
    }

    /**
     * 截取来源名称
     *
     * @param sourceName 来源网址
     * @return
     */
    public static String shearSourceName(String sourceName) {
        if (sourceName.startsWith("http")) {
            int start = sourceName.indexOf("//") + 2;
            int end = sourceName.indexOf("/", start);
            return sourceName.substring(start, end);
        } else
            return sourceName.substring(0, sourceName.indexOf("/"));
    }

    public static String splitString(String s, String split, int index) {
        return s.split(split)[index];
    }

}
