package com.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoctorController {
}
@RestController
class Assignment{
    @Autowired
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    //调用活动
    @RequestMapping("/doctor/as")
    public Aggregate assignment(String id){
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from activity_personal where id="+id);
        Aggregate aggregate = new Aggregate();

        list.forEach((result)->ah(result , aggregate));
        return aggregate;
    }

    public void ah(Map<String,Object> map, Aggregate aggregate){
        Activity activity = new Activity(map);
        System.out.println(activity.type);
        aggregate.append(activity);
    }

    //写入活动
    @RequestMapping("/doctor/save")
    public void save(HttpSession session, String activity_id,Date date, Time time_start, Time time_end, String detail, String type, String sta){
        String id = (String) session.getAttribute("id");
        jdbcTemplate.update("INSERT INTO activity_personal VALUES (?,?,?,?,?,?,?)",id,activity_id,date,time_start,time_end,detail,type,sta);
    }

    //查看队伍
    @RequestMapping("/doctor/team")
    public List<Team> team(String id){
        List<Map<String , Object>> list = jdbcTemplate.queryForList("select * from team where leader_id="+id);
        List<Team> teams = new ArrayList<Team>();
        list.forEach((result)->sh(result , teams));
        return teams;
    }


    public void sh(Map<String,Object> map, List<Team> list){
        Team team = new Team(map);
        list.add(team);
    }

    //建立团队
    @RequestMapping("/doctor/build")
    public int build(String id, int t_id){
        int a = jdbcTemplate.queryForObject("select count(*) from team where t_id="+t_id,int.class);
        int b = jdbcTemplate.queryForObject("select count(*) from doctor where id="+id,int.class);
        if(a==0||b==0){
            return 01;
        }
        else{
            try {
                jdbcTemplate.update("insert into take_in values (?,?)", id, t_id);
                return 11;
            }
            catch(Exception e){
                //记录已经添加
                return 10;
            }
        }
    }

    //返回所有不在组中的人
    @RequestMapping("/doctor/groupwindow")
    public  List<Map<String,Object>> check(int t_id){
        List<Map<String,Object>> list = jdbcTemplate.queryForList("select id,name from doctor where id not in (select id from take_in where team_id='"+t_id+"')");
        System.out.println(t_id);
        return list;
    }

    //返回所有医生信息
    @RequestMapping("/doctor/doctor_info")
    public List<Doctor> select(){
        List<Doctor> doctors = new ArrayList<Doctor>();
        List<Map<String , Object>> list = jdbcTemplate.queryForList("select * from doctor");
        list.forEach((result)->kh(result, doctors));
        return doctors;
    }
    public void kh(Map<String,Object> map, List<Doctor> list){
        Doctor doctor = new Doctor(map);
        list.add(doctor);
    }

}
