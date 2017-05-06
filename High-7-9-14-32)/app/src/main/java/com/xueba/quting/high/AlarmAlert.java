package com.xueba.quting.high;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.xueba.quting.high.Thread.SendGet;
import com.example.wanglei.qq.bean.Alarminfor;
import com.example.wanglei.qq.bean.QQinfor;
import com.example.wanglei.qq.bean.Ring;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AlarmAlert extends ActionBarActivity {
    //用于接收服务器返回的QQinfor对象
    private QQinfor qqinforGet=new QQinfor();
    //用于向服务器发送闹铃请求
    private QQinfor qqinforSend=new QQinfor();
    private Handler arHandler = new Handler() {
        //处理消息队列中的消息
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==2)
            {
                qqinforGet=(QQinfor)msg.obj;
                //获取传送过来的录音文件及录音信息
                Toast.makeText(AlarmAlert.this, "接收"+qqinforGet.getpid(), Toast.LENGTH_SHORT).show();
                System.out.println("开始"+qqinforGet.getpid());
                Ring ring_get=new Ring();
                ring_get=qqinforGet.getRings_list().get(0);
                byte[] ring =new byte[1024];
                ring=ring_get.getRing();
                if(ring==null){
                    Toast.makeText(AlarmAlert.this, "没有数据", Toast.LENGTH_SHORT).show();
                }
                //将ring写到定义的路径中
                System.out.println("开始写数据1111");
                //创建文件夹
                createSDCardDir();
                File sdcardDir = Environment.getExternalStorageDirectory();
                String path=sdcardDir.getPath()+"/Wakeup/user/get_rings";
                String rpath=path+"/"+qqinforGet.getpid()+".amr";
                FileOutputStream output= null;
                try {
                    output = new FileOutputStream(rpath);
                    output.write(ring,0,ring.length);
                    output.close();
                    System.out.println("写进去了");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //获取写进去的文件并播放
                MediaPlayer mediaPlayer= MediaPlayer.create(AlarmAlert.this, Uri.parse(rpath));
                //有文件路径创建MediaPlayer对象
                //不循环播放
                mediaPlayer.setLooping(false);
                //开始播放
                mediaPlayer.start();
                //isPlaying=true;
                //FileOutputStream output=new FileOutputStream("c:\\"+rid+".amr");



            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AlertDialog.Builder(AlarmAlert.this).
                setTitle("闹钟").//设置标题
                setMessage("时间到了！").//设置内容
                setPositiveButton("知道了", new DialogInterface.OnClickListener(){//设置按钮
            public void onClick(DialogInterface dialog, int which) {
                AlarmAlert.this.finish();//关闭Activity
            }
        }).create().show();
        qqinforSend = new QQinfor();
        //获取SharedPreferences存取的uid

        SharedPreferences mySharedPreferences= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        String  user_id=mySharedPreferences.getString("uid","");
        //接收传递过来的count
        Intent intent=getIntent();
        int count =intent.getIntExtra("count",0);
        //处理count
        while(count>1000){
            count=count-1000;
        }
        int sendday=intent.getIntExtra("day",0);

        String aid=user_id+"-"+sendday+"-"+count;

        Toast.makeText(AlarmAlert.this, "发送的day"+sendday, Toast.LENGTH_SHORT).show();
        //将System.currentTimeMillis()转换成calendar
        Date date=new Date(System.currentTimeMillis());
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        int day=calendar.get(Calendar.DAY_OF_WEEK);

        //将周一到周日转化成1-7
        int send_day=0;
        if(day==1){
            send_day=7;
        }else{
            send_day=day-1;
        }
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        String time=time_form(hour,minute);
        //将闹钟的各项信息封装到Alarminfor对象中去
        Alarminfor alarminfor=new Alarminfor();
        alarminfor.setAid(aid);
        alarminfor.setUid(user_id);
        alarminfor.setDay(send_day);
        alarminfor.setTime(time);
        ArrayList<Alarminfor> alarminfors=new ArrayList<Alarminfor>();
        alarminfors.add(alarminfor);
        qqinforSend.setAinfor_list(alarminfors);
        qqinforSend.setUid(user_id);
        System.out.println("发送的aid--->"+alarminfor.getAid());
        //向服务器发送闹铃请求，标志为20
        SendGet sendGet=new SendGet(qqinforSend,arHandler,20);
        //开启发送线程

        sendGet.start();
    }
    //将小时和时间连接成一个字符串
    public String time_form(int hour,int minute){
        String nhour,nminute;

        if(hour<10){
            nhour="0"+ hour;
        }else{
            nhour=""+ hour;
        }
        if(minute<10){
            nminute="0"+minute;
        }else{
            nminute=""+minute;
        }
        return nhour+":"+nminute;
    }

    //在SD卡上创建一个文件夹
    public void createSDCardDir(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir =Environment.getExternalStorageDirectory();
            //得到一个路径，内容是sdcard的文件夹路径和名字
            String path=sdcardDir.getPath()+"/Wakeup/user/get_rings";
            File file = new File(path);
            if (!file.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                file.mkdirs();
                setTitle("paht ok,path:"+path);
            }
        }
        else{
            setTitle("false");
            return;

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_alert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
