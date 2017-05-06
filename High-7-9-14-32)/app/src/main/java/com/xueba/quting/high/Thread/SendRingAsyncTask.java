package com.xueba.quting.high.Thread;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wanglei.qq.bean.QQinfor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by wanglei on 2015/7/2.
 */
public class SendRingAsyncTask extends AsyncTask<Integer, Integer, String> {

    private TextView textView = null;
    private ProgressBar progressBar = null;
    private  QQinfor qQinfor=null;
    private PopupWindow popupWindow=null;
    private Handler handler=null;
    //通过构造函数传递参数
    public SendRingAsyncTask(TextView textView,ProgressBar progressBar,QQinfor qQinfor,PopupWindow popupWindow,Handler handler) {
        this.textView = textView;
        this.progressBar = progressBar;
        this.qQinfor=qQinfor;
        this.popupWindow=popupWindow;
        this.handler=handler;
    }

    //该方法并不运行在UI线程当中，所以在该方法当中，不能对UI当中的控件进行设置和修改
    //主要用于进行异步操作。
    @Override
    protected String doInBackground(Integer... param) {
        //创建一个Socket
        Socket client=null;
        ObjectOutputStream oos=null;
        ObjectInputStream ois=null;
        try {

            String host = "182.254.222.104";  //要连接的服务端IP地址
            int port = 8899;   //要连接的服务端对应的监听端口
            //与服务端建立连接

            client = new Socket(host, port);
            //建立连接后就可以往服务端写数据了
            //建立输出流
            oos = new ObjectOutputStream(client.getOutputStream());
            //输入对象一定要flush

            System.out.println("qqinformation------>" + qQinfor);
            oos.writeObject(qQinfor);
            System.out.println("发送完毕------>" );
            oos.flush();
            ois = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
            System.out.println("开始接收数据从服务器------>");
            try{
                Object obj=ois.readObject();
                System.out.println("读取obj ------>" +obj);
                if (obj != null) {
                    QQinfor qqinfor_get = (QQinfor)obj;

                    System.out.println("user: " + qqinfor_get.getCity());
                    //将接收到的的QQinfor对象传送给主线程
                    if (handler != null) {
                        Message msg = handler.obtainMessage();
                        msg.obj = qqinfor_get;
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                    // textView.setText(user.getCity());
                }
            }
            catch (Exception e){

            }

        } catch(IOException ex) {
            //logger.log(Level.SEVERE, null, ex);
        }

        finally {
            try {
                ois.close();
            } catch(Exception ex) {}
            try {
                oos.close();
            } catch(Exception ex) {}
            try {
                client.close();
            } catch(Exception ex) {}
        }
        String result ="Success";
        return result;
    }

    //在doInBackground方法执行结束之后再运行，并且运行在UI线程当中。
    //主要用于将异步任务执行的结果展示给客户
    @Override
    protected void onPostExecute(String result) {
        textView.setText("异步操作执行结束" + result);
        //设置转动的进度条不可见
        progressBar.setVisibility(View.GONE);
        //关闭popupWindow，使用户可以操作activity
        popupWindow.dismiss();
    }

    //该方法运行在UI线程当中,主要用于进行异步操作之前的UI准备工作
    @Override
    protected void onPreExecute() {
        textView.setText("开始执行异步操作");
        //设置转动的进度条可见
        progressBar.setVisibility(View.VISIBLE);
        //使用popupWindow覆盖屏幕，使用户在上传时不能操作activity
        popupWindow.showAtLocation(progressBar, Gravity.CENTER, 0, 0);

    }

    //在doInBackground方法当中，每次调用publishProgress()方法之后，都会触发该方法
    //用于在异步任务执行的过程当中，对用户进行提示，例如控制进度条等
    @Override
    protected void onProgressUpdate(Integer... values) {
       /* int value = values[0];
        progressBar.setProgress(value);*/
    }


}
