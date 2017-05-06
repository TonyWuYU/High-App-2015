package com.xueba.quting.high.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import com.example.wanglei.qq.bean.friendsobject;
import com.example.wanglei.qq.bean.squareclientobject;
import com.example.wanglei.qq.bean.squareobject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SquDownloader extends Thread {
    private URL url = null;
    private Handler handler = null;
    private squareclientobject obj = null;

    public SquDownloader() {
        super();
    }

    public SquDownloader(squareclientobject object, Handler handler) {
        this.obj = new squareclientobject();
        this.obj = object;
        this.handler = handler;
        //System.out.println("url->"+this.urlStr);
    }


    /**
     * based on URL download file, the file is text file
     * 1.create a URL object
     * 2.using URL, create a HttpURLConnection object
     * 3.get InputStram
     * 4.read from InputStream
     *
     * @param
     * @return
     */
    public Bitmap download(String urlStr) {
        if (urlStr == null) {
            return null;
        }
        URL myFileUrl = null;
        Bitmap bitmap = null;
        //  System.out.print("你好好--->"+bitmap);

        try {
            myFileUrl = new URL(urlStr);
            //    System.out.println("url------>"+myFileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            //Log.v(TAG, "getbitmap bmp fail---");
            return null;
        }

        InputStream is = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            // System.out.println("bitmap2--->"+bitmap);
            // is.close();

            // Log.v(TAG, "image download finished." + imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            //Log.v(TAG, "getbitmap bmp fail---");
            return null;
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    @Override
    public void run() {
        Bitmap result = download(obj.getLogourl());
        //System.out.println("read from URL<"+result+">");
        obj.setBitmap(result);
        if (handler != null) {
            Message msg = handler.obtainMessage();
            msg.obj = obj;
            msg.what = 4;
            handler.sendMessage(msg);
        }
    }
}
