package com.xueba.quting.high;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

/**
 * Created by quting on 2015/7/4.
 */
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
        viewHolder.ivFriendHead.setBackgroundResource((Integer)squarList.get(position).get("img"));
        viewHolder.tvAlarmInfo.setText((String)squarList.get(position).get("info"));
        return convertView;
    }
    public final class Holder
    {
        ImageView ivFriendHead;//存放好友头像
        TextView tvAlarmInfo;//显示用户多久需要被叫醒
        Button btnCallUp;//叫醒按钮
    }
    //重写长按类
    /* public class ListViewLongClick implements AdapterView.OnLongClickListener {
        public int position;
        public ListViewLongClick(int arg1) {
            this.position = arg1;
        }

        @Override
       public boolean onLongClick(View v)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("确认要删除吗？");
            builder.setTitle("提示");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener()
            {   @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    squarList.remove(position);

                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return true;
        }
    }*/
}
