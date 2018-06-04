package com.android.lucy.treasure.utils;

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
}
