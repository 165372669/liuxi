package com.android.lucy.treasure.runnable.file;

import android.os.Message;

import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.SDCardHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * 历史搜索数据读取线程
 */

public class ReadSearchHistoryThread implements Runnable {

    private final MyHandler mHandler;

    public ReadSearchHistoryThread(MyHandler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void run() {
        if (!SDCardHelper.isSDCardMounted())
            return;
        String filesDir = SDCardHelper.getSDCardPrivateFilesDir(MyApplication.getContext(), "history");
        File historyFile = new File(filesDir + File.separator + "search_history");
        if (!SDCardHelper.isFileExists(historyFile))
            return;
        BufferedReader br = null;
        LinkedList<String> historys = new LinkedList<>();
        try {
            br = new BufferedReader(new FileReader(historyFile));
            String line;
            while ((line = br.readLine()) != null) {

                historys.add(line);
            }
            Message message = Message.obtain();
            message.obj = historys;
            mHandler.sendMessage(message);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
