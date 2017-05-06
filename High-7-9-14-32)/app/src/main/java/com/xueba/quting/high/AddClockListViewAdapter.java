package com.xueba.quting.high;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by quting on 2015/7/3.
 */
public class AddClockListViewAdapter extends BaseAdapter
{
    ArrayList<HashMap<String, Object>> ls;
    Context mContext;
    LayoutInflater inflater;
    final int TYPE_GENDER = 0;
    final int TYPE_RATE = 1;
    boolean isMan;
    boolean isWoman;
    boolean isSuiji;
    public AddClockListViewAdapter(Context context, ArrayList<HashMap<String, Object>> list)
    {
        mContext = context;
        this.ls=list;
        inflater = LayoutInflater.from(mContext);
        isMan=true;
        isWoman=false;
        isSuiji=false;

    }

    @Override
    public int getCount() {
        return ls.size();
    }

    @Override
    public Object getItem(int position) {
        return ls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        int p = position;
        if (p == 0)
            return TYPE_GENDER;
        else
            return TYPE_RATE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            inflater = LayoutInflater.from(mContext);
            Log.e("convertView = ", " NULL");
            // 按当前所需的样式，确定new的布局
            switch (type) {
                case TYPE_GENDER:
                    convertView = inflater.inflate(R.layout.choosegender_listview_layout, parent, false);
                    holder1 = new ViewHolder1();
                    holder1.rgGender=(RadioGroup)convertView.findViewById(R.id.rgGender);
                    holder1.rbMan=(RadioButton)convertView.findViewById(R.id.rbMan);
                    holder1.rbWoman=(RadioButton)convertView.findViewById(R.id.rbWoman);
                    holder1.rbSuiji=(RadioButton)convertView.findViewById(R.id.rbSuiji);
                    /*holder1.rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId)
                        {
                            //获取变更后的选中项的ID
                          int buttonId=group.getCheckedRadioButtonId();

                          if(buttonId==R.id.rbMan)
                          {

                              isMan=true;
                              isWoman=false;
                              isSuiji=false;
                              Log.e("man:","selected");
                          }
                          else if (buttonId==R.id.rbWoman)
                          {
                              isWoman=true;
                              isMan=false;
                              isSuiji=false;
                              Log.e("woman:","selected");
                          }
                            else
                          {
                              isSuiji=true;
                              isWoman=false;
                              isMan=false;
                              Log.e("suiji:","selected");
                          }
                        }
                    });*/
                    Log.e("convertView = ", "NULL TYPE_GENDER");
                    convertView.setTag(holder1);
                    break;
                case TYPE_RATE:
                    convertView = inflater.inflate(R.layout.setdate_listview_layout, parent,false);
                    holder2 = new ViewHolder2();
                    holder2.tvRate=(TextView)convertView.findViewById(R.id.tvRate);
                    Log.e("convertView = ", "NULL TYPE_RATE");
                    convertView.setTag(holder2);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_GENDER:
                    holder1 = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE_RATE:
                    holder2 = (ViewHolder2) convertView.getTag();
                    break;
            }
        }
        //设置资源
        switch (type) {
            case TYPE_GENDER:
                holder1.rbMan.setText("男");
                holder1.rbWoman.setText("女");
                holder1.rbSuiji.setText("随机");
                break;
            case TYPE_RATE:
                holder2.tvRate.setText("每天");
                break;
        }
        return convertView;
    }

    public class ViewHolder1 {
        RadioGroup rgGender;
        RadioButton rbMan;
        RadioButton rbWoman;
        RadioButton rbSuiji;
    }

    public class ViewHolder2
    {
        TextView tvRate;
    }
}

