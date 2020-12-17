package com.doctor;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorController {
}

@RestController
class Assignment{
    @Autowired
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    @Autowired
    HttpSession session;

    public void when_test(){
        session.setAttribute("kin", "doctor");
        session.setAttribute("statement", true);
        session.setAttribute("id", "001");
        session.setAttribute("sta_team",true);
    }

    //调用活动 done
    @RequestMapping("/doctor/as")
    public Aggregate assignment(String id){

        List<Map<String,Object>> list0 = jdbcTemplate.queryForList("select id,time_end,activity_id,date from activity_personal");
        list0.forEach((result)->help(result));
        if(session.getAttribute("kin")=="doctor") {
            Aggregate aggregate = new Aggregate(true);
            if(id==null) {
                List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from activity_personal ");
                list.forEach((result) -> ah(result, aggregate));
            }
            else {
                List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from activity_personal where id=" + id);
                list.forEach((result) -> ah(result, aggregate));
            }
            return aggregate;
        }
        else{
            Aggregate aggregate = new Aggregate(false);
            return aggregate;
        }
    }
    public void help(Map<String , Object> map){
        LocalDate localDate = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        java.util.Date date = java.util.Date.from(instant);
        String id = (String)map.get("id");
        String activity_id = (String)map.get("activity_id");
        Date date_sql = (Date)map.get("date");
        java.util.Date date_sel = new java.util.Date(date_sql.getTime());
        if(date_sel.compareTo(date)==-1){
            jdbcTemplate.update("update activity_personal set state=? where id=? and activity_id=?",true,id,activity_id);
        }
    }

    public void ah(Map<String,Object> map, Aggregate aggregate){
        Activity activity = new Activity(map);
        System.out.println(activity.type);
        aggregate.append(activity);
    }

    //修改活动 done

    @RequestMapping("/doctor/correct")
    public JSONObject correct(String activity_id, String date1, String time_start1, String time_end1, String detail, String type,boolean sta) {
        System.out.println(date1);
        Date date = Date.valueOf(date1);
        Time time_start = Time.valueOf(time_start1);
        Time time_end = Time.valueOf(time_end1);
        JSONObject jsonObject = new JSONObject();
        if (session.getAttribute("kin") == "doctor") {
            String id = (String) session.getAttribute("id");
            try {
                jdbcTemplate.update("update activity_personal set date=?,time_start=?,time_end=?,detail=?,type=?,state=? where id=? and activity_id=?", date, time_start, time_end, detail, type, sta, id, activity_id);
                jsonObject.put("state", "success");
            }
            catch (Exception e){
                jsonObject.put("state","NotEx");
            }
        }
        else
        {
            jsonObject.put("state","Login");
        }
        return  jsonObject;
    }

    //新建活动 done

    @RequestMapping("/doctor/new")
    public JSONObject newEstab(String date1,String time_start1, String time_end1, String detail, String type){

        Date date = Date.valueOf(date1);
        Time time_start = Time.valueOf(time_start1);
        Time time_end = Time.valueOf(time_end1);
        JSONObject object = new JSONObject();
        if (session.getAttribute("kin") == "doctor") {
            String id = (String) session.getAttribute("id");
            String activity_id = new String(String.valueOf(new java.util.Date().getTime()));
            jdbcTemplate.update("insert into activity_personal values(?,?,?,?,?,?,?,?)",id,activity_id,date,time_start,time_end,detail,type,false);
            object.put("state","success");
            object.put("activityid",activity_id);
        }
        else{
            object.put("state","Login");
            object.put("activityid","null");
        }
        return object;
    }

    //删除

    @RequestMapping("doctor/delete")
    public JSONObject del(String activity_id,String id){
        JSONObject object = new JSONObject();
        if(session.getAttribute("kin") == "doctor") {
            if (id == session.getAttribute("id") || (boolean) session.getAttribute("sta_team")) {
                try {
                    jdbcTemplate.update("delete from activity_personal where id=? and activity_id=?", id, activity_id);
                    object.put("state", "success");
                } catch (Exception e) {
                    object.put("state", "wrong");
                }
            } else {
                object.put("state", "noauthority");
            }
        }
        else{
            object.put("state", "Login");
        }
        return object;
    }

    //查看队伍 done

    @RequestMapping("/doctor/team")
    public List<Team> team(String id){
        List<Team> teams = new ArrayList<Team>();
        if(session.getAttribute("kin")=="doctor") {
            List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from team where leader_id=" + id);

            list.forEach((result) -> sh(result, teams));
        }
        else{
            Team team = new Team();
            teams.add(team);
        }
        return teams;
    }


    public void sh(Map<String,Object> map, List<Team> list){
        Team team = new Team(map);
        list.add(team);
    }
    //登录 done

    @RequestMapping("doctor/login")
    public JSONObject test(String id,String passwd1) {

//        String name = request.getParameter("abc");
        JSONObject object = new JSONObject();

//        String passwd1 = request.getParameter("passwd");
        try {
            String password = jdbcTemplate.queryForObject("select passwd from doctor where id = '" + id + "'", String.class);
            boolean sta = jdbcTemplate.queryForObject("select authority_team from doctor where id = '"+id+"'",boolean.class);
            System.out.println(password);
            System.out.println(passwd1);
            if (passwd1.equals(password) == true) {
                session.setAttribute("kin", "doctor");
                session.setAttribute("statement", true);
                session.setAttribute("id", id);
                session.setAttribute("sta_team",sta);
                object.put("state","success");
                return object;
            } else {
                object.put("state","passwrong");
                return object;
            }
        } catch (Exception e) {
            object.put("state","idwrong");
            return object;
        }

    }


    //建立团队已存在的团队加人 done

    @RequestMapping("/doctor/build")
    public JSONObject build( String id, int t_id){
        JSONObject object = new JSONObject();
        if(session.getAttribute("kin")=="doctor"){
            if((boolean)session.getAttribute("sta_team")) {

                int a = jdbcTemplate.queryForObject("select count(*) from team where t_id=" + t_id, int.class);//没有这个队伍
                int b = jdbcTemplate.queryForObject("select count(*) from doctor where id=" + id, int.class);//没有这个人
                if (a == 0 || b == 0) {
                    object.put("state", "notEx");
                } else {
                    try {
                        jdbcTemplate.update("insert into take_in values (?,?)", id, t_id);
                        object.put("state", "success");
                    } catch (Exception e) {
                        //记录已经添加
                        object.put("state", "alreadyin");
                    }
                }
            }
            else
            {
                object.put("state","noauthority");
            }
        }
        else{
            object.put("state","Login");
        }
        return object;
    }

    //返回所有不在组中的人 done

    @RequestMapping("/doctor/groupwindow")
    public  List<Map<String,Object>> check(int t_id){
        List<Map<String, Object>> list;
        if(session.getAttribute("kin")=="doctor") {
            list = jdbcTemplate.queryForList("select id,name from doctor where id not in (select id from take_in where team_id='" + t_id + "')");
            System.out.println(t_id);
        }
        else {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("state","Login");
            list = new ArrayList<Map<String, Object>>();
            list.add(map);
        }
        return list;
    }

    //返回所有医生信息 done

    @RequestMapping("/doctor/doctor_info")
    public List<Doctor> select(){
        List<Doctor> doctors = new ArrayList<Doctor>();
        if((boolean)session.getAttribute("statement")) {
            List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from doctor");
            list.forEach((result) -> kh(result, doctors));
        }
        else{
            Doctor doctor = new Doctor();
            doctors.add(doctor);
        }
        return doctors;
    }
    public void kh(Map<String,Object> map, List<Doctor> list){
        Doctor doctor = new Doctor(map);
        list.add(doctor);
    }

    //安排活动 done
    @RequestMapping("doctor/appointment")
    public JSONObject appointment(String id , Date date ,String type, Time starttime , Time endtime , String detail){
        JSONObject jsonObject = new JSONObject();
        if((boolean)session.getAttribute("statement")) {
            if (session.getAttribute("kin") == "doctor" && !(boolean) session.getAttribute("sta_team")) {
                jsonObject.put("state", "noauthority");
            } else {
                List<Time> start_times = new ArrayList<Time>();
                List<Map<String, Object>> temp;
                List<Time> end_times = new ArrayList<Time>();
                temp = jdbcTemplate.queryForList("select time_start from activity_personal where id=" + id);

                try {
                    jdbcTemplate.update("insert into activity_personal(id, date, time_start, time_end, detail,type,state)", id, date, starttime, endtime, detail, type, false);
                    jsonObject.put("state", "success");
                } catch (Exception e) {
                    jsonObject.put("state", "wrong");
                }
            }
        }
        else{
            jsonObject.put("state","Login");
        }
        return jsonObject;
    }


    //获取时间
    @RequestMapping("/doctor/time")
    public List<Map<String ,Object>> time(String id){
        List<Map<String,Object>> list = jdbcTemplate.queryForList("select time_start,time_end from activity_personal where id='"+id+"'");
        return list;
    }


}
@Configuration
class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}

