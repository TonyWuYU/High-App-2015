package com.xueba.quting.high;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.wanglei.qq.bean.QQinfor;
import com.xueba.quting.high.Thread.SendGet;

/**
 * Created by quting on 2015/7/7.
 */
public class EditSignActivity extends Activity
{

    private ImageView ivBack;
    private EditText et_character;
    private ImageView bt_sure;
    // private Handler handler;
    private Handler handler = new Handler() {
        //处理消息队列中的消息
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==2)
            {
                QQinfor  qqinforGet=(QQinfor)msg.obj;

            }
        }

    };
    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editsign);
        ivBack=(ImageView)findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new ClickListener());
        et_character=(EditText)findViewById(R.id.et_character);
        bt_sure=(ImageView)findViewById(R.id.bt_sure);
        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QQinfor qqinfor=new QQinfor();
                //获取用户更换的昵称和签名
                String  character=et_character.getText().toString();
                //输入信息，不能为空
                if(character!=null){
                    //将修改后的信息发送给服务器
                    qqinfor.setCharacter(character);
                    SendGet sendGet=new SendGet(qqinfor,handler,2);
                    //将修改后的签名存入 SharedPreferences
                    //获取SharedPreferences存取的uid
                    SharedPreferences mySharedPreferences= getSharedPreferences("user",
                            Activity.MODE_PRIVATE);
                    //实例化SharedPreferences.Editor对象（第二步）
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putString("Ucharacter",character);
                    //提交当前数据
                    editor.commit();

                }
                else{
                    new AlertDialog.Builder(EditSignActivity.this).
                            setTitle("内容不能为空").//设置标题
                            setMessage("请重新填写").//设置内容
                            setPositiveButton("确定", new DialogInterface.OnClickListener(){//设置按钮
                        public void onClick(DialogInterface dialog, int which) {
                            EditSignActivity.this.finish();//关闭Activity
                        }
                    }).create().show();
                }
              finish();
            }
        });
    }
    public class ClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            EditSignActivity.this.finish();
        }

    }
}
