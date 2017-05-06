package com.xueba.quting.high;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Created by quting on 2015/7/6.
 */
public class WelcomeActivity extends Activity
{
    protected boolean active = true;
    protected int splashTime = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomepage);
        Thread splashthread =new Thread()
        {
            @Override
            public void run()
            {
                try{
                int waitTime=0;
                while(active&&waitTime<splashTime)
                {
                    sleep(100);
                    if(active)
                        waitTime+=100;

                }
                }catch(InterruptedException e){

                }finally
                {
                    finish();
                    Intent intent=new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
        splashthread.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            active = false;
        }
        return true;
    }
}
