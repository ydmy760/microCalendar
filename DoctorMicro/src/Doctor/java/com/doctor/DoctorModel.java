package com.doctor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.sql.Date;
import java.sql.Time;
import java.util.*;

@SpringBootApplication
@EnableDiscoveryClient
public class DoctorModel {

    public static void main(String[] args) {
        SpringApplication.run(DoctorModel.class, args);
    }

}

class Doctor{
    public String statement;
    private String id;
    public String name;
    public String sex;
    public boolean authority_team;
    public String position;
    public String department;
    public String tel;
    public String e_mail;
    public String pic;
    public Doctor(Map<String,Object> map){
        statement = "success";
        id = (String)map.get("id");
        name = (String) map.get("name");
        sex = (String)map.get("sex");
        position = (String)map.get("position");
        authority_team = (boolean) map.get("authority_team");
        department = (String) map.get("department");
        tel = (String) map.get("tel");
        pic = (String)map.get("pic");
        e_mail = (String) map.get("e_mail");
    }
    public Doctor(){
        statement = "Login";
    }

}
class Timee{

}
class Aggregate{
    public String statement;
    public List<Activity> activities;
    public Aggregate(boolean a){
        if(a==true){
            statement="success";
        }
        else
            statement = "Login";
        activities = new ArrayList<Activity>();
    }
    public void append(Activity a){
        activities.add(a);
    }
}

class Team{
    public String statement;
    public int t_id;
    public Date date;
    public Time start_time;
    public String type;
    public String leader_id;
    public String theme;
    public boolean sta;//是否完成
    public Team(Map<String,Object> map){
        statement = "success";
        t_id = (int)map.get("t_id");
        date = (Date)map.get("date");
        start_time = (Time)map.get("time");
        type = (String)map.get("type");
        leader_id = (String)map.get("leader_id");
        theme = (String) map.get("theme");
        sta = (boolean)map.get("statement");
    }
    public Team(){
        statement = "Login";
    }
}
//与会人员
class Take_in{
    public String id;
    public int t_id;
    public Take_in(String id, int t_id){
        this.id = id;
        this.t_id = t_id;
    }
}
class All_Take{
    public List<Take_in> meets;
    public All_Take(){
        meets = new ArrayList<Take_in>();
    }
    public void Add(String id, int t_id){
        Take_in take_in = new Take_in(id,t_id);
        meets.add(take_in);
    }
}
//事物
class Activity{
    private String id;//医生编号
    public String activity_id;
    public Date date;//日期
    public Time time_start;//事件开始
    public Time time_end;//事件结束
    public String detail;//事件说明
    public String type;//事件类型
    public boolean state;//事件标志

    public Activity(Map<String, Object> map){
        id = (String) map.get("id");
        activity_id = (String) map.get("activity_id");
        date = (Date) map.get("date");
        time_start = (Time) map.get("time_start");
        time_end = (Time) map.get("time_end");
        detail = (String) map.get("detail");
        type = (String) map.get("type");
        state = (boolean) map.get("state");
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setType(String type) {
        this.type = type;
    }
}