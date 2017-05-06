package com.xueba.quting.high.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wanglei on 2015/5/21.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private  String CREATE_TABLE="";
    private String tablename="";
    private Context m_context;
    public DBOpenHelper(Context context, String dbName, int dbVersion, String tablename, String sql){
        //必须调用父类中的构造函数
        super(context,dbName,null,dbVersion);
        CREATE_TABLE=sql;
        this.tablename=tablename;
        m_context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
        //Toast.makeText(m_context, "open db", Toast.LENGTH_LONG).show();
        System.out.println("数据库创建成功");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
       db.execSQL("drop table if exists"+tablename);

        onCreate(db);
    }

}
