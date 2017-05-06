package com.xueba.quting.high;

/**
 * Created by quting on 2015/7/2.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wanglei on 2015/7/4.
 */
public class AlarmReciever extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {


        //获取传送过来的count
        int count=intent.getIntExtra("count",0);
        int day=intent.getIntExtra("send_day",0);

        Intent start_intent = new Intent(context, AlarmAlert.class);
        //再次将闹钟发送给AlarmAlert
        start_intent.putExtra("count",count);
        start_intent.putExtra("day",day);
        start_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(start_intent);
    }
}
