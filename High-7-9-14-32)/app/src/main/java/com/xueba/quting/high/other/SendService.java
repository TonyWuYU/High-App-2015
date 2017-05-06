package com.xueba.quting.high.other;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.wanglei.qq.bean.QQinfor;
import com.xueba.quting.high.Thread.SendGet;

public class SendService extends Service {
   private QQinfor qqinfor=new QQinfor();
    // private Handler handler;
    private Handler sa_Handler = new Handler() {
        //处理消息队列中的消息
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==2)
            {
                QQinfor qQinforGet=(QQinfor)msg.obj;
                System.out.println("开始跳转");
                //数据返回
                //backto();

            }
        }

    };
    public SendService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //取出发送过来的QQinfor，和pid

        qqinfor = (QQinfor)intent.getSerializableExtra("QQinfor");
        int pid=intent.getIntExtra("pid",0);
        SendGet sendGet=new SendGet(qqinfor,sa_Handler,pid);
        sendGet.start();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
