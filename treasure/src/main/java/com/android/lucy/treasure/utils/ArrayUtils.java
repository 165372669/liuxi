package com.android.lucy.treasure.utils;

import com.android.lucy.treasure.bean.BookInfo;
import com.android.lucy.treasure.bean.CatalogInfo;
import com.android.lucy.treasure.bean.SourceInfo;

import java.util.List;

/**
 * 数组List工具类
 */

public class ArrayUtils {

    /**
     * 获取指定SourceName在集合里的位置
     *
     * @param sourceInfos 查找集合
     * @param sourceName  查找字符串
     * @return 失败返回-1
     */
    public static int getArrayIndex(List<SourceInfo> sourceInfos, String sourceName) {
        for (int i = 0; i < sourceInfos.size(); i++) {
            SourceInfo sourceInfo = sourceInfos.get(i);
            if (sourceInfo.getSourceName().equals(sourceName)) {
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
            bookInfo.setSourceName(bookInfo.getSourceInfos().get(0).getSourceName());
            return 0;
        }
        int sourceIndex = ArrayUtils.getArrayIndex(bookInfo.getSourceInfos(), sourceName);
        if (sourceIndex == -1) {
            sourceIndex = 0;
        }
        return sourceIndex;
    }
}
