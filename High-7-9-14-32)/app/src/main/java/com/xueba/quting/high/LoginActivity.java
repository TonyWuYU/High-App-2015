package com.xueba.quting.high;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wanglei.qq.bean.QQinfor;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xueba.quting.high.Thread.SendGet;

import org.json.JSONObject;

import java.io.File;


public class LoginActivity extends Activity {

    private static final String APPID = "1104654981";
    private TextView textView;
    private final String  qqurl=null;
    //千万注意不要只声明，而不初始化
    public final static QQinfor qqinfor = new QQinfor();
    //从服务器获取来的QQinfor对象
    private QQinfor qQinforGet=new QQinfor();

    private Button bt_out,bt_getinfo,bt_upfile;
    private ImageView bt_log;
    private ImageView mUserLogo;

    private Tencent mTencent; //Tecent由jar包提供主操作对象
    private String scope; //获取信息的范围参数,一般为all
    private UserInfo userInfo; //qq用户信息
    private IUiListener loginListener; //授权登录监听器
    private IUiListener userInfoListener; //获取用户信息监听器

    // private Handler handler;
    private Handler mHandler = new Handler() {
        //处理消息队列中的消息
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==2)
            {
                qQinforGet=(QQinfor)msg.obj;
                SharedPreferences mySharedPreferences= getSharedPreferences("user",
                        Activity.MODE_PRIVATE);
                //实例化SharedPreferences.Editor对象（第二步）
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                //将Uid放入对应的键值对
                editor.putString("uid",qQinforGet.getUid());
                //放入用户名和个性签名
                editor.putString("Ucharacter",qQinforGet.getCharacter());
                editor.putString("Uname",qQinforGet.getQqname());

                editor.commit();
                Intent intent=new Intent();
                intent.putExtra("QQinfor",qQinforGet);
                // intent.putExtra("Bitmap",bitmap);
                intent.setClass(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化界面，并且给控件设置相应的监听器
        initView();

    }
    //初始化界面
    private  void initView(){
        //设置activity的layout
        setContentView(R.layout.activity_login);
        //获取控件，并且给控件设置监听器
        setView();
        setListener();
    }
    private void setView() {
        bt_log=(ImageView)findViewById(R.id.btnLogin);
        //给使用QQ登录按钮设置监听器
        bt_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行login()
                login();
            }
        });
    }
    /*
    初始化Tencent对象，并且设置授权监听器，和获取用户信息监听器
    * */
    private void setListener() {
        //通过传入c参数APPID，context初始化Tencent主操作对象由导入的jar包提供
        mTencent = Tencent.createInstance(APPID, LoginActivity.this);
        //要所有权限，不然会再次申请增量权限，
        scope = "all";
        //通过重写jar包提供的 IUiListener来实例化授权登录监听器 loginListener
        loginListener = new IUiListener() {

            @Override
            public void onError(UiError arg0) {
                // TODO Auto-generated method stub

            }

            //回调处理返回的json对象
            @Override
            public void onComplete(Object value) {
                // TODO Auto-generated method stub

                System.out.println("有数据返回..");
                if (value == null) {
                    return;
                }

                try {
                    JSONObject jo = (JSONObject) value;
                    // 注意返回的json对象"ret"对应的键值是用来标记是否授权登录成功，若为0，则登录成功，这里非常重要
                    int ret = jo.getInt("ret");
                    System.out.println("json=" + String.valueOf(jo));
                    //返回的ret所对应的键值为0，则表示授权登陆成功
                    if (ret == 0) {
                        //添加显示登陆成功的Toast
                      //  Toast.makeText(LoginActivity.this, "登录成功",
                              //  Toast.LENGTH_LONG).show();
                        //登录成功后刷新界面，并对相应控件设置监听器
                        //  updateView();
                        setContentView(R.layout.activity_loading);
                        //实例化用户信息对象
                        userInfo = new UserInfo(LoginActivity.this, mTencent.getQQToken());
                        //给用户信息对象设置监听器
                        userInfo.getUserInfo(userInfoListener);
                        //实例化用户信息对象

                        //获取返回的json对象中"openid"，"access_token"，"expires_in"所对应的键值
                        String openID = jo.getString("openid");
                        String accessToken = jo.getString("access_token");
                        String expires = jo.getString("expires_in");
                        //将openID设置为qqinfor的uid
                        qqinfor.setUid(openID);
                        //将返回的值设置给使用的Tencent对象的值
                        mTencent.setOpenId(openID);
                        mTencent.setAccessToken(accessToken, expires);


                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        };

        //通过重写jar包提供的 IUiListener来实例化获取用户信息监听器  userInfoListener
        userInfoListener = new IUiListener() {

            @Override
            public void onError(UiError arg0) {
                // TODO Auto-generated method stub

            }

            //回调处理返回的json对象
            @Override
            public void onComplete(Object arg0) {
                // TODO Auto-generated method stub
                if(arg0 == null){
                    return;
                }
                try {
                    final  JSONObject jo = (JSONObject) arg0;
                    int ret = jo.getInt("ret");
                    System.out.println("json=" + String.valueOf(jo));
                    //获取返回的用户名
                    String nickName = jo.getString("nickname");
                    //将nickname/设置为qqinfor的QQname
                    qqinfor.setQqname(nickName);
                    //获取用户的性别
                    String gender = jo.getString("gender");
                    qqinfor.setGender(gender);
                    //获取省份和城市
                    String province=jo.getString("province");
                    String  city=jo.getString("city");
                    System.out.print("city--->"+city);
                    qqinfor.setCity(province+"-"+city);

                    //Toast显示用户名
                    Toast.makeText(LoginActivity.this, "你好，" + nickName,
                            Toast.LENGTH_LONG).show();
                    // textView.setText(""+nickName);

                    Bitmap bitmap = null;
                    String url=jo.getString("figureurl_qq_2");
                    //设置好友头像的url
                    // qqurl=url;
                    qqinfor.setLogUrl(url);
                    qqinfor.setNum_friends(11);
                    qqinfor.setCharacter("你很好！");
                    SendGet sendGet=new SendGet(qqinfor,mHandler,1);
                    sendGet.start();

                } catch (Exception e) {
                    // TODO: handle exception
                }


            }
            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        };

    }
    /*：在某些低端机上调用登录后，由于内存紧张导致APP被系统回收，登录成功后无法成功回传数据。
解决办法如下
在调用login的Activity或者Fragment重写onActivityResult方法：*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.RESULT_LOGIN) {
                Tencent.handleResultData(data, loginListener);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void login() {
        //session（session由jar包提供，未登录成功则session为无效）无效即需要登录
        if (!mTencent.isSessionValid()) {
            //jar包提供Tencent对象传入参数context，scope, loginListener即可授权登录（获取信息的范围参数,一般为all）
            mTencent.login(LoginActivity.this, scope, loginListener);
        }
    }
    //登录成功后更新界面

    //获取MP3文件的路径
    public  static String get3Path(){
        String SDCardRoot= Environment.getExternalStorageDirectory().getAbsolutePath();
        String path=SDCardRoot+ File.separator+"mp3"+File.separator+"12.mp3";
        return path;
    }
    @Override
    protected void onDestroy() {
        if (mTencent != null) {
            //注销登录
            mTencent.logout(LoginActivity.this);
        }
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
