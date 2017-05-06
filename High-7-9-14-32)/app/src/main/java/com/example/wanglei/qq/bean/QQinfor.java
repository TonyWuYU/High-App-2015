package com.example.wanglei.qq.bean;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wanglei on 2015/6/24.
 */
public class QQinfor  implements Serializable {
 private  static  final long serialVersionUID=1L;
public String  uid;
public String qqname;
public String  LogUrl;
public String  gender;

    public String getOthergender() {
        return othergender;
    }

    public void setOthergender(String othergender) {
        this.othergender = othergender;
    }

    public String othergender;
public String  character;
public int num_friends;
public String city;
public ArrayList<Alarminfor> ainfor_list;
public  ArrayList<Ring> rings_list;
public ArrayList<Friends>  frineds_list;


    public ArrayList<squareobject> getSquare_list() {
        return square_list;
    }

    public void setSquare_list(ArrayList<squareobject> square_list) {
        this.square_list = square_list;
    }

    public ArrayList<squareobject> square_list;



    public void setMessageinfor_list(ArrayList<Messageinfor> messageinfor_list) {
        Messageinfor_list = messageinfor_list;
    }

    public  ArrayList<Messageinfor>  Messageinfor_list;
public int pid;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setQqname(String qqname) {
        this.qqname = qqname;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLogUrl(String logUrl) {
        LogUrl = logUrl;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setNum_friends(int num_friends) {
        this.num_friends = num_friends;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public void setpid(int pid) {
        this.pid = pid;
    }



    public void setAinfor_list(ArrayList<Alarminfor> ainfor_list) {
        this.ainfor_list = ainfor_list;
    }
    public void setRings_list(ArrayList<Ring> rings_list) {

        this.rings_list = rings_list;
    }

    public void setFrineds_list(ArrayList<Friends> frineds_list) {
        this.frineds_list = frineds_list;
    }

    public String getUid() {
        return uid;
    }

    public String getQqname() {
        return qqname;
    }


    public String getGender() {
        return gender;
    }
    public String getLogUrl() {
        return LogUrl;
    }

    public String getCharacter() {
        return character;
    }

    public int getNum_friends() {
        return num_friends;
    }
    public String getCity() {
        return city;
    }
    public int getpid() {

        return pid;
    }

    public ArrayList<Alarminfor> getAinfor_list() {
        return ainfor_list;
    }
    public ArrayList<Ring> getRings_list() {
        return rings_list;
    }
    public ArrayList<Friends> getFrineds_list() {
        return frineds_list;
    }
    public ArrayList<Messageinfor> getMessageinfor_list() {return Messageinfor_list;}
}
