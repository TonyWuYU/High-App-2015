package com.xueba.quting.high.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.wanglei.qq.bean.Alarminfor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglei on 2015/5/21.
 */
public class DBHelper {
    private  String DATABASE_NAME="mydbases";
    private  static  final  int  DATABASE_VERSION=1;
  //  private static final String TABLE_NAME="alarm";//数据库表名
    private String  TABLE_NAME;

    private static final  String[] COLUMNS={"aid","time","cycle","gender","state"};//数据库表字段名
    private String sql="";
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private Context m_context;
    public DBHelper(Context context, String uid){
         m_context=context;
        DATABASE_NAME="mydbases"+uid;
        TABLE_NAME="alarm"+uid;
        sql="create table "+ TABLE_NAME+"(aid,integer primary key,time text ,cycle text,gender text,state integer);";
        helper=new DBOpenHelper(context,DATABASE_NAME,DATABASE_VERSION,TABLE_NAME,sql);
        db=helper.getWritableDatabase();
    }

   public  void insert(Alarminfor alarminfor ){
        /*ContentValues 和HashTable类似都是一种存储的机制 但是两者最大的区别就在于，
        contenvalues只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西，而HashTable却可以存储对象。*/
       ContentValues values=new ContentValues();
        // values.put（KEY_TITLE, title）语句将列名和对应的列值放置到initialValues里边。
        values.put(COLUMNS[0],alarminfor.getCount());

      // System.out.println("Time"+alarminfor.getTime());
        values.put(COLUMNS[1],alarminfor.getTime());
        values.put(COLUMNS[2],alarminfor.getCycle());
        values.put(COLUMNS[3],alarminfor.getGender());
        values.put(COLUMNS[4],alarminfor.getState());

        db.insert(TABLE_NAME,null,values);
       Toast.makeText(m_context, "插入成功"+alarminfor.getTime(), Toast.LENGTH_LONG).show();


    }
   /* public void update_for_absent_1(int aid){
        db.execSQL("update alarm set score =score-1 where id ="+stu_id);
        //Toast.makeText(m_context, "修改成功", Toast.LENGTH_LONG).show();

    }
    public void update_for_absent_2(int stu_id){

        db.execSQL("update student set score =score +1 where id ="+stu_id);
       // Toast.makeText(m_context, "修改成功", Toast.LENGTH_LONG).show();

    }*/
   public void update(Alarminfor alarminfor){

        int aid=alarminfor.getCount();
        String time=alarminfor.getTime();
        String cycle=alarminfor.getCycle();
        String gender=alarminfor.getGender();
      //   contenvalues只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西，而HashTable却可以存储对象。*/
       ContentValues values=new ContentValues();
       values.put(COLUMNS[1],alarminfor.getTime());
       values.put(COLUMNS[2],alarminfor.getCycle());
       values.put(COLUMNS[3],alarminfor.getGender());
       values.put(COLUMNS[4],alarminfor.getState());
       String where="aid="+aid;

        db.update(TABLE_NAME,values,where,null);
       /* db.execSQL("update"+TABLE_NAME+ "set time="+"'"+time+"'"+" where aid ="+aid);
        db.execSQL("update"+TABLE_NAME+ "set cycle ="+"'"+cycle+"'"+" where aid ="+aid);
        db.execSQL("update"+TABLE_NAME+ "set gender ="+"'"+gender+"'"+" where aid ="+aid);*/

       Toast.makeText(m_context, "修改成功", Toast.LENGTH_LONG).show();

    }

    public void delete(Alarminfor alarminfor){

       /* int[] id={20120001,20120002,20120003,20120004,20120005,20120006,20120007,20120008,20120009,20120010};
        int id1=20120011;*/
        //删除id所在的行
        int count=alarminfor.getCount();
        db.delete(TABLE_NAME,COLUMNS[0]+"="+count,null);
        Toast.makeText(m_context, "删除成功", Toast.LENGTH_LONG).show();

    }
    public List<Alarminfor> find() {
        //动态数组
        ArrayList<Alarminfor> list = new ArrayList<Alarminfor>();
        Alarminfor alarminfor = null;
        //Cursor 是数据库每行的集合。
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, null, null, null, null, null);
        if(cursor!=null){
            while (cursor.moveToNext()) {
                //将数据库中查询到的信息封装到alarminfor
                alarminfor = new Alarminfor();
                alarminfor.setCount(cursor.getInt(0));
                alarminfor.setTime((cursor.getString(1)));
                //System.out.println("Time"+cursor.getString(1));
                alarminfor.setCycle(cursor.getString(2));
                alarminfor.setGender(cursor.getString(3));
                alarminfor.setState(cursor.getInt(4));
                list.add(alarminfor);

            }
        }

        cursor.close();//关闭游标
        return list;
    }


}
