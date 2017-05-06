package com.example.wanglei.qq.bean;
import java.io.Serializable;
/**
 * Created by TonyWu1 on 2015/7/5.
 */
public class squareobject implements Serializable {
    private  static  final long serialVersionUID=1L;
   private  String uid;
   private  String logourl;
   private   String alarmtime;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    private String aid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public String getAlarmtime() {
        return alarmtime;
    }

    public void setAlarmtime(String alarmtime) {
        this.alarmtime = alarmtime;
    }
}
