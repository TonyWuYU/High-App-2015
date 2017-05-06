 package com.xueba.quting.high.Thread;

import android.os.Handler;
import android.os.Message;

import com.example.wanglei.qq.bean.QQinfor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by wanglei on 2015/6/29.
 */
public class SendGet  extends Thread {
    private QQinfor qQinfor;
    private Handler handler;
    private   int pid;
     public   SendGet(QQinfor qQinfor,Handler handler,int pid) {
        this.qQinfor = qQinfor;
        this.handler=handler;
        this.pid=pid;
    }

    @Override
    public void run() {
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
            qQinfor.setpid(pid);
            System.out.println("qqinformation------>" + qQinfor);
            oos.writeObject(qQinfor);
            System.out.println("发送完毕------>" );
            oos.flush();
            ois = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
            System.out.println("开始接收数据从服务器------>" );
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

        //关闭输入流

        // client.close();

    }
}