package com.example.wanglei.qq.bean;

import java.io.Serializable;

/**
 * Created by wanglei on 2015/6/29.
 */
public class Alarminfor implements Serializable {
    private  static  final long serialVersionUID=1L;
    private String uid;
    private String  aid;
    private String rid;
    private String Cycle;
    private String Time;
    private  String Gender;
    private  int State;
    private int count;
    private  int day;
    private int valstate;
    private int isOne;

    public int getIsOne() {
        return isOne;
    }

    public void setIsOne(int isOne) {

        this.isOne = isOne;
    }

    public int getValstate() {
        return valstate;
    }

    public void setValstate(int valstate) {

        this.valstate = valstate;
    }

    private boolean update_cycle;

    public boolean isUpdate_cycle() {
        return update_cycle;
    }

    public void setUpdate_cycle(boolean update_cycle) {

        this.update_cycle = update_cycle;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {

        this.day = day;
    }

    public String getUid() {
        return uid;
    }



    public String getCycle() {
        return Cycle;
    }

    public String getTime() {
        return Time;
    }

    public String getGender() {
        return Gender;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {

        this.count = count;
    }


    public void setUid(String uid) {

        this.uid = uid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {

        this.aid = aid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {

        this.rid = rid;
    }

    public void setCycle(String cycle) {
        Cycle = cycle;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {

        State = state;
    }
}

