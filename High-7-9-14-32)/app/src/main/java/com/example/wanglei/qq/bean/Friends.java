package com.example.wanglei.qq.bean;

import java.io.Serializable;

/**
 * Created by wanglei on 2015/6/29.
 */
public class Friends implements Serializable {
    private  static  final long serialVersionUID=1L;
    private String uid;
    private int  friends_id;
    private String chara;
    private String gender;
    private String logourl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getChara() {
        return chara;
    }

    public void setChara(String chara) {
        this.chara = chara;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }



    public void setFriends_id(int friends_id) {
        this.friends_id = friends_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {

        this.uid = uid;
    }

    public int getFriends_id() {
        return friends_id;
    }



}
