package com.patient;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class PatientModel {
    public static void main(String[] args) {
        SpringApplication.run(PatientModel.class, args);
    }

}
//定义医生类
class Doctor {
    private int id;
    public String name;
    public String department;
    public String tel;
    public int age;
    public String details;
    public Doctor(){

    }
    public Doctor(String s){
        name = s;
        department = s;
        tel = s;
        details = s;

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTel(String tel) {
        this.tel = tel;
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

