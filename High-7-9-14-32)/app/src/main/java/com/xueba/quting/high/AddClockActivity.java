package com.xueba.quting.high;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by quting on 2015/7/2.
 */
public class AddClockActivity extends Activity {
    //控件
    private TimePicker tpTimeSet;
    private Button btnSetAlarm;
    private ListView listView;
    private TextView tvGender;
    private TextView tvSetRate;

    //变量
    private ArrayAdapter rateAdapter;
    private AddClockListViewAdapter addClockListViewAdapter;
    int arrayPosition;
    int upMinute;
    int upHour;
    boolean[] selected = new boolean[]{false, false, false, false, false, false, false};//标志星期几被选
    private String[] choosedDate;
    private final int ZIDINGYI_MUTI_CHOICE_DIALOG = 1;
    private final int RATE_SINGLE_CHOICE_DIALOG = 2;
    private final long WEEK = 7 * 24 * 60 * 60 * 1000;
    ArrayList<HashMap<String, Object>> arrayList;
    Dialog dialog;
    LayoutInflater inflater;


    boolean isOnce = true;//判断闹钟是否只响一次,因为spinner里面默认第一个是只响一次，因此isOnce要设置成true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addclock);
        //绑定控件
        // spRate = (Spinner) findViewById(R.id.spRate);
        btnSetAlarm = (Button) findViewById(R.id.btnAddAlarm);
        listView = (ListView) findViewById(R.id.lvChoose);

        tpTimeSet = (TimePicker) findViewById(R.id.tpClock);
        tpTimeSet.setIs24HourView(true);
        //绑定按钮点击事件
        btnSetAlarm.setOnClickListener(new ClickListener());
        //listView adapter绑定
        arrayList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("第一行", "叫醒人性别");
        arrayList.add(map1);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map1.put("第二行", "重复");
        arrayList.add(map2);
        addClockListViewAdapter = new AddClockListViewAdapter(this, arrayList);
        listView.setAdapter(addClockListViewAdapter);
        //为listView添加点击事件
        listView.setOnItemClickListener(new onItemClickListener());
        inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.choosegender_listview_layout, null);
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.rgGender);
        final RadioButton suijiButton = (RadioButton) v.findViewById(R.id.rbSuiji);
        final RadioButton manButton = (RadioButton) v.findViewById(R.id.rbMan);
        final RadioButton womanButton = (RadioButton) v.findViewById(R.id.rbWoman);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == suijiButton.getId()) {
                    Toast.makeText(AddClockActivity.this, "随机", Toast.LENGTH_SHORT).show();
                } else if (checkedId == manButton.getId()) {
                    Toast.makeText(AddClockActivity.this, "男", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddClockActivity.this, "女", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //ListView的点击事件
    public class onItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg2 == 1)//如果重复行即第二行被选中
            {
                showDialog(RATE_SINGLE_CHOICE_DIALOG);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            //自定义对话框
            case ZIDINGYI_MUTI_CHOICE_DIALOG:
                AlertDialog.Builder zingdingyiBuilder = new AlertDialog.Builder(AddClockActivity.this);
                zingdingyiBuilder.setTitle("自定义");
                //将selected先清空一次
                selected = new boolean[]{false, false, false, false, false, false, false};
                DialogInterface.OnMultiChoiceClickListener zidingyiMultiListener = new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                        //把选择的那天标志为true
                        selected[which] = isChecked;
                    }
                };
                zingdingyiBuilder.setMultiChoiceItems(R.array.zidingyi, selected, zidingyiMultiListener);
                DialogInterface.OnClickListener zidingyiBtnListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                            }
                        };
                zingdingyiBuilder.setPositiveButton("确定", zidingyiBtnListener);
                dialog = zingdingyiBuilder.create();
                break;
            //重复对话框
            case RATE_SINGLE_CHOICE_DIALOG:
                AlertDialog.Builder rateBuilder = new AlertDialog.Builder(AddClockActivity.this);
                rateBuilder.setTitle("重复");
                DialogInterface.OnClickListener rateClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == 4) {
                            dialog.dismiss();
                            showDialog(ZIDINGYI_MUTI_CHOICE_DIALOG);
                        }
                    }
                };
                rateBuilder.setSingleChoiceItems(R.array.rate, 0, rateClickListener);
                DialogInterface.OnClickListener rateBtnListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {


                            }
                        };
                rateBuilder.setPositiveButton("确定", rateBtnListener);
                dialog = rateBuilder.show();
                break;
        }
        return dialog;
    }

    //点击事件
    public class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAddAlarm:
                    SetAlarm();
            }
        }
    }

    //确认添加闹钟
    public void SetAlarm()
    {

    }
}
