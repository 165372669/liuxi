package com.android.lucy.treasure.runnable.file;

import com.android.lucy.treasure.MyApp;
import com.android.lucy.treasure.utils.SDCardHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 历史搜索数据保存到文件线程
 */

public class WriteSearchHistoryThread implements Runnable {

    private final List<String> historys;

    public WriteSearchHistoryThread(List<String> historys) {
        this.historys = historys;
    }

    @Override
    public void run() {
        if (!SDCardHelper.isSDCardMounted())
            return;
        String filesDir = SDCardHelper.getSDCardPrivateFilesDir(MyApp.getContext(), "history");
        File historyFile = new File(filesDir + File.separator + "search_history");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(historyFile));
            for (String history : historys) {
                bw.write(history);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bw)
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
