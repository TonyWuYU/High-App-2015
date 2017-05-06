package com.xueba.quting.high;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.wanglei.qq.bean.QQinfor;
import com.example.wanglei.qq.bean.Ring;
import com.example.wanglei.qq.bean.Rname;
import com.xueba.quting.high.Thread.SendGet;
import com.xueba.quting.high.Thread.SendRingAsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class RingActivity extends ListActivity {
    private ImageView button_record;
    private TextView Rtimer,result;
    //显示上传进度
    private ProgressBar Probar;
    //上传时使用,使用户不能操作activity
    private PopupWindow popupWindow;
    private MediaRecorder recorder;
    private Timer timer;
    //文件夹路径
    private  String path;
    private  String rid;
    private int rcount=0;

    private List<Rname> Rnames=null;
    //录音路径
    private  String Rpath=null;
    //保存用户id
    private String user_id;
    private  QQinfor r_qQinforGet=null;
    //记录点击录音按钮的次数
    private  int  rbt_count=0;
    private MediaPlayer mediaPlayer;
    //标记录音是否是播放状态
    private  boolean isPlaying=false;
    //标记是否是录音状态
    private  boolean isRecording=false;

    // 用来接收服务器返回的信息
    private Handler rHandler = new Handler() {
        //处理消息队列中的消息
        @Override
        public void handleMessage(Message msg) {
            //处理录音倒计时
            if(msg.what==1){
                // Handler处理消息
                if (msg.arg1 > 0) {
                    Rtimer.setText(msg.arg1 + "");
                } else {
                    //停止录音
                    stopRecord();
                    //添加对话框表示录音超时自动停止
                    AlertDialog alert=new AlertDialog.Builder(RingActivity.this).create();
                    alert.setMessage("录音超时，自动停止");
                    alert.setButton(DialogInterface.BUTTON_POSITIVE,"确定",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                }
            }
            //接收来自服务器的QQinfor
            if(msg.what==2)
            {
                r_qQinforGet=(QQinfor)msg.obj;
                System.out.print("GetQQinfor--->"+r_qQinforGet);

            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        //获取SharedPreferences存取的uid
        SharedPreferences mySharedPreferences= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        user_id=mySharedPreferences.getString("uid","");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);

        //获取SharedPreferences存取的uid
        SharedPreferences mySharedPreferences= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        user_id=mySharedPreferences.getString("uid","");

        //创建一个放置录音文件的的文件夹
        createSDCardDir(user_id);
        //每次开启该activity刷新ListView显示录音列表
        refreshList(path);
        //获取传递过来的QQinfor
        Intent intent=getIntent();

        //录音按钮
        button_record = (ImageView) this.findViewById(R.id.bt_record);
        //用来倒计时录音限制时间的TextView
        Rtimer=(TextView)findViewById(R.id.Rtimer);
        //进度条
        Probar=(ProgressBar)findViewById(R.id.Probar);
        result=(TextView)findViewById(R.id.result);
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.layout_popup, null);
        // 初始化PopupWindow对象
        popupWindow=new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, true);

        button_record.setOnClickListener(new AudioListerner());
    }


    class AudioListerner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //通过记录按键次数判断开始录音，还是停止录音
            if (rbt_count%2==0) {
                //开始录音
                startRecord();
                //按录音键次数加1
                rbt_count=rbt_count+1;
                //修改按钮标签
                button_record.setImageResource(R.drawable.recording);
                //设置一个timer倒计时录音限制时间
                // 按钮按下时创建一个Timer定时器
                timer = new Timer();
                // 创建一个TimerTask
                // TimerTask是个抽象类,实现了Runnable接口，所以TimerTask就是一个子线程
                TimerTask timerTask = new TimerTask() {
                    // 倒数10秒
                    int i = 30;
                    @Override
                    public void run() {
                        Log.d("debug", "run方法所在的线程："
                                + Thread.currentThread().getName());
                        // 定义一个消息传过去
                        Message msg = new Message();
                        msg.arg1=i--;
                        //将msg.what标记为1与，SendGet中接收的信息区分开
                        msg.what = 1;

                        rHandler.sendMessage(msg);
                    }
                };
                timer.schedule(timerTask, 0, 1000);// 0秒后开始倒计时，倒计时间隔为1秒
                isRecording=true;

            }

            else {
                if(isRecording){
                    //停止录音
                    stopRecord();

                    isRecording=false;
                }

            }
        }
        //开始录音方法
        public void startRecord() {
            recorder = new MediaRecorder();// new出MediaRecorder对象
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置MediaRecorder的音频源为麦克风
            recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            // 设置MediaRecorder录制的音频格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 设置MediaRecorder录制音频的编码为amr.
            //创建一个放置录音文件的的文件夹
            createSDCardDir(user_id);
            //方便给闹铃标记闹铃
            Rnames=getrecordFiles(path);

            //使用SharedPreferences记录录音个数
            final SharedPreferences mySharedPreferences= getSharedPreferences("ring",
                    Activity.MODE_PRIVATE);

            final SharedPreferences.Editor editor = mySharedPreferences.edit();
            //将Uid放入对应的键值对
            //读取已存闹钟计数
            rcount=mySharedPreferences.getInt("count",0);


            rid=user_id+"-"+rcount;
            int rcount_add=rcount+1;
            editor.putInt("count",rcount_add);
            editor.commit();

            /*Toast.makeText(RingActivity.this, "qqinfor" + ring_qqinfor.getUid(),
                    Toast.LENGTH_LONG).show();*/
            //设置录音存储路径及文件名
            Rpath=path+"/"+rid+".amr";
            recorder.setOutputFile(Rpath);
            // 设置录制好的音频文件保存路径
            try {
                recorder.prepare();// 准备录制
                recorder.start();// 开始录制
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //停止录音的方法
    public void stopRecord(){
        recorder.stop();// 停止刻录
        // recorder.reset(); // 重新启动MediaRecorder.
        recorder.release(); // 刻录完成一定要释放资源
        button_record.setImageResource(R.drawable.startrecord);
        // recorder = null;
        //停止计时器
        timer.cancel();
        //将Rtimer的内容置空
        Rtimer.setText("");
        //将Timer停止
        timer.cancel();
        //录制和发送完毕完毕要刷新列表
        refreshList(path);
        //按录音键次数加1
        rbt_count=rbt_count+1;

        try{
            //录制完毕后获取录音文件，并转换为byte[]
            byte[] rbyte= Ring.getContent(Rpath);
            //设置Ring对象的各个属性
            Ring ring=new Ring();
            ring.setRing_id(rid);
            ring.setUid(user_id);
            ring.setRing(rbyte);
            //将ring添加到 rings列表中
            ArrayList<Ring> rings=new ArrayList<Ring>();
            rings.add(ring);
            //将rings设置到QQinfor对象中
            QQinfor qQinfor_rSend=new QQinfor();
            qQinfor_rSend.setRings_list(rings);
            //其中5用来标记pid，让服务器识别是接收闹铃
            qQinfor_rSend.setpid(5);
            //调用SendGet发送QQinfor对象，其中5用来标记pid，让服务器识别是接收闹铃
           /* SendGet r_sendGet= new  SendGet(qQinfor_rSend,rHandler,5);
            //开启发送线程
            r_sendGet.start();*/
            //参数传入使用SendRingAsyncTask来传送录音
            SendRingAsyncTask sendRingAsyncTask=new SendRingAsyncTask(result,Probar,qQinfor_rSend,popupWindow,rHandler);
            //执行
            sendRingAsyncTask.execute(0);
            //将录音记录增加1

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //在SD卡上创建一个文件夹
    public void createSDCardDir(String uid){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir =Environment.getExternalStorageDirectory();
            //得到一个路径，内容是sdcard的文件夹路径和名字
            path=sdcardDir.getPath()+"/Wakeup/user/alarm/records"+uid;
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

    //读取目录中录音文件的名称
    public List<Rname> getrecordFiles(String path){
        //创建一个 List<Mp3Info>列表
        List<Rname> rnames =new ArrayList<Rname>();
        //由指定路径获取文件夹
        File file = new File(path);
        //获取文件夹下的文件数组
        File[] files= file.listFiles();
        /*取出文件数组的文件并将每个文件名和大小赋值给对应的Mp3Info对象
        * 并添加到 List<Mp3Info>列表中*/
        for(int i=0;i<files.length;i++){

            if(files[i].getName().endsWith("amr")){
                //先去掉文件的后缀名
                String new_name=files[i].getName().substring(0, files[i].getName().lastIndexOf("."));
                System.out.println("count------>"+new_name);
                Rname rname=new Rname();
                //将文件名按“-”拆分
                String []  in_rname= new_name.split("-");
                //拆分的两个部分分别赋值给uid和count
                rname.setUid(in_rname[0]);
                //System.out.println("count------>"+in_rname[1]);
                // rname.setUid("你好");
                rname.setCount(Integer.parseInt(in_rname[1]));
                //将rname对象加入rnames列表
                rnames.add(rname);
            }

        }
        return rnames;
    }

    //创建SimpleAdapter
    private SimpleAdapter buildSimpleAdapter(List<Rname>  rnames) {
        // create a List object,and based on SimpleAdapter standard, put data in mp3Infos to List
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (Iterator iterator = rnames.iterator(); iterator.hasNext(); ) {
            Rname rname = (Rname) iterator.next();
            HashMap<String, String> map = new HashMap<String, String>();

            // map.put("uid", rname.getUid());
            map.put("count", "" + rname.getCount());
            map.put("play_stop", "play");
            map.put("delete", "delete");
            list.add(map);
        }
        // create a SimpleAdapter object
        SimpleAdapter simpleAdapter = new SimpleAdapter(RingActivity.this, list,
                R.layout.layout_rnames, new String[]{"count", "play_stop", "delete"},
                new int[]{R.id.r_name, R.id.play_stop, R.id.r_delete});



        //给listView的会造成焦点冲突 通过这种方式来设置button设置监听器
        SimpleAdapter.ViewBinder binder=new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if (view instanceof Button) {
                    final View button = view;
                    view.setOnClickListener(new View.OnClickListener() {
                        LinearLayout listItem = (LinearLayout) button.getParent();
                        TextView tv_id = (TextView) listItem.findViewById(R.id.r_name);
                        String PR_name=tv_id.getText().toString();
                        //由路径获取文件
                        String sPath=path+"/"+user_id+"-"+PR_name+".amr";
                        //获取要删除文件的rid
                        String delete_rid=user_id+"-"+PR_name;
                        @Override
                        public void onClick(View view) {
                            //设置删除按钮的监听器
                            if(view.getId()==R.id.r_delete){

                                //设置Ring对象的各个属性
                                Ring ring=new Ring();
                                //指定要删除的rid
                                ring.setRing_id(delete_rid);
                                //ring.setRing_id("08C4668703C6DE819DBFC505FF368589-0");
                                //将ring添加到 rings列表中
                                ArrayList<Ring> rings=new ArrayList<Ring>();
                                rings.add(ring);
                                //将rings设置到QQinfor对象中
                                QQinfor qQinfor_rSend=new QQinfor();
                                qQinfor_rSend.setRings_list(rings);
                                //调用SendGet发送QQinfor对象，其中6用来标记pid，让服务器识别是删除闹铃
                                SendGet r_sendGet= new  SendGet(qQinfor_rSend,rHandler,6);
                                //开启发送线程
                                r_sendGet.start();
                                File file=new File(sPath);
                                //删除文件
                                if(file.exists()){file.delete();}

                                //更新列表
                                refreshList(path);
                            }
                            //设置播放暂停按钮的监听器
                            if(view.getId()==R.id.play_stop){
                                Button bt_select =(Button) view;

                                if(!isPlaying&&(bt_select.getText().toString().equals("play"))){
                                    //由路径实例化mediaPlayer
                                    mediaPlayer= MediaPlayer.create(RingActivity.this, Uri.parse(sPath));
                                    //有文件路径创建MediaPlayer对象
                                    //不循环播放
                                    mediaPlayer.setLooping(false);
                                    //开始播放
                                    mediaPlayer.start();
                                    isPlaying=true;

                                    bt_select.setText("stop");
                                }
                                else{

                                    //停止播放
                                    if((mediaPlayer!=null)&&isPlaying){
                                        mediaPlayer.stop();
                                        //释放资源
                                        mediaPlayer.release();
                                        bt_select.setText("play");
                                        isPlaying=false;

                                    }

                                }
                            }


                        }
                    });
                    return false;
                }
                return false;
            }
        };
        simpleAdapter.setViewBinder(binder);
        return simpleAdapter;
    }
    //刷新列表
    public void refreshList(String path){
        //获取录音文件名的列表
        List<Rname> Lrnames=getrecordFiles(path);
        //将 Rnames传入buildSimpleAdapter创建SimpleAdapter
        SimpleAdapter simpleAdapter=buildSimpleAdapter(Lrnames);
        //为该activity设置simpleAdapter,显示录音文件列表
        setListAdapter(simpleAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ring, menu);
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
