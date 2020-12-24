package com.patient;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient

public class PatientModel {
    public static void main(String[] args) {
        SpringApplication.run(PatientModel.class, args);
    }

}
//定义医生类
class Doctor{
    public String statement;
    public String id;
    public String name;
    public String sex;
    public String position;
    public String department;
    public String pic;
    public String detail;
    public Doctor(Map<String,Object> map){
        statement = "success";
        id = (String)map.get("id");
        name = (String) map.get("name");
        sex = (String)map.get("sex");
        position = (String)map.get("position");
        department = (String) map.get("department");
        pic = (String)map.get("pic");
        detail = (String)map.get("detail");
    }
    public Doctor(){
        statement = "Login";
    }

}
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
class Time_use{
    public Time start_time;
    public Date date;
    public int capacity;
    public String doctor;
    public int item;
    public Time_use(Map<String,Object> map){
        date = (Date)map.get("date");
        capacity = (int)map.get("capacity");
        doctor = (String)map.get("doctor_id");
        item = (int)map.get("item_id");
    }
}
class Aggregate{
    public String statement;
    public List<Time_use> time_uses;
    public Aggregate(){
        statement="success";
        time_uses = new ArrayList<Time_use>();
    }
    public void append(Time_use a){
        time_uses.add(a);
    }
}

//定义患者类
class Patient{
    private String id;
    private String passwd;
    public String name;
    public String sex;
    public String detail;
    public int age;
    public String tel;
    public Patient(String id, String passwd, String name, String sex, int age, String detail, String tel){
        this.id = id;
        this.passwd = passwd;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.detail = detail;
        this.tel = tel;
    }
    public void setId(String id){
        this.id=id;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}

