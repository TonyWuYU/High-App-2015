package com.xueba.quting.high;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wanglei.qq.bean.Alarminfor;
import com.example.wanglei.qq.bean.QQinfor;
import com.xueba.quting.high.Thread.SendGet;
import com.xueba.quting.high.other.SendService;
import com.xueba.quting.high.sqlite.DBHelper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;




public class AlarmSetActivity extends ListActivity {
    //  private TextView setTime,curTime,time_cycle;
    private TextView time_cycle;

    //Widgets
    private TimePicker tpTimeSet;
    private Button btnSetTime;
    private ImageView btnAdd;
    private Spinner spRate;
    private ArrayAdapter rateAdapter;
    private ListView listView;
    // private ListViewAdapter listViewAdapter;
    //选别选择项的RadioGroup
    private RadioGroup radioGroup;
    //
    private RadioButton rMan,rWomen,rRamdom;
    private ImageView Back_alarms;
    //
    private DBHelper dbHelper;
    private QQinfor A_qqinfor,qQinforGet;
    //以下int变量，用来存储月份，日期，小时，分钟
    private int month,date,hour,minute;
    //将设置的是周几发送给服务器
    private int send_day=0;
    //用来标记设置闹钟的周期和要求的性别默认分别为“只响一次”，“男”
    private String cycle="只响一次";
    private String  gender="男";
    private String rTime;
    //用于标记是修改还是添加
    private boolean ADD_SET=true;
    //用于标记删除
    private boolean DELETE=false;
    //用于标记只响一次，还是周期性设置
    private boolean repeat=false;
    //用于标记用户是否选择了自定义标志
    private int  isCustom=0;
    private Alarminfor get_alarm=new Alarminfor();
    //用来将用户的周期信息存储为0和1组成的字符串
    private String record_cycle;
    //Variables
    int upMinute;
    int upHour;
    //用来标记闹钟是否是否设置成功
    private boolean set_success=false;
    String rate;
    //用于判断自定义的多选对话框哪一项被选中
    boolean[] selected = new boolean[]{false,false,false,false,false,false,false};
    private  String[] choosedDate;
    int arrayPosition;
    private final int MUTI_CHOICE_DIALOG=1;
    //标记自定义多选对话框
    private final int ZIDINGYI_MUTI_CHOICE_DIALOG = 3;
    //周期选择对话框
    private final int CYCLE_SINGLE_CHOICE_DIALOG=2;
    private final long WEEK=7*24*60*60*1000;
    private SharedPreferences mySharedPreferences;
    private  SharedPreferences.Editor editor;
    private Dialog dialog;
    //用来标记在修改状态时用户是否点击修改的周期的对话框
    private boolean update_cycle=false;

    // private Handler handler;
    private Handler a_Handler = new Handler() {
        //处理消息队列中的消息
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==2)
            {
                qQinforGet=(QQinfor)msg.obj;
                System.out.println("开始跳转");
               // AlarmSetActivity.this.finish();
                //数据返回
                //backto();

            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);

        A_qqinfor=new QQinfor();
        //获取SharedPreferences存取的uid
        SharedPreferences mySharedPreferences= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        String  user_id=mySharedPreferences.getString("uid","");
        A_qqinfor.setUid(user_id);
        //实例化dbHelper用来操作数据库
        dbHelper=new DBHelper(AlarmSetActivity.this,user_id);

        //curTime=(TextView)findViewById(R.id.curTime);
        //setTime=(TextView)findViewById(R.id.setTime);
        //返回按钮时间
        Back_alarms=(ImageView)findViewById(R.id.ivBack);
        Back_alarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmSetActivity.this.finish();
            }
        });
        radioGroup=(RadioGroup)findViewById(R.id.rgGender);
        rMan=(RadioButton)findViewById(R.id.rbMan);
        rWomen=(RadioButton)findViewById(R.id.rbWoman);
        rRamdom=(RadioButton)findViewById(R.id.rRamdom);
        //给该radiogroup设置监听器
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //选中性别男
                if(i==R.id.rbMan){
                    gender="男";

                }
                //选中性别女
                else if(i==R.id.rbWoman){
                    gender="女";
                    ;
                }
                //选中随机
                else if(i==R.id.rRamdom){

                    gender="随机";
                }
                //setTime.setText(gender);

            }
        });
        //Bound widget
        tpTimeSet=(TimePicker)findViewById(R.id.timePicker);
        //设置TimerPicker为24小时制
        tpTimeSet.setIs24HourView(true);
        //设置时间按钮
        btnSetTime=(Button)findViewById(R.id.button);
        //添加按钮
        btnAdd=(ImageView)findViewById(R.id.btn_add);
        //不可以点击
        btnAdd.setClickable(false);
        //给这两个按钮设置监听器
        btnSetTime.setOnClickListener(new ClickListener());
        btnAdd.setOnClickListener(new ClickListener());
        //获取listView对象
        // listView=(ListView)findViewById(R.id.android:list);

        Intent intent=getIntent();
        //获取传递过来的ADD_SET,alarminfor和
        ADD_SET=intent.getBooleanExtra("ADD_SET",false);
        get_alarm=(Alarminfor)intent.getSerializableExtra("alarminfor");
        // A_qqinfor=(QQinfor)intent.getSerializableExtra("QQinfor");
        //  setTime.setText(""+ADD_SET);
        //通过设置进入该Activity
        if(ADD_SET==false){
            //将添加按钮设置为可见，并且可点击
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setClickable(true);
            //修改btnSetTime
            btnSetTime.setText("删除闹钟");
            //获取传递过来的Alarminfor

            //将时间以”：“拆分
            String[] get_time=get_alarm.getTime().split(":");
            //分别获取小时和分钟
            //setTime.setText(get_time[1]);
            int get_hour=Integer.parseInt(get_time[0]);
            int get_minute=Integer.parseInt(get_time[1]);
            tpTimeSet.setCurrentHour(get_hour);
            tpTimeSet.setCurrentMinute(get_minute);
            System.out.print("第一个count"+get_alarm.getCount());
            //   Toast.makeText(this, "传送的count"+get_alarm.getCount(),
            //   Toast.LENGTH_LONG).show();
            //获取周期
            cycle=get_alarm.getCycle();
            //time_cycle.setText(cycle);
            if(cycle.equals("只响一次")){
                repeat=false;
            }
            else if(cycle.equals("每天")){
                repeat=true;
                selected = new boolean[]{true,true,true,true,true,true,true};

            }

            else if(cycle.equals("工作日")){
                repeat=true;
                selected = new boolean[]{true,true,true,true,true,false,false};
            }
            else if(cycle.equals("周末")){
                repeat=true;
                selected = new boolean[]{false,false,false,false,false,true,true};
            }

            //获取性别
            gender=get_alarm.getGender();
            //

            if(gender.equals("男")){
                rMan.setChecked(true);
            }
            if(gender.equals("女")){
                rWomen.setChecked(true);
            }
            if(gender.equals("随机")){
                rRamdom.setChecked(true);
            }

            // time_cycle.setText(""+get_alarm.getCycle());
        }

        //listView adapter绑定
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        // cycle="你好";
        map.put("Cycle","周期");
        map.put("TimeCycle",cycle);
        list.add(map);
        // 创建一个SimpleAdapter 对象
        SimpleAdapter simpleAdapter = new SimpleAdapter(AlarmSetActivity.this, list,
                R.layout.setdate_listview_layout, new String[] { "Cycle", "TimeCycle" },
                new int[] { R.id.tvSetRate,R.id.tvRate });
        setListAdapter(simpleAdapter);

    }
    //每次ActivityonResume时，实例化 A_qqinfo，并且将SharedPreferences 存储的uid设置给他
    @Override
    protected void onResume() {
        super.onResume();
        A_qqinfor=new QQinfor();
        SharedPreferences mySharedPreferences= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        String  user_id=mySharedPreferences.getString("uid","");
        A_qqinfor.setUid(user_id);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            //自定义对话框
            //自定义对话框
            case MUTI_CHOICE_DIALOG:
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmSetActivity.this);
                builder.setTitle("自定义");
                DialogInterface.OnMultiChoiceClickListener multiListener = new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked)
                    {
                        selected[which] = isChecked;
                    }
                };
                //将Rate.xml文件中定义的数组zidingyi放到该多选对话框中，显示周一到周日，并且将boolean数组selected和监听器传入*/
                builder.setMultiChoiceItems(R.array.zidingyi,selected,multiListener);
                DialogInterface.OnClickListener btnListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which)
                            {     //清空cycle
                                cycle=null;
                                for(int i=0; i<selected.length; i++)
                                {   //判断哪一项被选中

                                    if(selected[i]==true){

                                        if(i==0){cycle=cycle+"周一";}
                                        if(i==1){cycle=cycle+"周二";}
                                        if(i==2){cycle=cycle+"周三";}
                                        if(i==3){cycle=cycle+"周四";}
                                        if(i==4){cycle=cycle+"周五";}
                                        if(i==5){cycle=cycle+"周六";}
                                        if(i==6){cycle=cycle+"周日";}


                                    }


                                    //choosedDate[i]=getResources().getStringArray(R.array.zidingyi)[i];
                                }
                                //去除cycle中的null
                                cycle=cycle.substring(4);
                                // setTime.setText(""+cycle);
                                time_cycle.setText(cycle);
                            }

                        };
                builder.setPositiveButton("确定",btnListener);
                dialog=builder.create();
                break;
            //周期对话框对话框
            case CYCLE_SINGLE_CHOICE_DIALOG:
                AlertDialog.Builder rateBuilder = new AlertDialog.Builder(AlarmSetActivity.this);
                rateBuilder .setTitle("周期");
                DialogInterface.OnClickListener rateClickListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which)
                    {


                        if(which==0){
                            selected = new boolean[]{false,false,false,false,false,false,false};
                            cycle="只响一次";
                            //修改状态时，修改周期
                            if(!ADD_SET){
                                update_cycle=true;
                            }
                            dialog.dismiss();

                        }

                        // arrayPosition=arg2;
                        //选中下拉框的自定义标签
                        //选择每天,则selected[]数组全置为true
                        if(which==1){
                            cycle="每天";
                            repeat=true;
                            //修改状态时，修改周期
                            if(!ADD_SET){
                                update_cycle=true;
                            }
                            for(int i=0; i<selected.length; i++)
                            {
                                selected[i] =true;

                            }
                            dialogInterface.dismiss();
                        }
                        //选择工作日
                        if(which==2){
                            cycle="工作日";
                            repeat=true;
                            //修改状态时，修改周期
                            if(!ADD_SET){
                                update_cycle=true;
                            }
                            selected = new boolean[]{true,true,true,true,true,false,false};

                            dialog.dismiss();
                        }
                        //选择周末
                        if(which==3){
                            cycle="周末";
                            repeat=true;
                            //修改状态时，修改周期
                            if(!ADD_SET){
                                update_cycle=true;
                            }
                            selected = new boolean[]{false,false,false,false,false,true,true};
                            dialog.dismiss();
                        }
                        //选择自定义
                        if(which==4)
                            repeat=true;
                        {   //取消本对话框
                            dialog.dismiss();
                            // showDialog(MUTI_CHOICE_DIALOG);
                        }
                        //将设置的周期显示到time_cycle控件中去
                        time_cycle.setText(cycle);
                    }
                };
                rateBuilder.setSingleChoiceItems(R.array.rate,0,rateClickListener);
                DialogInterface.OnClickListener rateBtnListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which)
                            {


                            }
                        };
                rateBuilder.setPositiveButton("确定",rateBtnListener);
                dialog = rateBuilder.show();
                break;
        }
        return dialog;
    }

    public class ClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.button:
                    //添加状态时该按钮作用为添加
                    if(ADD_SET){
                        //执行SetTime方法设置闹钟
                        SetTime();
                    }
                    //修改状态时该按钮的作用为删除
                    else {
                        //标记删除
                        DELETE=true;
                        //删除时只需要count参数，生成aid，将sqlite中数据及服务器端的该闹钟删除
                        //删除时只需要count参数，生成aid，将sqlite中数据及服务器端的该闹钟删除
                        set_to_sqlite(get_alarm.getCount(),null,null,null);
                        Alarminfor alarminfor_delete=get_alarm;
                        alarminfor_delete.setUid(A_qqinfor.getUid());
                        ArrayList<Alarminfor> alarminfors_delete =new ArrayList<Alarminfor>();
                        alarminfors_delete.add(alarminfor_delete);
                        A_qqinfor.setAinfor_list(alarminfors_delete);
                        // Toast.makeText(AlarmSetActivity.this,"删除 "+alarminfor, Toast.LENGTH_LONG).show();
                        //删除请求发送给服务器，标志为11
                        SendGet sendGet=new SendGet(A_qqinfor,a_Handler,10);
                        sendGet.start();
                        //取消闹钟
                        for(int i=0;i<7;i++){
                            register_cancel(get_alarm.getCount()+i*1000,0,0,false,false);
                        }

                        //Thread.sleep(4000);
                        //返回闹钟列表
                        AlarmSetActivity.this.finish();
                    }

                    break;

                case R.id.btn_add:
                    //执行执行SetTime方法设置闹钟，并保存
                    SetTime();
                    Toast.makeText(AlarmSetActivity.this, "修改 ", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;

            }
        }
    }
    public void SetTime()
    {   //获取tpTimeSet上设置的小时和分钟
        upMinute=tpTimeSet.getCurrentMinute();
        upHour=tpTimeSet.getCurrentHour();
        //将小时和分钟转换成标准的字符串
        String time=time_form(upHour,upMinute);
        //用于存储发送给服务器的闹钟列表
        ArrayList<Alarminfor> send_alarms=new ArrayList<Alarminfor>();
        //  long firstTime= SystemClock.elapsedRealtime();//开机之后到现在的运行时间(包括睡眠时间)
        //获取系统时间
        //   long systemTime=SystemClock.currentThreadTimeMillis();
        //创建Calendar对象
        Calendar calendar=Calendar.getInstance();

        //将系统时间设置给calendar
        calendar.setTimeInMillis(System.currentTimeMillis());
        //获取当前的WEEK_OF_MONTH
        int week=calendar.get(Calendar.WEEK_OF_MONTH);
        //获取当前系统的DAY_OF_WEEK
        int curDay=calendar.get(Calendar.DAY_OF_WEEK);
        //由于calendar,从1到7是从周日开始转换
        int send_day=0;
        if(curDay==1){
            send_day=7;
        }else{
            send_day=curDay%7-1;
        }

        //设置时区
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //将 calendar的小时和分钟设置为用户设置的小时和分钟
        calendar.set(Calendar.HOUR_OF_DAY,upHour);
        calendar.set(Calendar.MINUTE,upMinute);
        //将秒设为0
        calendar.set(Calendar.SECOND,0);
        SharedPreferences mySharedPreferences= getSharedPreferences("alarms",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //读取已存闹钟计数
        int count=mySharedPreferences.getInt("count",0);
        //选择了只响一次
        if(!repeat){
            // 选择的定时时间和当前时间对比是否符合要求
            long currTime=System.currentTimeMillis();
            long selectTime = calendar.getTimeInMillis();
            SimpleDateFormat formatter1=new SimpleDateFormat ("yyyy年MM月dd日    HH:mm:ss     ");
            Date sel= new Date(selectTime);//获取当前时间
            Date cur=new Date(currTime);
            String seles = formatter1.format(sel);
            String  curr=formatter1.format(cur);
            // setTime.setText(""+seles);
            // curTime.setText(""+curr);

            //设置时间大于当前时间
            if(currTime>selectTime){
                //弹出对话框，提醒用户重新设置时间
                new AlertDialog.Builder(AlarmSetActivity.this).
                        setTitle("重新设置").//设置标题
                        setMessage("设置时间小于当前时间，请重新设置").//设置内容
                        setPositiveButton("确定", new DialogInterface.OnClickListener(){//设置按钮
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();

            }
            //时间正确
            else{



                //  Toast.makeText(AlarmSetActivity.this,"设置单次闹钟成功! ", Toast.LENGTH_LONG).show();
                //将闹钟信息封装到Alarminfor ,并添加到闹钟列表，8用来标记选择的是只响一次
                //send_alarms.add(to_alarm(8, time, count, gender));
                if (ADD_SET) {
                    //将闹钟信息封装到alarminfor对象
                    send_alarms.add(to_alarm(send_day, time, count, gender, 1));
                    //由于网络延时，增加10秒


                    //注册闹钟单次闹钟
                    register_cancel(count, selectTime, send_day, true, false);

                }
                //修改状态时count不能改变
                else{
                    int update_count=get_alarm.getCount();
                    send_alarms.add(to_alarm(send_day, time,update_count, gender,1));

                    //注册之前先取消之前的闹钟
                    //register_cancel(update_count,0,0,false,false);
                    //再次注册新的闹钟
                    register_cancel(update_count,selectTime,send_day,true,false);

                }

                //设置成功
                set_success=true;
                //测试
                SimpleDateFormat formatter=new SimpleDateFormat ("yyyy年MM月dd日    HH:mm:ss     ");
                Date curDate= new Date(calendar.getTimeInMillis());//获取当前时间
                String setTimes2 = formatter.format(curDate);
                //setTime.setText(""+setTimes2);

            }

        }
        //周期闹钟
        else{
            //测试

            for(int i=0;i<selected.length;i++)
            {   //获取当前的周数

                if(selected[i]==true)
                {
                    //选择的数组的位置和星期对应，由于calendar周日是1，周一是2，依次
                    int dayofweek=(i+1)%7+1;

                    //由于设置的时间小于当前时间，则周数增加一
                    int weekofmonth=weekofmonth(i, curDay, week);

                    //设置DAY_OF_WEEK
                    calendar.set(Calendar.DAY_OF_WEEK,dayofweek);
                    calendar.set(Calendar.WEEK_OF_MONTH,weekofmonth);

                    int year=calendar.get(Calendar.YEAR);
                    //calendar使用的是罗马历，月份从0开始
                    month=calendar.get(Calendar.MONTH)+1;
                    date=calendar.get(Calendar.DAY_OF_MONTH);
                    hour=calendar.get(Calendar.HOUR_OF_DAY);
                    minute=calendar.get(Calendar.MINUTE);


                    //setTime.setText(""+currtime);
                    // 选择的定时时间
                    long currTime=System.currentTimeMillis();
                    long selectTime = calendar.getTimeInMillis();
                    // 如果当前时间大于设置的时间，则增加一周
                    //测试


                    if(currTime> selectTime) {

                        calendar.set(Calendar.WEEK_OF_MONTH,week+1);
                        //重新设置选择的时间
                        selectTime = calendar.getTimeInMillis();
                    }
                    SimpleDateFormat formatter=new SimpleDateFormat ("yyyy年MM月dd日    HH:mm:ss     ");
                    Date curDate= new Date(calendar.getTimeInMillis());//获取当前时间

                    String setTimes = formatter.format(curDate);

                    System.out.println("第几条记录"+i+"---"+setTimes);
                    //setTime.setText(""+setTimes);
                    //register_cancel(count,selectTime,send_day,true,false);

                    //闹钟设置正确

                    // 进行闹铃注册
                    //注册闹钟

                    //将闹钟信息封装到Alarminfor ,并添加到闹钟列表，1-7用来标记周一到周日
                    if(ADD_SET){
                        send_alarms.add(to_alarm((i+1),time,count, gender,0));
                        //注册周期闹钟,由于第一个标志位用于区分闹钟，不然会被覆盖掉，用户闹钟一般不会超过1000，这样来标记
                        register_cancel(count+i*1000,selectTime,send_day,true,true);
                    }
                    //修改状态时count不能改变
                    else{
                        int update_count=get_alarm.getCount();
                        send_alarms.add(to_alarm((i+1), time,update_count, gender,0));
                        //先取消原来的闹钟
                        register_cancel(update_count+i*1000,0,0,false,true);
                        ///再重新注册
                        register_cancel(update_count+i*1000,selectTime,send_day,true,true);
                    }

                    //设置成功
                    set_success=true;

                }
            }
        }

        //闹钟设置成功后
        if(set_success){

            //将闹钟列表设置到QQinfor对象中去
            A_qqinfor.setAinfor_list(send_alarms);
            //将闹钟的信息封装到alarminfor类，并保存到sqlite
            set_to_sqlite(count,time,cycle,gender);
            //判断是添加状态还是修改状态，
            if(ADD_SET){
                //将添加的闹钟信息由Service发送给服务器
                 SendGet sendGet=new SendGet(A_qqinfor,a_Handler,3);
                 sendGet.start();
                //Send_byService(A_qqinfor, 3);
                //添加状态，则SharedPreferences文件中count计数增加1
                int count_add=count+1;
                editor.putInt("count",count_add);
                //提交当前数据
                editor.commit();
            }
            //将修改的闹钟信息发送给服务器
            else{
                  SendGet sendGet=new SendGet(A_qqinfor,a_Handler,4);
                  sendGet.start();
                //Send_byService(A_qqinfor, 4);
            }
            //返回闹钟列表





        }


    }
    //获取将设置好的闹钟信息存储到sqlite
    public void set_to_sqlite(int count,String time,String cycle, String gender){


        Alarminfor alarminfor=new Alarminfor();
        alarminfor.setCount(count);
        alarminfor.setTime(time);
        //System.out.println("setTimeqqq"+alarminfor.getTime());
        alarminfor.setCycle(cycle);
        alarminfor.setState(1);
        alarminfor.setGender(gender);

        if(ADD_SET){
            //将设置闹钟的信息添加到SQLite
            dbHelper.insert(alarminfor);
        }
        //修改状态下
        else{
            //由于修改请求，count不变,使用原来的count
            alarminfor.setCount(get_alarm.getCount());
            //int count1=alarminfor.getCount();
            //点击了删除按钮，用10标记发送删除请求
            if(DELETE){
                //删除SQLite闹钟的信息
                //System.out.println("点击了删除按钮"+alarminfor.getTime());
                dbHelper.delete(alarminfor);

            }
            //修改请求，用4标记
            else{

                //将修改的闹钟的信息添加到SQLite
                System.out.print("修改的count"+alarminfor.getCount());
                dbHelper.update(alarminfor);

            }


        }

    }
    //将闹钟信息封装到alarminfor对象，
    public Alarminfor to_alarm(int send_day,String time,int count, String gender,int isOne){
        String uid=A_qqinfor.getUid();

        //创建一个alarminfor对象和列表
        Alarminfor alarminfor=new Alarminfor();

        //设置它的各项属性
        //通过这种方式构造aid
        String aid=uid+"-"+send_day+"-"+count;

        alarminfor.setAid(aid);

        //     Toast.makeText(this, "测试"+alarminfor.getAid(),Toast.LENGTH_LONG).show();
        // System.out.println("测试"+alarminfor.getAid());
        alarminfor.setDay(send_day);
        alarminfor.setCount(count);
        alarminfor.setTime(time);
        alarminfor.setUid(uid);
        //System.out.println("setTimeqqq"+alarminfor.getTime());
        alarminfor.setCycle(cycle);
        alarminfor.setState(1);
        alarminfor.setGender(gender);
        alarminfor.setIsOne(isOne);
        alarminfor.setUpdate_cycle(update_cycle);
        // setTime.setText(alarminfor.getAid());
        String sgener=alarminfor.getGender();
        String  cycles=alarminfor.getCycle();
        int  counts=alarminfor.getCount();
        return alarminfor;

    }
    //注册闹钟
    public void register_cancel(int rcount,long selectTime,int send_day,boolean register_cancel,boolean isrepeat){
        //注册闹钟
        Intent intent=new Intent(AlarmSetActivity.this,AlarmReciever.class);
        //将闹钟的count 传送给闹钟播放程序
        intent.putExtra("send_day",send_day);
        intent.putExtra("count",rcount);
        //第二个参数用于标记设置的闹钟，用于闹钟的删除和更新，最后一个参数设置为这样才能床送putExtra,不然会是空值
        PendingIntent sender=PendingIntent.getBroadcast(AlarmSetActivity.this,rcount,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //注册闹钟
        if(register_cancel){
            //周期性闹钟
            if(isrepeat){
                 /*重复执行闹钟，，第一个参数表示闹钟类型(睡眠状态下不可用
                         第二个参数表示，首次执行的时间week表示每周都循环，sender前面定义的PendingIntent*/
                manager.setRepeating(AlarmManager.RTC_WAKEUP,selectTime,WEEK,sender);
            }
            //只响一次
            else{
               /*，第一个参数表示闹钟类型(睡眠状态下不可用
                第二个参数表示，首次执行的时间，sender前面定义的PendingIntent*/
                manager.set(AlarmManager.RTC_WAKEUP,selectTime,sender);
            }

        }
        //取消闹钟
        else{
            manager.cancel(sender);
        }

    }
    //返回AlarmListActivity
    public void backto(){
        //跳转到AlarmListActivity
        Intent intent=new Intent();
        intent.setClass(AlarmSetActivity.this, MainActivity.class);
        //启动Activity
        startActivity(intent);
    }


    //将设置的闹钟信息发送到服务器

    //ListView的监听器
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //获取显示周期的TextView,注意只能在该方法下才能获取子控件的id
        time_cycle=(TextView)findViewById(R.id.tvRate);
        //由position 为1可知选择了周期条目
        if(position==0){
            //打开周期对话框
            //Toast.makeText(this, "测试",Toast.LENGTH_LONG).show();
            showDialog(CYCLE_SINGLE_CHOICE_DIALOG);

        }

    }
    //将调整周数当前为周二，你设置一个每天的闹钟则周一的时间自动加一个星期
    public static int weekofmonth(int dayofweek,int curday,int week){

        if(curday==1){
            if(dayofweek!=curday) {
                week = week + 1;
            }
        }
        else{
            if((dayofweek+2)<curday){
                week=week+1;
            }
            else{
                if(dayofweek==6){
                    week=week+1;
                }
            }
        }

        return week;
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
    //由Sendserivce将闹钟信息发送给，服务器
    public void Send_byService(QQinfor qqinfor,int pid){
        Intent intent = new Intent();
        intent.putExtra("QQinfor", qqinfor);
        intent.putExtra("pid",pid);
        intent.setClass(AlarmSetActivity.this, SendService.class);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
