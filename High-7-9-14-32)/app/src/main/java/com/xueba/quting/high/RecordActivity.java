package com.xueba.quting.high;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by quting on 2015/7/6.
 */
public class RecordActivity  extends Activity
{
    //控件
    ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myrecord);
        imgBack=(ImageView)findViewById(R.id.ivBack);
        imgBack.setOnClickListener(new ClickListener());
    }
    public class ClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.ivconfirm:
                   SaveData();
                    break;
                case R.id.ivBack:
                    RecordActivity.this.finish();//关闭当前activity
                    break;
                default:
                    break;
            }

        }
    }
    public void SaveData()
    {
        Toast.makeText(getApplicationContext(),"存储数据",Toast.LENGTH_SHORT).show();
        RecordActivity.this.finish();
    }
}
