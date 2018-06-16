package com.android.lucy.treasure.utils;

import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.BookSourceInfo;

import java.util.List;

/**
 * 数组List工具类
 */

public class ArrayUtils {

    /**
     * 获取指定SourceName在集合里的位置
     *
     * @param bookSourceInfos 查找集合
     * @param sourceName  查找字符串
     * @return 失败返回-1
     */
    public static int getArrayIndex(List<BookSourceInfo> bookSourceInfos, String sourceName) {
        for (int i = 0; i < bookSourceInfos.size(); i++) {
            BookSourceInfo bookSourceInfo = bookSourceInfos.get(i);
            if (bookSourceInfo.getSourceName().equals(sourceName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取来源数据在Source集合里面的Index
     *
     * @return 来源index
     */
    public static int getSourceIndex(BookInfo bookInfo) {
        //获取当前来源的index
        String sourceName = bookInfo.getSourceName();
        if (null == sourceName) {
            bookInfo.setSourceName(bookInfo.getBookSourceInfos().get(0).getSourceName());
            return 0;
        }
        int sourceIndex = ArrayUtils.getArrayIndex(bookInfo.getBookSourceInfos(), sourceName);
        if (sourceIndex == -1) {
            sourceIndex = 0;
        }
        return sourceIndex;
    }
}
