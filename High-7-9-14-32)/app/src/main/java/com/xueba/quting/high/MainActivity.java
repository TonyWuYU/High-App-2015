package com.xueba.quting.high;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanglei.qq.bean.Alarminfor;
import com.example.wanglei.qq.bean.Friends;
import com.example.wanglei.qq.bean.QQinfor;
import com.example.wanglei.qq.bean.Rname;
import com.example.wanglei.qq.bean.friendsobject;
import com.example.wanglei.qq.bean.squareclientobject;
import com.example.wanglei.qq.bean.squareobject;
import com.xueba.quting.high.Thread.SendGet;
import com.xueba.quting.high.other.HttpDownloader;
import com.xueba.quting.high.other.MulDownloader;
import com.xueba.quting.high.other.SquDownloader;
import com.xueba.quting.high.sqlite.DBHelper;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/************************************clock****************************************/
/********************************************clock*******************************/
/*******************************************square*********************************/
/**************************************************square*******************************/
/*******************************************Messagepage********************************************/
/*******************************************Messagepage********************************************/
//主界面
public class MainActivity extends Activity {

    //Variables
   // Fragment myClockfrag;
    //FragmentTransaction myClockfragTrans;
    private ActionBarDrawerToggle drawerToggle;//drawerLayout的toggle,类似listView的adapter
    private LayoutInflater layoutInflater;//infalte viewpager中的页面
    //侧滑栏向左滑为-1，向右滑为1；
    private final int LEFT=-1;
    private final int CLOSE=1;
    //按下时的点为(downx，downy),抬起时的点为(upx,upy)
    //float downx=0,downy=0,upx=0,upy=0;
    private List<View> viewArrayList;
    private myPagerAdapter pagerAdapter;
    //确认上一次是否按得返回键
    private boolean isLastBack=false;
    private long exitTime;
    private final long WEEK=7*24*60*60*1000;
    private  TextView tvName;
    //Widgets
    private DrawerLayout drawerLayout;
   // private LinearLayout leftDrawerlayout;
 //   private GestureDetector gestureDetector;
    private ViewPager viewPager;//控制滑动的控件
    private ImageView userHead;//我的闹钟界面的用户头像
    private ImageView imgMyClock;//我的闹钟按钮
    private ImageView imgSquare;//我的广场按钮
    private ImageView imgMyFriend;//我的好友按钮（我的消息）
   // private ImageView imgAddClock;//
    private ImageView record;
    private TextView tvRecord;
    private TextView tvmyFriend;
    private EditText etSign;
    private TextView tvAbout;
    /****clock****/
//显示设置好的性别和时间
    private TextView set_gender,set_time;
    private QQinfor AL_qqinfor;
    //打开关闭按钮
    private ImageView imgAddClock;
    private Button bt_refresh;
    private  boolean ADD_SET;
    //用于sqlite
    private DBHelper dbHelper;
    private ListView alarmListView=null;
    private MyAdapter adapter=null;
    //从服务器段获取的QQinfor
    private  QQinfor al_getqqinfor=new QQinfor();
    //创建一个Alarminfor列表，存储已经设置的闹钟信息
    private List<Alarminfor> alarms=new ArrayList<Alarminfor>();

    /****clock***********/
    /*******************************************square***************************************/
    ArrayList<HashMap<String,Object>>alSquar;
    SquarListViewAdapter squareListViewAdapter;
    String username;//uid
    String user_gender;//用户 性别
    String user_wantgender=null;
    QQinfor qq_get=new QQinfor();
    private AlertDialog.Builder dialog;
    private AlertDialog alertDialog;
    private ListView listView;
    private  String rid,aid;
    ArrayList<squareobject> squ_getlist=new ArrayList<squareobject>();
    ArrayList<squareclientobject> squ_getclentlist=new ArrayList<squareclientobject>();
    ArrayList<squareclientobject> squ_getlastlist=new ArrayList<squareclientobject>();
    squareobject temp_squ=new squareobject();
    squareclientobject tempclent_squ;
    private QQinfor qq_friendinfor=null;//用于存放从数据库查询到的对象
    ArrayList<String> user_name;//存放用户昵称的数组
    ArrayList<String>user_sign;//存放用户个性签名
    ArrayList<String>user_head;//用于存放用户的头像的下载地址
    ArrayList<String>user_clock;//用于存放用户的闹钟
    ArrayList<Bitmap>head_bitmap;//用于将用户的头像转换为bitmap;
    HashMap<String,Object> map=new HashMap<String,Object>();
    //控件
    ListView lvSquar;
    Spinner spGender;
    private Handler squHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 2)//从服务器数据库返回值
            {
                alSquar=new ArrayList<HashMap<String, Object>>();
                squ_getclentlist=new ArrayList<squareclientobject>();
                squ_getlastlist=new ArrayList<squareclientobject>();
                qq_get=(QQinfor)msg.obj;
                squ_getlist=qq_get.getSquare_list();
                for(int i=0;i<squ_getlist.size();i++){
                    tempclent_squ=new squareclientobject();
                    tempclent_squ.setUid(squ_getlist.get(i).getUid());
                    tempclent_squ.setAlarmtime(squ_getlist.get(i).getAlarmtime());
                    tempclent_squ.setLogourl(squ_getlist.get(i).getLogourl());
                    tempclent_squ.setAid(squ_getlist.get(i).getAid());
                    squ_getclentlist.add(tempclent_squ);
                }

                for(int i=0;i<squ_getclentlist.size();i++){
                    new SquDownloader(squ_getclentlist.get(i),squHandler).start();
                }


            }

            if (msg.what == 4) //下载图片
            {
                squ_getlastlist.add((squareclientobject)msg.obj);
                if(squ_getlastlist.size()==squ_getclentlist.size()){
                    show_square();
                }

            }
        }
    };
    /*****************************************square*******************************************/
    /*****************************************下载图片************************************************/

    /*****************************************alarm*******************************************/
    private Handler a_Handler = new Handler() {
        //处理消息队列中的消息
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==2)
            {
                QQinfor qqinforGet=(QQinfor)msg.obj;


            }
        }

    };
    /*****************************************alrm*******************************************/
    private   QQinfor Int_qqinfor=new QQinfor();
    private  QQinfor qQinforGet=new QQinfor();
    private Bitmap userbitmap = null;//装在用户的头像
    // private Handler handler;
    private Handler eHandler = new Handler() {
        //处理消息队列中的消息
        @Override
        public void handleMessage(Message msg) {
            //Bitmap bitmap = null ;
            if (msg.what == 1) {//发送1消息是 获取qq头像
                userbitmap = (Bitmap) msg.obj;
                //将获取到的qq头像设置给qqinfor对象
                //  qqinfor.setQqLogo(bitmap);
                //显示头像
                userHead.setImageBitmap(userbitmap);
                userHead.setVisibility(android.view.View.VISIBLE);
                Userlogo.setImageBitmap(userbitmap);
            }
            if(msg.what==2)
            {
                qQinforGet=(QQinfor)msg.obj;
                System.out.print("GetQQinfor--->"+qQinforGet);

            }
        }

    };
    /*****************************************下载图片************************************************/
    /*****************************************Message初始化****************************************************/
    protected ListView chatListView=null;
    protected MessAdapter messadapter=null;
    ArrayList<HashMap<String,Object>> list1=null;
    HashMap<String,Object> map_message=null;
    private QQinfor qq_friendinfor_message=new QQinfor();
    private QQinfor chat_qqinfrom=null;
    String username_mess=null;//上面有一个
    Bitmap chat_we=null;
    ArrayList<friendsobject> storelist;//信息载体
    ArrayList<friendsobject> pulllist;//信息载体
    private int POSITION;
    private TimerTask task;
    private Timer timer1;
    QQinfor QQ_getfriend1=new QQinfor();
    private Handler mess_Handler = new Handler() {
        //处理消息队列中的消息
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==2)
            {

                storelist=new ArrayList<friendsobject>();
                pulllist=new ArrayList<friendsobject>();
                list1=new ArrayList<HashMap<String,Object>>();
                qq_friendinfor_message=(QQinfor)msg.obj;
                /*********如果传回服务代码506，更新消息列表************/
                if(qq_friendinfor_message.getpid()==506){
                    initialize_message();
                }
                else{
                    /*********接收到数据，载入载体**********/
                    friendsobject temp=new friendsobject();;
                    for(int i=0;i<qq_friendinfor_message.getFrineds_list().size();i++){
                        temp=new friendsobject();
                        temp.setUid(qq_friendinfor_message.getFrineds_list().get(i).getUid());//对象uid
                        temp.setName(qq_friendinfor_message.getFrineds_list().get(i).getName());//对象名字
                        temp.setChara(qq_friendinfor_message.getFrineds_list().get(i).getChara());//对象个性签名
                        temp.setUrl(qq_friendinfor_message.getFrineds_list().get(i).getLogourl());//对象头像URL
                        storelist.add(temp);
                    }
                    /*****消息好友数据绑定下载****/
                    for(int i=0;i<qq_friendinfor_message.getFrineds_list().size();i++){
                        new MulDownloader(storelist.get(i),mess_Handler).start();
                    }
                }

            }

            if(msg.what==3){
                /*******收集下载好的对象*********/
                pulllist.add((friendsobject)msg.obj);
                if(pulllist.size()==qq_friendinfor_message.getFrineds_list().size()){
                    show_message();//接受完成，展示
                }
            }
        }

    };
    ImageView Userlogo;
    /*****************************************Message初始化****************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定控件
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        imgMyClock=(ImageView)findViewById(R.id.imgMyClock);
        imgMyFriend=(ImageView)findViewById(R.id.imgmyFriend);
        imgSquare=(ImageView)findViewById(R.id.imgSquare);
        etSign=(EditText)findViewById(R.id.txtSign);
        tvName=(TextView)findViewById(R.id.txtName);
        //从SharedPreferences中获取个性签名
        SharedPreferences sharedPreferences= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        String  character=sharedPreferences.getString("Ucharacter","");
        String   Uname=sharedPreferences.getString("Uname","");
        //将个性签名设置给该控件
        etSign.setText(character);
        tvName.setText(Uname);
        tvAbout=(TextView)findViewById(R.id.txtAbout);
        Userlogo=(ImageView)findViewById(R.id.imgHead);

       //初始化DrawerLayout
        InitActionBarDrawerToggle();
        drawerLayout.setDrawerListener(drawerToggle);
        //初始化ViewPager
        layoutInflater=LayoutInflater.from(this);
        View myClockView=layoutInflater.inflate(R.layout.activity_myclock,null);
        View highSquar=layoutInflater.inflate(R.layout.activity_highsquar,null);
        View highFriend=layoutInflater.inflate(R.layout.activity_highmessage,null);
        viewArrayList=new ArrayList<View>();
        viewArrayList.add(myClockView);
        viewArrayList.add(highSquar);
        viewArrayList.add(highFriend);
        pagerAdapter=new myPagerAdapter(viewArrayList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ListViewOnPageChangeLitener());
        //绑定viewPager中view的控件
        userHead=(ImageView) myClockView.findViewById(R.id.imgUserHead);
        imgAddClock=(ImageView)myClockView.findViewById(R.id.imgAddClock);
        //alarmListView=(ListView)myClockView.findViewById(R.id.lvMyClock);
        //点击事件
        userHead.setOnClickListener(new ClickListener());
        imgMyClock.setOnClickListener(new ClickListener());
        imgSquare.setOnClickListener(new ClickListener());
        imgMyFriend.setOnClickListener(new ClickListener());
        imgAddClock.setOnClickListener(new ClickListener());
        etSign.setOnClickListener(new ClickListener());
        tvAbout.setOnClickListener(new ClickListener());
         //跳转至我的录音
         tvRecord=(TextView)findViewById(R.id.txtRing);
         tvRecord.setOnClickListener(new ClickListener());
         //跳转至我的好友列表
         tvmyFriend=(TextView)findViewById(R.id.txtFriend);
         tvmyFriend.setOnClickListener(new ClickListener());
        /***********************图片个人设置*********************************/
        Intent intent_person=getIntent();
        //获取传递过来的QQinfor
        Int_qqinfor=(QQinfor)intent_person.getSerializableExtra("QQinfor");
        //获取QQinfor中图像的url地址
        String url =Int_qqinfor.getLogUrl();
        //通过头像的url ,开启下载线程,并由eHandler接收结果
        HttpDownloader hd = new HttpDownloader(url, eHandler);
        hd.start();
        /***********************图片个人设置*********************************/
        /******************clockoncreate**********************/
        //从SharedPreferences中获取uid,用于创建多用户的表
        SharedPreferences mySharedPreferences= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        String  user_id=mySharedPreferences.getString("uid","");

        dbHelper=new DBHelper(MainActivity.this,user_id);
        alarmListView=(ListView)myClockView.findViewById(R.id.lvMyClock);
       // show_alarms();
       /************************clockoncreate**********************/
        /***********************squarecreate********************************/
        //listView列表实现
        lvSquar=(ListView)highSquar.findViewById(R.id.lvHighList);
        alSquar=new ArrayList<HashMap<String, Object>>();

        Intent intent=getIntent();
        //获取传递过来的QQinfor
        QQinfor getlast=new QQinfor();
        getlast=(QQinfor)intent.getSerializableExtra("QQinfor");//接受上个活动的数据
        username=getlast.getUid();
        user_gender=getlast.getGender();
        user_wantgender="男";

        //Spinner实现

        spGender=(Spinner)highSquar.findViewById(R.id.spGenderChoose);
        String[]list=getResources().getStringArray(R.array.gender);
        //为spinner设置一个适配器
        ArrayAdapter<String> sp_Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list);
        //定义下拉样式
        sp_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(sp_Adapter);
        //为广场中的Spinner添加选择事件
        spGender.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                user_wantgender = parent.getItemAtPosition(position).toString();
                //Toast.makeText(SquarActivity.this, "你点击的是:" + str, Toast.LENGTH_SHORT).show();
                QQinfor qq_send=new QQinfor();
                qq_send.setUid(username);
                qq_send.setGender(user_gender);
                qq_send.setOthergender(user_wantgender);
                if(user_wantgender.equals("随机")){
                    alSquar.removeAll(alSquar);
                    //squareListViewAdapter.notifyDataSetChanged();
                    new SendGet(qq_send,squHandler,80).start();
                }
                else{
                    alSquar.removeAll(alSquar);
                    if(squareListViewAdapter!=null){
                        squareListViewAdapter.notifyDataSetChanged();}
                    new SendGet(qq_send,squHandler,70).start();

                    //adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        }) ;
        initialize_square();
        /***********************squarecreate********************************/
        /********************************messagecreate******************************************/
        //每一列的列名/Map的键名  和其对应的View子控件的ID
        list1=new ArrayList<HashMap<String,Object>>();
        storelist=new ArrayList<friendsobject>();
        pulllist=new ArrayList<friendsobject>();
        chatListView=(ListView)highFriend.findViewById(R.id.qq_list);
        if(chatListView==null){
            Log.e("chatlistview","null");
        }
        Intent intent_message=getIntent();
        //获取传递过来的QQinfor
        chat_qqinfrom=new QQinfor();
        chat_qqinfrom=(QQinfor)intent_message.getSerializableExtra("QQinfor");//接受上个活动的数据
        ////chat_we=(Bitmap)intent.getParcelableExtra("Ubitmap");//获得上个活动下载到的图片
        chat_we=userbitmap;//用户头像

        //获取SharedPreferences存取的uid
        SharedPreferences mySharedPreferences_message= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor_message = mySharedPreferences_message.edit();
        username_mess=mySharedPreferences_message.getString("uid","");
        //username_mess=chat_qqinfrom.getUid();//获得当前用户uid
        initialize_message();//初始化消息，发送服务代码给服务器，等待接受
        timer1 = new Timer(true);//初始化定时器
        /********************************messagecreate******************************************/
    }
    //重写clickLlistener
    public class ClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.imgUserHead:
                    Slipping(LEFT);//打开侧边栏
                    break;
                case R.id.imgMyClock:
                    gotoMyClock();//跳转至我的闹钟
                    break;
                case R.id.imgSquare:
                    gotoMySquare();//跳转至我的广场
                    break;
                case R.id.imgmyFriend://跳转至我的朋友
                    gotoMyFriend();
                    break;
                case R.id.imgAddClock:
                    gotoAddClock();//跳转至添加闹钟界面
                    break;
                case R.id.txtRing:
                    gotoMyrecord();//跳转至我的录音界面
                    break;
                case R.id.txtFriend:
                    gotoFriendList();//跳转至好友列表
                    break;
                case R.id.txtSign:
                    gotoEditSign();//跳转至编辑个性签名
                    break;
                case R.id.txtAbout:
                    gotoAboutUs();//跳转至关于
                    break;
                default:
                    break;
            }
        }
    }
    //跳转至我的闹钟
    public void gotoMyClock()
    {
        /*******图标变色********/
        viewPager.setCurrentItem(0);
        imgMyClock.setImageResource(R.drawable.redalarm);
        imgMyFriend.setImageResource(R.drawable.mymessage);
        imgSquare.setImageResource(R.drawable.squar);

    }
    //跳转至广场
    public void gotoMySquare()
    {
        //Intent intent=new Intent(MainActivity.this,SquarActivity.class);
        //startActivity(intent);
        viewPager.setCurrentItem(1);
        imgMyClock.setImageResource(R.drawable.alarm);
        imgMyFriend.setImageResource(R.drawable.mymessage);
        imgSquare.setImageResource(R.drawable.beforesquar);
    }
    //跳转至我的好友
    public void gotoMyFriend()
    {
        viewPager.setCurrentItem(2);
        imgMyClock.setImageResource(R.drawable.alarm);
        imgSquare.setImageResource(R.drawable.squar);
        imgMyFriend.setImageResource(R.drawable.bluemymessage);
    }
    //跳转至添加闹钟页面
    public void gotoAddClock()
    {   /************clock************/
        Intent intent =new Intent();
        //标记为添加状态
        ADD_SET=true;
        intent.putExtra("ADD_SET", ADD_SET);
        // intent.putExtra("QQinfor",AL_qqinfor);
        intent.setClass(MainActivity.this, AlarmSetActivity.class);
        startActivity(intent);
        /************clock************/
       // Intent intent=new Intent(MainActivity.this,AddClockActivity.class);
       // startActivity(intent);
    }
    //跳转至侧边栏
    public void gotoMyrecord()
    {  //跳转到录音界面
        Intent intent=new Intent(MainActivity.this,RingActivity.class);
        startActivity(intent);
    }
    public void  gotoFriendList()
    {
        Intent intent_getlast=getIntent();
        //获取传递过来的QQinfor
        QQinfor checkqq=new QQinfor();
        checkqq=(QQinfor)intent_getlast.getSerializableExtra("QQinfor");
        Intent intentfriend=new Intent();
        intentfriend.putExtra("QQinfor", checkqq);
        intentfriend.putExtra("Ubitmap",userbitmap);
        intentfriend.setClass(MainActivity.this, FriendActivity.class);
        startActivity(intentfriend);
    }
    //跳转至编辑个性签名
    public void gotoEditSign()
    {
        Intent intent=new Intent(MainActivity.this,EditSignActivity.class);
        startActivity(intent);
    }
    //跳转至关于我们
    public void gotoAboutUs()
    {
        Intent intent=new Intent(MainActivity.this,AboutUsActivity.class);
        startActivity(intent);
    }
    //初始化 ActionBarDrawerToggle
    public void InitActionBarDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.myhead,  /* nav drawer image to replace 'Up' caret */
                R.string.open,  /* "open drawer" description for accessibility */
                R.string.close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                invalidateOptionsMenu();
            }
        };
    }
    //操作侧滑栏
    public void Slipping(int direction)
    {
        switch (direction)
        {
            //向左滑动
            case LEFT:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            //关闭侧滑栏
            case CLOSE:
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
        }
    }


    //设置键盘监听
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event) {
        //按两次返回键退出程序
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            //Toast.LENGTH_SHORT是2秒，Toast.LENGTH_LONG是3.5秒
            if((System.currentTimeMillis()-exitTime)>2000)
            {
                Toast.makeText(getApplicationContext(),"再按一次退出键退出程序",Toast.LENGTH_SHORT).show();
                exitTime=System.currentTimeMillis();
            }
            else
                finish();
        }
        return true;
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //设置手机上的按钮事件
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
        else if(id==R.id.home)//按下home键直接退出程序
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    //重写ListView的onpagerahangeListener
    public class ListViewOnPageChangeLitener implements ViewPager.OnPageChangeListener
    {
        @Override
      public void onPageSelected(int arg0)
        {
            switch (arg0)
            {
                case 0:
                    imgMyClock.setImageResource(R.drawable.redalarm);
                    imgMyFriend.setImageResource(R.drawable.mymessage);
                    imgSquare.setImageResource(R.drawable.squar);
                    break;
                case 1:
                    imgMyClock.setImageResource(R.drawable.alarm);
                    imgMyFriend.setImageResource(R.drawable.mymessage);
                    imgSquare.setImageResource(R.drawable.beforesquar);
                    break;
                case 2:
                    imgMyClock.setImageResource(R.drawable.alarm);
                    imgSquare.setImageResource(R.drawable.squar);
                    imgMyFriend.setImageResource(R.drawable.bluemymessage);
                    break;
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
           }

         @Override
         public void onPageScrollStateChanged(int arg0) {
          }
    }

    /***********************************clock*************************************/
    //////展示闹钟列表 ////
    private void show_alarms(){

        //创建自定义的MyAdapter对象
        String[] from={"Gendertext","Gender","Time","Cycle","ImageView"};           //这里的内容对应后面HashMap中的键
        int[] to={R.id.gender_text,R.id.set_gender,R.id.set_time , R.id.set_cycle,R.id.on_off};
        //从数据库中读取已经存入的闹铃信息
        alarms= dbHelper.find();
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        if(alarms!=null){
            for(int i=0; i<alarms.size(); i++){
                Alarminfor alarminfor=alarms.get(i);
                HashMap<String, String> map = new HashMap<String, String>();
                //map调用put方法添加键值对
                map.put("Gendertext",alarminfor.getGender());
                map.put("Time", alarminfor.getTime());
                map.put("Cycle",alarminfor.getCycle());
                // map.put("ON_OFF","ON");
                list.add(map);
            }
        }

        adapter=new MyAdapter(this,R.layout.alarm_item,list,from,to);
        //调用ListView的setAdapter()方法设置适配器
        alarmListView.setAdapter(adapter);
    }

    public class MyAdapter extends BaseAdapter {
        private Context context=null;
        private int resources;
        private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        private String[] from;
        private int[] to;


        public MyAdapter(Context context, int resources,
                         List<HashMap<String, String>>list, String[] from, int[] to)
        {
            super();
            this.context = context;
            this.resources = resources;
            this.list = list;
            this.from = from;
            this.to = to;
        }

        /**
         * 剩下的问题就是依次实现BaseAdapter的这几个类方法就可以了
         */

        @Override
        public int getCount() {        //这个方法返回的是ListView的行数
            // TODO Auto-generated method stub
            return list.size();
        }



        @Override
        public Object getItem(int arg0) {      //这个方法没必要使用，可以用getItemId代替
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int itemId) {     //点击某一行时会调用该方法，其形参由安卓系统提供
            // TODO Auto-generated method stub
            return itemId;
        }

        class ViewHolder{
            public ImageView on_off=null,Gender=null;
            public TextView Gendertext=null,Time=null,Cycle=null;

            /*
             * 从这里可以看出，from和to数组彼此之间的元素应该一一对应，同时from和to各自元素内部的顺序不同，最后ListView
             * 呈现的位置也会不同！
             */
            public ViewHolder(View convertView){

            /*注意View和Activity都属于容器类，都需要设置布局文件，内部都含有子控件，且都有findViewById()
             * 他们之间没有明显的继承关系
             */
                Gendertext=(TextView)convertView.findViewById(to[0]);
                Gender=(ImageView)convertView.findViewById(to[1]);
                Time=(TextView)convertView.findViewById(to[2]);
                Cycle=(TextView)convertView.findViewById(to[3]);
                on_off=(ImageView)convertView.findViewById(to[4]);
            }

        }
        class  TextViewListener implements View.OnClickListener{

            private int position;
            public TextViewListener(int position){
                this.position=position;
            }
            @Override
            public void onClick(View v) {

                if (alarms != null) {
                    //将选中的Alarminfor对象传送过去
                    Alarminfor alarminfor = alarms.get(position);
                    //通过Intent将要下载的歌曲名传递给播放mp3的activity
                    Intent intent = new Intent();
                    //intent.putExtra("QQinfor",AL_qqinfor);
                    intent.putExtra("alarminfor", alarminfor);
                    //设置标志位表明是设置闹钟
                    ADD_SET = false;
                    intent.putExtra("ADD_SET", ADD_SET);

                    intent.setClass(MainActivity.this, AlarmSetActivity.class);
                    //启动Activity
                    startActivity(intent);
                }
                // TODO Auto-generated method stub
                //Toast.makeText(AlarmListActivity.this, "点击了TextView第"+position, Toast.LENGTH_SHORT).show();

            }
        }
        class ImageListener implements View.OnClickListener {

            private int position;

            public ImageListener(int position){
                this.position=position;
            }                          //构造函数没有返回值

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //每次点击都要重新获取数据库中该条闹钟的状态
                alarms= dbHelper.find();
                //由点击的位置position，就可以知道你所，选中的alarminfor对象
                Alarminfor alarminfor = alarms.get(position);
                SharedPreferences mySharedPreferences= getSharedPreferences("user",
                        Activity.MODE_PRIVATE);
                //实例化SharedPreferences.Editor对象（第二步）
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                String  user_id=mySharedPreferences.getString("uid","");
                alarminfor.setUid(user_id);
                ArrayList<Alarminfor> alarminfors_send =new ArrayList<Alarminfor>();
                int alarmCount=alarminfor.getCount();
                String alarmCycle=alarminfor.getCycle();
                String  Time=alarminfor.getTime();
                //取出时间的小时和分钟
                String time[]=Time.split(":");
                int hour=Integer.parseInt(time[0]);
                int minute=Integer.parseInt(time[1]);
                //获取该该条闹钟的状态确定是开还是关
                int alarmState =alarminfor.getState();

                //创建Calendar对象
                Calendar calendar=Calendar.getInstance();
                //将系统时间设置给calendar
                calendar.setTimeInMillis(System.currentTimeMillis());
                //获取当前是周几及第几周
                int day=calendar.get(Calendar.DAY_OF_WEEK);
                int week =calendar.get(Calendar.WEEK_OF_MONTH);
                //由于calendar,从1到7是从周日开始转换
                int send_day=0;
                if(day==1){
                    send_day=7;
                }else{
                    send_day=day%7-1;
                }
                //将取出的时间设置给calendar
                calendar.set(Calendar.HOUR_OF_DAY,hour);
                calendar.set(Calendar.MINUTE,minute);
                calendar.set(Calendar.SECOND,0);
                // 选择的定时时间和当前时间转化成当前格式
                long currTime=System.currentTimeMillis();
                long selectTime = calendar.getTimeInMillis();

                //关闭状态，点击则需要开启闹钟
                if(alarmState==0){

                    if(alarmCycle.equals("只响一次")){
                        //设置时间大于当前时间
                        if(currTime>selectTime){
                            //弹出对话框，提醒用户重新设置时间
                            new AlertDialog.Builder(MainActivity.this).
                                    setTitle("重新设置").//设置标题
                                    setMessage("设置时间小于当前时间，请重新设置").//设置内容
                                    setPositiveButton("确定", new DialogInterface.OnClickListener(){//设置按钮
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();

                        }
                        else{
                            //注册该闹钟
                            register_cancel(alarmCount,selectTime,send_day,true,false);
                            //修改闹钟的图片
                            ((ImageView) v.findViewById(R.id.on_off)).setImageResource(R.drawable.turnon);
                            //将闹钟的State设置为1
                            alarminfor.setState(1);
                        }

                    }
                    else {
                        boolean[] selected = new boolean[]{false,false,false,false,false,false,false};
                        if(alarmCycle.equals("每天")){
                            selected = new boolean[]{true,true,true,true,true,true,true};
                        }
                        else if(alarmCycle.equals("工作日")){
                            selected = new boolean[]{true,true,true,true,true,false,false};
                        }
                        else if(alarmCycle.equals("周末")){
                            selected = new boolean[]{false,false,false,false,false,true,true};
                        }


                        for(int i=0;i<selected.length;i++)
                        {   //获取当前的周数

                            if(selected[i]==true)
                            {
                                //选择的数组的位置和星期对应，由于calendar周日是1，周一是2，依次
                                int dayofweek=(i+1)%7+1;

                                //由于设置的时间小于当前时间，则周数增加一
                                int weekofmonth=AlarmSetActivity.weekofmonth(i, day, week);

                                //设置DAY_OF_WEEK,周数
                                calendar.set(Calendar.DAY_OF_WEEK,dayofweek);
                                calendar.set(Calendar.WEEK_OF_MONTH,weekofmonth);
                                //setTime.setText(""+currtime);
                                // 选择的定时时间
                                long currtime=System.currentTimeMillis();
                                long selecttime = calendar.getTimeInMillis();
                                // 如果当前时间大于设置的时间，则增加一周
                                if(currTime> selectTime) {

                                    calendar.set(Calendar.WEEK_OF_MONTH,week+1);
                                    //重新设置选择的时间
                                    selectTime = calendar.getTimeInMillis();
                                }
                                SimpleDateFormat formatter=new SimpleDateFormat ("yyyy年MM月dd日    HH:mm:ss     ");
                                Date curDate= new Date(calendar.getTimeInMillis());//获取当前时间

                                String setTimes = formatter.format(curDate);

                                System.out.println("第几条记录"+i+"---"+setTimes);
                                //注册周期闹钟,由于第一个标志位用于区分闹钟，不然会被覆盖掉，用户闹钟一般不会超过1000，这样来标记
                                register_cancel(alarmCount+i*1000,selectTime,send_day,true,true);

                            }
                        }
                        //将闹钟的State设置为1
                        alarminfor.setState(1);
                        //修改闹钟的图片
                        ((ImageView) v.findViewById(R.id.on_off)).setImageResource(R.drawable.turnon);
                    }


                }
                //开启状态注销闹钟
                else{
                    //取消闹钟
                    for(int i=0;i<7;i++){
                        register_cancel(alarmCount+i*1000,0,0,false,false);
                    }
                    //将闹钟的状态设置为0
                    alarminfor.setState(0);
                    //显示关闭状态
                    //(Button) v.findViewById(R.id.on_off).setText()
                    ((ImageView) v.findViewById(R.id.on_off)).setImageResource(R.drawable.turnoff);
                }
                //将闹钟信息更新到数据库
                dbHelper.update(alarminfor);
                //将闹钟的State信息更新到服务器
                alarminfors_send.add(alarminfor);
                QQinfor qqinfor_aSate=new QQinfor();
                qqinfor_aSate.setAinfor_list(alarminfors_send);
                SendGet sendGet=new SendGet(qqinfor_aSate,a_Handler,4);
                sendGet.start();

            }

        }


        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub
            // Toast.makeText(AlarmListActivity.this, "点击了on_ffListView"+position, Toast.LENGTH_SHORT).show();
            /**
             * 首先判断是不是第一次创建Item，若是，则创建convertView实例和ViewHolder对象，并通过fandViewById()方法
             * 获得每一行中所有空间的实例放在ViewHolder对象中，然后对convertView设置标签
             */
            ViewHolder viewHolder=null;

            //注意convertView不是随意创建的，需要有LayoutInflater,根据list_item布局文件创建
            if(convertView==null){
                LayoutInflater inflater=LayoutInflater.from(context);
                convertView=inflater.inflate(resources,null);    //这里的null是一个ViewGroup形参，基本用不上
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            }
            else{
                viewHolder=(ViewHolder)convertView.getTag();    //通过getTag()方法获得附加信息
            }


            viewHolder.Gendertext.setText((String)(list.get(position).get(from[0])));
            viewHolder.Time.setText((String)(list.get(position).get(from[2])));
            viewHolder.Cycle.setText((String)(list.get(position).get(from[3])));
            if(viewHolder.Gendertext.getText().toString().equals("男")){
                viewHolder.Gender.setImageResource(R.drawable.man);
            }else if(viewHolder.Gendertext.getText().toString().equals("女")){
                viewHolder.Gender.setImageResource(R.drawable.woman);
            }else{
                viewHolder.Gender.setImageResource(R.drawable.beforesquar);
            }
            // viewHolder.on_off.setText((String) (list.get(position).get(from[3])));
            // viewHolder.on_off.setImageBitmap((Bitmap)list.get(position).get(from[3]));

            viewHolder.on_off.setOnClickListener(new ImageListener(position));
           // viewHolder.Gender.setOnClickListener(new TextViewListener(position));
            viewHolder.Time.setOnClickListener(new TextViewListener(position));
            viewHolder.Cycle.setOnClickListener(new TextViewListener(position));

            return convertView;     //把这个每一行的View对象返回
        }

    }

    //注册闹钟
    public  void register_cancel(int rcount,long selectTime,int send_day,boolean register_cancel,boolean isrepeat){
        //注册闹钟
        Intent intent=new Intent(MainActivity.this,AlarmReciever.class);
        //将闹钟的count 传送给闹钟播放程序
        intent.putExtra("send_day",send_day);
        intent.putExtra("count",rcount);
        //第二个参数用于标记设置的闹钟，用于闹钟的删除和更新，最后一个参数设置为这样才能床送putExtra,不然会是空值
        PendingIntent sender=PendingIntent.getBroadcast(MainActivity.this,rcount,intent, PendingIntent.FLAG_UPDATE_CURRENT);
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

    /***********************************clock*************************************/
    /****************************************square*****************************************/
private void  initialize_square(){
    QQinfor qq_send=new QQinfor();
    qq_send.setUid(username);
    qq_send.setGender(user_gender);
    qq_send.setOthergender(user_wantgender);
    if(user_wantgender.equals("随机")){
        new SendGet(qq_send,squHandler,80).start();
    }
    else{
        new SendGet(qq_send,squHandler,70).start();
    }
 }

    private void show_square(){

        //创建自定义的MyAdapter对象
        for(int i=0; i<squ_getlastlist.size(); i++){
            map=new HashMap<String,Object>();    //map调用put方法添加键值对
            map.put("img",squ_getlastlist.get(i).getBitmap());
            map.put("info",squ_getlastlist.get(i).getAlarmtime()+"需要被叫醒");
            alSquar.add(map);
        }
        squareListViewAdapter=new SquarListViewAdapter(this,alSquar);
        lvSquar.setAdapter(squareListViewAdapter);
        squareListViewAdapter.notifyDataSetChanged();
    }

    //创建SimpleAdapter
    private SimpleAdapter buildSimpleAdapter(){
        // 创建一个文件夹对象，赋值为外部存储器的目录
        File sdcardDir = Environment.getExternalStorageDirectory();
        SharedPreferences mysharedPreferences = getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mysharedPreferences.edit();
        String user_id = mysharedPreferences.getString("uid", "");
        String  path=sdcardDir.getPath()+"/Wakeup/user/alarm/records"+user_id;
        //获取录音文件名的列表
        List<Rname> Lrnames=getrecordFiles(path);
        // create a List object,and based on SimpleAdapter standard, put data in mp3Infos to List
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (Iterator iterator = Lrnames.iterator(); iterator.hasNext();) {
            Rname rname = (Rname) iterator.next();
            HashMap<String, String> map = new HashMap<String, String>();

            // map.put("uid", rname.getUid());
            map.put("count", ""+rname.getCount());
            map.put("stop","play");
            list.add(map);
        }
        // create a SimpleAdapter object
        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, list,
                R.layout.ringlistview, new String[] { "count", "stop" },
                new int[] { R.id.r_name,R.id.r_stop });


        //给listView的会造成焦点冲突 通过这种方式来设置button设置监听器
        SimpleAdapter.ViewBinder binder=new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if (view instanceof Button) {
                    final View button = view;
                    view.setOnClickListener(new View.OnClickListener() {
                        LinearLayout listItem = (LinearLayout) button.getParent();
                        TextView tv_id = (TextView) listItem.findViewById(R.id.r_name);

                        @Override
                        public void onClick(View view) {
                            Button bt_select =(Button) view;
                            int stu_id=Integer.parseInt(tv_id.getText().toString());

                            // 若这个button已被选中，这次点击则取消选中，并修改数据库
                            if(bt_select.getText().toString().equals("play")){

                                bt_select.setText("stop");


                            }
                            else{
                                bt_select.setText("play");
                                // bt_select.setClickable(false);

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
    public class SquarListViewAdapter extends BaseAdapter
    {
        ArrayList<HashMap<String,Object>> squarList;
        Context mContext;
        private LayoutInflater mInflater;
        public SquarListViewAdapter(Context context, ArrayList<HashMap<String,Object>> list)
        {
            this.mContext=context;
            this.squarList=list;
            this.mInflater= LayoutInflater.from(mContext);
        }
        @Override
        public int getCount()
        {
            return squarList.size();
        }
        @Override
        public Object getItem(int position)
        {
            return squarList.get(position);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }

        class ButtonListener implements View.OnClickListener {

            private int position;

            public ButtonListener(int position){
                this.position=position;
            }                          //构造函数没有返回值

            @Override
            public void onClick(View v) {

                aid=squ_getlastlist.get(position).getAid();
                dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("选择录音");
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View dialogView = factory.inflate(R.layout.dialog_layout, null);
                listView = (ListView) dialogView.findViewById(R.id.list);
                dialog.setView(dialogView);
                SimpleAdapter adapter=buildSimpleAdapter();
                listView.setAdapter(adapter);
                alertDialog=dialog.show();
                listView.setScrollbarFadingEnabled(true);

                //listView的监听器
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        // TODO Auto-generated method stub
                        TextView tv = (TextView) arg1.findViewById(R.id.r_name);//取得每条item中的textview控件
                        int count = Integer.parseInt(tv.getText().toString());
                        //获取SharedPreferences存取的uid

                        SharedPreferences mySharedPreferences = getSharedPreferences("user",
                                Activity.MODE_PRIVATE);
                        //实例化SharedPreferences.Editor对象（第二步）
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        String user_id = mySharedPreferences.getString("uid", "");
                        //由count和uid,生成rid
                        rid=user_id+"-"+count;
                        //将aid和rid封装到alarminfor对象中发送给服务器
                        Alarminfor alarminfor=new Alarminfor();
                        alarminfor.setAid(aid);
                        alarminfor.setRid(rid);
                        System.out.println("Aid--->"+alarminfor.getAid());
                        System.out.println("Rid--->"+alarminfor.getRid());
                        ArrayList<Alarminfor> alarminfors=new ArrayList<Alarminfor>();
                        alarminfors.add(alarminfor);
                        QQinfor  qQinfor=new QQinfor();
                        qQinfor.setAinfor_list(alarminfors);
                        //发送给服务器标记为25
                        SendGet sendGet=new SendGet(qQinfor,squHandler,25);
                        sendGet.start();

                        // et_name.setText(tv.getText().toString());
                        alertDialog.cancel();
                    }
                });
                // TODO Auto-generated method stub
            }

        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Holder viewHolder=new Holder();
            if(convertView==null)
            {
                convertView=mInflater.inflate(R.layout.squar_listview_layout,parent,false);
                viewHolder.btnCallUp=(Button)convertView.findViewById(R.id.btnCallUp);
                viewHolder.ivFriendHead=(ImageView)convertView.findViewById(R.id.ivFriendHead);
                viewHolder.tvAlarmInfo=(TextView)convertView.findViewById(R.id.tvAlarmInfo);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder=(Holder)convertView.getTag();
            }
            // viewHolder.ivFriendHead.setBackgroundResource((Integer)squarList.get(position).get("img"));
            viewHolder.ivFriendHead.setImageBitmap((Bitmap)squarList.get(position).get("img"));
            viewHolder.tvAlarmInfo.setText((String) squarList.get(position).get("info"));
            viewHolder.btnCallUp.setOnClickListener(new ButtonListener(position));
            return convertView;
        }
        public final class Holder
        {
            ImageView ivFriendHead;//存放好友头像
            TextView tvAlarmInfo;//显示用户多久需要被叫醒
            Button btnCallUp;//叫醒按钮
        }
    }
    /****************************************square*****************************************/
    /*******************************************message方法*********************************************/
    private  void initialize_message(){
        QQinfor QQ_getfriend=new QQinfor();
        QQ_getfriend.setpid(501);//拉取朋友页表
        QQ_getfriend.setUid(username_mess);
        new SendGet(QQ_getfriend, mess_Handler, 501).start();
    }
    /////展示好友列表////
    private void show_message(){

        //创建自定义的MyAdapter对象
        String[] from={"userPhoto","userName","userSign"};           //这里的内容对应后面HashMap中的键
        int[] to={R.id.ct_photo,R.id.ct_name,R.id.ct_sign};
        for(int i=0; i<qq_friendinfor_message.getFrineds_list().size(); i++){
            map_message=new HashMap<String,Object>();    //map调用put方法添加键值对
            map_message.put("userPhoto",pulllist.get(i).getBitmap());
            map_message.put("userName", pulllist.get(i).getName());
            map_message.put("userSign",pulllist.get(i).getChara());
            list1.add(map_message);
        }
        messadapter=new MessAdapter(this,R.layout.list_item,list1,from,to);
        //调用ListView的setAdapter()方法设置适配器
        chatListView.setAdapter(messadapter);
        messadapter.notifyDataSetChanged();//更新
    }

    /*******MessAdapter类********/
    public class MessAdapter extends BaseAdapter{
        private Context context=null;
        private int resources;
        private ArrayList<HashMap<String,Object>> list=null;
        private String[] from;
        private int[] to;

        public MessAdapter(Context context, int resources,
                         ArrayList<HashMap<String, Object>> list, String[] from, int[] to) {
            super();
            this.context = context;
            this.resources = resources;
            this.list = list;
            this.from = from;
            this.to = to;
        }

        /**
         * 剩下的问题就是依次实现BaseAdapter的这几个类方法就可以了
         */

        @Override
        public int getCount() {        //这个方法返回的是ListView的行数
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {      //这个方法没必要使用，可以用getItemId代替
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int itemId) {     //点击某一行时会调用该方法，其形参由安卓系统提供
            // TODO Auto-generated method stub
            return itemId;
        }

        /**
         * getView方法为系统在绘制每一行时调用，在此方法中要设置需要显示的文字，图片，
         * 以及为按钮设置监听器。
         *
         * 形参意义：
         * position：当前绘制的item 的位置（ID）；
         * convertView，系统在绘制ListView时，如果是绘制第一个Item（即第一行），convertView为null,当
         * 绘制第二个及以后的Item的convertView不为空，这时可以直接利用这个convertView的getTag()方法，获得各控件
         * 的实例，并进行相应的设置，这样可以加快绘图速度。
         *
         * 为了为convertView设置附加信息Tag，这里创建一个内部类ViewHolder，用于盛放一行中所有控件的引用，将这些引用
         * 实例化后作为convertView的附加信息。
         */
        class ViewHolder{
            public ImageButton ctPhoto=null;
            public TextView ctName=null,ctSign=null;

            /*
             * 从这里可以看出，from和to数组彼此之间的元素应该一一对应，同时from和to各自元素内部的顺序不同，最后ListView
             * 呈现的位置也会不同！
             */
            public ViewHolder(View convertView){
                ctPhoto=(ImageButton)convertView.findViewById(to[0]);
            /*注意View和Activity都属于容器类，都需要设置布局文件，内部都含有子控件，且都有findViewById()
             * 他们之间没有明显的继承关系
             */
                ctName=(TextView)convertView.findViewById(to[1]);
                ctSign=(TextView)convertView.findViewById(to[2]);

            }

        }

        class ImageListener implements OnClickListener{

            private int position;

            public ImageListener(int position){
                this.position=position;
            }                          //构造函数没有返回值

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent=new Intent();
                QQinfor chatobject=new QQinfor();//初始化
                chatobject.setUid(username_mess);//写入当前用户uid
                ArrayList<Friends> temp_list=new ArrayList<Friends>();
                Friends temp=new Friends();
                temp.setUid(pulllist.get(position).getUid());
                temp_list.add(temp);
                chatobject.setFrineds_list(temp_list);
                Bitmap chat_other=null;
                chat_other=pulllist.get(position).getBitmap();//获得下载好的好友头像
                intent.putExtra("QQinfor", chatobject);//装载QQInfor消息
                intent.putExtra("Obitmap",chat_other);//装载聊天好友头像
                intent.putExtra("Ubitmap",userbitmap);//装载自己头像
                intent.putExtra("Chatname",pulllist.get(position).getName());//记录名字
                intent.setClass(MainActivity.this,ChattwoActivity.class);//转入聊天界面
                startActivity(intent);
            }

        }

        class imagelongListener implements OnLongClickListener {
            private int position;

            public imagelongListener(int position){
                this.position=position;
            }                          //构造函数没有返回值

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确定要删除该消息?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // MyActivity.this.finish();
                                QQinfor qq_delete = new QQinfor();
                                qq_delete.setUid(username_mess);
                                qq_delete.setpid(503);
                                Friends delete_friend = new Friends();
                                ArrayList<Friends> delete_friends = new ArrayList<Friends>();
                                delete_friend.setUid(pulllist.get(position).getUid());//指定好友id
                                delete_friends.add(delete_friend);
                                qq_delete.setFrineds_list(delete_friends);//写入删除好友消息的信息
                                new SendGet(qq_delete, mess_Handler, 503).start();//发送服务代码
                                list1.remove(position);//删除指定行
                                messadapter.notifyDataSetChanged();//更新
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
                //Toast.makeText(MessageActivity.this, "长安", Toast.LENGTH_LONG).show();//getApplicationContext()
                return true;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub

            /**
             * 首先判断是不是第一次创建Item，若是，则创建convertView实例和ViewHolder对象，并通过fandViewById()方法
             * 获得每一行中所有空间的实例放在ViewHolder对象中，然后对convertView设置标签
             */
            ViewHolder viewHolder=null;

            //注意convertView不是随意创建的，需要有LayoutInflater,根据list_item布局文件创建
            if(convertView==null){
                LayoutInflater inflater=LayoutInflater.from(context);
                convertView=inflater.inflate(resources,null);    //这里的null是一个ViewGroup形参，基本用不上
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            }
            else{
                viewHolder=(ViewHolder)convertView.getTag();    //通过getTag()方法获得附加信息
            }
            /**
             * 这里对viewHolder中的各个控件进行相应的设置
             */
            /**
             * @author DragonGN
             * 这里出现了一个问题：在绘制当前行的ListItem时，只需要对当前行的控件进行设置，因此这里不能加一个for
             * 循环对每一个list中的每一个元素进行遍历，而应该根据当前创建的ListItem行的position,然后
             * 访问数据库list中相应位置的Map的数据，进行控件的设置！
             */

            /**
             * 注意这里必须是setBackgroundDrawable() 而不是setBackground(),后者会报错，尽管前者过期了但一样可用
             */
            // viewHolder.ctPhoto.setBackgroundDrawable((Drawable)(list.get(position).get(from[0])));
            viewHolder.ctPhoto.setImageBitmap((Bitmap) list.get(position).get(from[0]));
            //Map中要添加一个Drawable对象,这里的from和to中的元素应该一一对应,其顺序也应该对应ViewHolder构造方法中控件的调用的顺序

            viewHolder.ctName.setText((String)(list.get(position).get(from[1])));
            viewHolder.ctSign.setText((String) (list.get(position).get(from[2])));
            viewHolder.ctPhoto.setOnClickListener(new ImageListener(position));
            viewHolder.ctPhoto.setOnLongClickListener(new imagelongListener(position));//设定控件的点击事件
            viewHolder.ctSign.setOnLongClickListener(new imagelongListener(position));//设定控件的点击事件
            viewHolder.ctName.setOnLongClickListener(new imagelongListener(position));//设定控件的点击事件
            return convertView;     //把这个每一行的View对象返回
        }
    }
    /*******************************************message方法*********************************************/
    /********************************clock重写activity周期**************************9************/
   /* @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences mySharedPreferences= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        String  user_id=mySharedPreferences.getString("uid","");
        dbHelper=new DBHelper(MainActivity.this,user_id);
        show_alarms();
        //refreshList();
    }*/
   /******************************************message重写活动********************************************/
//重写方法使timer结束
   @Override
   protected void onStop() {
       //my implementation here
       timer1.cancel();//关闭定时器
       super.onStop();
   }
    @Override
    protected void onPause() {
        //my implementation here
        timer1.cancel();//关闭定时器
        super.onPause();
    }

    @Override
    protected void onResume() {
        //my implementation here
        if(timer1!=null){
            //定时器开启
            timer1=new Timer(true);
            task = new TimerTask(){
                public void run() {
                    QQinfor qq_refresh=new QQinfor();
                    qq_refresh.setpid(505);
                    qq_refresh.setUid(username_mess);
                    new SendGet(qq_refresh,mess_Handler,505).start();//刷新消息，如果有消息，服务器返回数据并刷新
                    //initialize();
                }
            };
            timer1.schedule(task, 1000, 1000);//一秒后启动，每1秒刷新一次消息
        }

        initialize_message();//更新消息列表
        super.onResume();
        /***********个性签名*******/
        //从SharedPreferences中获取个性签名
        SharedPreferences sharedPreference= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        String  character=sharedPreference.getString("Ucharacter","");
        String   Uname=sharedPreference.getString("Uname","");
        //将个性签名设置给该控件
        etSign.setText(character);
        tvName.setText(Uname);
        /******clock*****/
        SharedPreferences mySharedPreferences= getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        String  user_id=mySharedPreferences.getString("uid","");
        dbHelper=new DBHelper(MainActivity.this,user_id);
        show_alarms();
    }

    @Override
    protected void onDestroy() {
        //my implementation here
        timer1.cancel();//取消定时器
        super.onDestroy();
    }
    /******************************************message重写活动********************************************/
}
