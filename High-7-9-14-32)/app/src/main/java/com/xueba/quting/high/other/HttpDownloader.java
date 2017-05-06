package com.xueba.quting.high.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloader extends Thread{
    private URL url = null;
    private String urlStr = null;
    private Handler handler = null;

    public HttpDownloader()
    {
        super();
    }

    public HttpDownloader(String urlStr, Handler handler){

        this.urlStr = urlStr;
        this.handler = handler;
        //System.out.println("url->"+this.urlStr);
    }


    /**
     * based on URL download file, the file is text file
     * 1.create a URL object
     * 2.using URL, create a HttpURLConnection object
     * 3.get InputStram
     * 4.read from InputStream
     * @param
     * @return
     */
    public Bitmap download() {
        if (urlStr == null){
            return null;
        }
        URL myFileUrl=null;
        Bitmap bitmap = null;
      //  System.out.print("你好好--->"+bitmap);

        try {
            myFileUrl = new URL(urlStr);
        //    System.out.println("url------>"+myFileUrl);
        }
        catch (IOException e) {
            e.printStackTrace();
            //Log.v(TAG, "getbitmap bmp fail---");
            return null;
        }

        InputStream is=null;
        try{
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
        }finally {
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
      Bitmap result = download();
        //System.out.println("read from URL<"+result+">");
        if (handler != null){
            Message msg = handler.obtainMessage();
            msg.obj = result;
            msg.what=1;
            handler.sendMessage(msg);
        }
    }
}