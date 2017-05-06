package com.xueba.quting.high.other;

import com.example.wanglei.qq.bean.QQinfor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by wanglei on 2015/6/28.
 */
public class ClientThread extends Thread {

    Socket socket = null;
    QQinfor qQinfor=null;
    public ClientThread(Socket socket,QQinfor qQinfor){
        this.socket = socket;

        this.qQinfor=qQinfor;
    }
    @Override
    public void run() {
        ObjectOutputStream oos=null;
        ObjectInputStream ois=null;
        InputStream in = null;
        System.out.println("Begin to Chat to server...");
        try {
            // //建立输出流
            oos = new ObjectOutputStream(socket.getOutputStream());
            //建立输入流
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            //循环发送与服务端不停的交互数据
           /* while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                doWrite(oos);
                //System.out.println("begin read message from server.");
              doRead(ois);

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try {
                in.close();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 读取服务端数据
     * @param //in
     * @return
     */
    public boolean doRead(ObjectInputStream ois) throws IOException {
        //引用关系，不要在此处关闭流
        String uid,uname,ugender,ucity,uchara;
        int unumf;
        Object obj;
        try {
            obj = ois.readObject();
            if (obj != null) {
                QQinfor qqinfor_get = (QQinfor)obj;//把接收到的对象转化为user
                //ois.flush()
                System.out.print("get_infor--->"+qqinfor_get.getCity());
            }


        }
        catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return true;
    }

    /**
     * 发送数据到服务端
     * @param out
     * @return
     */
    public boolean doWrite(ObjectOutputStream out){
        //引用关系，不要在此处关闭流

        try {
            //System.out.println("qqinformation------>" + qqinfor);
            out.writeObject(qQinfor);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
