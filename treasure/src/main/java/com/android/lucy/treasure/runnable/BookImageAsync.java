package com.android.lucy.treasure.runnable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.android.lucy.treasure.manager.AsyncManager;
import com.android.lucy.treasure.utils.BitmapUtils;
import com.android.lucy.treasure.utils.ImageLruCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 小说封面下载
 */

public class BookImageAsync extends AsyncTask<String, Object, Bitmap> {


    private ImageView mImageView;
    private ImageLruCache lruCache;
    private String imgUrl;

    public BookImageAsync(ImageView mImageView) {
        this.mImageView = mImageView;
        lruCache = ImageLruCache.getInstance();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        imgUrl = strings[0];
        Bitmap bitmap = downloadImg(imgUrl);
        return bitmap;
    }

    /*
    *当doInBackgroud方法执行完毕后，表示任务完成了
    * */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(isCancelled())
            return;
        if (null != bitmap) {
            lruCache.addBitmapToCache(imgUrl,new BitmapDrawable(bitmap));
            if(null!=mImageView)
            mImageView.setImageDrawable(BitmapUtils.bitmapToDrawable(bitmap));
        }
        super.onPostExecute(bitmap);
    }

    /*
        * 下载图片
        * */
    private Bitmap downloadImg(String imgUrl) {
        HttpURLConnection connection = null;
        try {

            URL url = new URL(imgUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);//设置连接超时
            connection.setReadTimeout(3000);//设置读取超时
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) ");
            connection.setDoOutput(false);
            connection.setDoInput(true);
            //建立连接
            connection.connect();
            //inputStream流只能使用一次
            byte[] buf = readInputStream(connection.getInputStream());
            return BitmapUtils.getBitmap(buf, mImageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != connection)
                connection.disconnect();
        }
        return null;
    }


    /*
    * 读取流返回图片的字节数组
    * */
    private byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buf)) != -1) {
            bos.write(buf, 0, len);
            if(isCancelled())
                break;
        }
        return bos.toByteArray();
    }

}
