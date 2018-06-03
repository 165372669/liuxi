package com.android.lucy.treasure.runnable.file;

import android.os.Environment;

import com.android.lucy.treasure.application.MyApplication;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.SDCardHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 写出数据库文件到本地
 */

public class WriteDataFileThread implements Runnable {

    private final String dbName;

    public WriteDataFileThread(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public void run() {
        File dbFile = new File(Environment.getDataDirectory().getAbsolutePath()
                + "/data/com.android.lucy.androidapp/databases/" + dbName);
        String filesDir = SDCardHelper.getSDCardPrivateFilesDir(MyApplication.getContext(), "db");
        File historyFile = new File(filesDir + File.separator + dbName);
        MyLogcat.myLog("historyFile:" + historyFile.getPath());
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(dbFile);
            fos = new FileOutputStream(historyFile);
            int len;
            byte[] buffer = new byte[2048];
            while (-1 != (len = fis.read(buffer))) {
                fos.write(buffer, 0, len);
            }
            fos.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭数据流
            try {
                if (fos != null) fos.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
