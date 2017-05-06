package com.xueba.quting.high;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wanglei.qq.bean.QQinfor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quting on 2015/7/4.
 */
public class SquarActivity extends Activity {
    //参数
    ArrayList<HashMap<String,Object>>alSquar;
    SquarListViewAdapter squareListViewAdapter;
    int[]radom=new int[10];//用于存放广场列表的随机索引
    private QQinfor qq_friendinfor=null;//用于存放从数据库查询到的对象
    ArrayList<String> user_name;//存放用户昵称的数组
    ArrayList<String>user_sign;//存放用户个性签名
    ArrayList<String>user_head;//用于存放用户的头像的下载地址
    ArrayList<String>user_clock;//用于存放用户的闹钟
    ArrayList<Bitmap>head_bitmap;//用于将用户的头像转换为bitmap;
    //控件
    ListView lvSquar;
    Spinner spGender;
    private Handler rHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 2)//从服务器数据库返回值
            {

                for(int i=0;i<user_head.size();i++)//下载用户头像
                {
                    new com.xueba.quting.high.other.HttpDownloader(user_head.get(i),rHandler).start();
                }



            }

            if (msg.what == 1) //下载图片
            {


            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highsquar);
        //listView列表实现
        lvSquar=(ListView)findViewById(R.id.lvHighList);
        alSquar=new ArrayList<HashMap<String, Object>>();
        HashMap<String,Object> map1=new  HashMap<String,Object>();
        map1.put("img",R.drawable.myhead);
        map1.put("info","明日8:30需要被叫醒");
        alSquar.add(map1);
        HashMap<String,Object> map2=new  HashMap<String,Object>();
        map2.put("img",R.drawable.myhead);
        map2.put("info","后天10:30需要被叫醒");
        alSquar.add(map2);
        squareListViewAdapter=new SquarListViewAdapter(this,alSquar);
        lvSquar.setAdapter(squareListViewAdapter);
        //为广场中的listView添加长按点击事件

        //lvSquar.setOnItemLongClickListener(new ListViewLongClick());
         //Spinner实现
        spGender=(Spinner)findViewById(R.id.spGenderChoose);
        String[]list=getResources().getStringArray(R.array.gender);
        //为spinner设置一个适配器
        ArrayAdapter<String> sp_Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list);
        //定义下拉样式
        sp_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(sp_Adapter);
        //为广场中的Spinner添加选择事件
        spGender.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String str=parent.getItemAtPosition(position).toString();
                Toast.makeText(SquarActivity.this, "你点击的是:" + str,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        }) ;

    }
    //产生随机数索引广场列表
    public int[] CreateRadom()
    {
        int number=100;//定义随机数的范围
        List array=new ArrayList();//将1到100存放至此数组
        for(int i=0;i<100;i++)
        {
            array.add(i+1);
        }
        for(int j=0;j<radom.length;j++)
        {
            int index=(int) (Math.random() * number);//产生一个随机索引
            radom[j]=(int)array.get(index);
            array.remove(index);//移除已经取过的数
            number--;//将随机数的范围缩小
        }
        return radom;
    }

    //创建对话框
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SquarActivity.this);
        builder.setMessage("确认退出吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener()
        {   @Override
            public void onClick(DialogInterface dialog, int which)
         {
            dialog.dismiss();
        }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
        });
        builder.create().show();
    }
}
