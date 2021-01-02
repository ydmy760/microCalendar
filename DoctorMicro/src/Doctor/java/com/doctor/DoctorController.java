package com.doctor;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
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
import java.util.*;
import org.springframework.web.client.RestTemplate;

public class DoctorController {
}

@Configuration
@RestController
class Assignment {
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Autowired
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    @Autowired
    HttpSession session;
    @Autowired
    private RestTemplate restTemplate;

    public void when_test() {
        session.setAttribute("kin", "doctor");
        session.setAttribute("statement", true);
        session.setAttribute("id", "2020001");
        session.setAttribute("sta_team", true);
    }


    //调用活动 done
    @RequestMapping("/doctor/as")
    public Aggregate assignment() {
        //when_test();
        List<Map<String, Object>> list0 = jdbcTemplate.queryForList("select id,time_end,activity_id,date,type from activity_personal");
        list0.forEach((result) -> help(result));
        if (session.getAttribute("kin") == "doctor") {
            String id=(String)session.getAttribute("id");
            Aggregate aggregate = new Aggregate(true);
            if (id == null) {
                List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from activity_personal ");
                list.forEach((result) -> ah(result, aggregate));
            } else {
                List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from activity_personal where id=" + id);
                list.forEach((result) -> ah(result, aggregate));
            }
            return aggregate;
        } else {
            Aggregate aggregate = new Aggregate(false);
            return aggregate;
        }
    }

    public void help(Map<String, Object> map) {
        LocalDate localDate = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        java.util.Date date = java.util.Date.from(instant);
        String id = (String) map.get("id");
        String activity_id = (String) map.get("activity_id");
        Date date_sql = (Date) map.get("date");
        String type = (String) map.get("type");
        System.out.println(type);
        java.util.Date date_sel = new java.util.Date(date_sql.getTime());
        if (date_sel.compareTo(date) == -1 && !Objects.equals(type, "changed")) {
            jdbcTemplate.update("update activity_personal set state=? where id=? and activity_id=?", true, id, activity_id);
        }
    }

    public void ah(Map<String, Object> map, Aggregate aggregate) {
        Activity activity = new Activity(map);
        System.out.println(activity.type);
        aggregate.append(activity);
    }


    //获取同科室的医生名单
    @RequestMapping("/doctor/get_member")
    public List<Map<String, Object>> geta(String doctor_id) {
        if (session.getAttribute("kin") == "doctor") {
            if ((boolean) session.getAttribute("sta_team")) {
                try {
                    List<Map<String, Object>> list = jdbcTemplate.queryForList("select id,name from doctor where department=(select department from doctor where id=?)", doctor_id);
                    return list;
                } catch (Exception e) {
                    List<Map<String, Object>> list = new ArrayList<>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("state", "wrong_db");
                    list.add(map);
                    return list;
                }
            } else {
                List<Map<String, Object>> list = new ArrayList<>();
                Map<String, Object> map = new HashMap<>();
                map.put("state", "id_wrong");
                list.add(map);
                return list;
            }
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("state", "Login");
            list.add(map);
            return list;
        }
    }

    //分配门诊
    @RequestMapping("/doctor/arr")
    public JSONObject arrangement(String doctor_id, int number, String date1) {
        JSONObject object = new JSONObject();
        if (session.getAttribute("kin") == "doctor") {
            if ((boolean) session.getAttribute("sta_team")) {
                Time start_time;
                Time end_time;
                Date date = Date.valueOf(date1);
                String activity_id = new String(String.valueOf(new java.util.Date().getTime()));
                String id = (String) session.getAttribute("id");
                if (number == 1) {
                    start_time = Time.valueOf("08:00:00");
                    end_time = Time.valueOf("11:30:00");
                } else {
                    start_time = Time.valueOf("14:00:00");
                    end_time = Time.valueOf("17:30:00");
                }
                Map<String, Object> map = new HashMap<>();
                map.put("doctor_id", doctor_id);
                map.put("date1", date1);
                map.put("number", number);
                try {
                    jdbcTemplate.update("insert into activity_personal values (?,?,?,?,?,?,?,?)", doctor_id, activity_id, date, start_time, end_time, "门诊", id, false);
                    boolean a = restTemplate.getForObject("http://micro_calender_patient/patient/add_doc?doctor_id={doctor_id}&date1={date1}&number={number}", boolean.class, map);
                    object.put("state", "success");
                } catch (Exception e) {
                    object.put("state", "wrong_db");
                }
            } else {
                object.put("state", "wrong_id");
            }
        } else {
            object.put("state", "Login");
        }
        return object;
    }

    //修改活动 done

    @RequestMapping("/doctor/correct")
    public JSONObject correct(String activity_id, String date1, String time_start1, String time_end1, String detail, String type, boolean sta) {
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
            } catch (Exception e) {
                jsonObject.put("state", "NotEx");
            }
        } else {
            jsonObject.put("state", "Login");
        }
        return jsonObject;
    }


    @RequestMapping("/doctor/new")
    public JSONObject newEstab(String date1, String time_start1, String time_end1, String detail, String type) {
        when_test();
        Date date = Date.valueOf(date1);
        Time time_start;
        Time time_end;
        if(time_end1!=null && time_start1!=null) {

            time_start = Time.valueOf(time_start1);
            time_end = Time.valueOf(time_end1);
        }
        else{
            time_start=null;
            time_end=null;
        }
        JSONObject object = new JSONObject();
        if (session.getAttribute("kin") == "doctor") {
            String id = (String) session.getAttribute("id");
            String activity_id = new String(String.valueOf(new java.util.Date().getTime()));
            jdbcTemplate.update("insert into activity_personal values(?,?,?,?,?,?,?,?)", id, activity_id, date, time_start, time_end, detail, type, false);
            object.put("state", "success");
            object.put("activityid", activity_id);
        } else {
            object.put("state", "Login");
            object.put("activityid", "null");
        }
        return object;
    }

    //删除

    @RequestMapping("doctor/delete")
    public JSONObject del(String activity_id, String id) {
        JSONObject object = new JSONObject();
        if (session.getAttribute("kin") == "doctor") {
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
        } else {
            object.put("state", "Login");
        }
        return object;
    }

    //查看队伍 done

    @RequestMapping("/doctor/team")
    public List<Team> team(String id) {
        List<Team> teams = new ArrayList<Team>();
        if (session.getAttribute("kin") == "doctor") {
            List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from team where leader_id=" + id);

            list.forEach((result) -> sh(result, teams));
        } else {
            Team team = new Team();
            teams.add(team);
        }
        return teams;
    }


    public void sh(Map<String, Object> map, List<Team> list) {
        Team team = new Team(map);
        list.add(team);
    }
    //登录 done

    @RequestMapping("doctor/login")
    public JSONObject test(String id, String passwd1,HttpServletRequest request) {

        JSONObject object = new JSONObject();
        try {
            String password = jdbcTemplate.queryForObject("select passwd from doctor where id = '" + id + "'", String.class);
            boolean sta = jdbcTemplate.queryForObject("select authority_team from doctor where id = '" + id + "'", boolean.class);
            if (passwd1.equals(password) == true) {
                session = request.getSession();
                session.setAttribute("kin", "doctor");
                session.setAttribute("statement", true);
                session.setAttribute("id", id);
                session.setAttribute("sta_team", sta);
                object.put("state", "success");
                object.put("authority",sta);
            } else {
                object.put("state", "passwrong");
            }
            return object;
        } catch (Exception e) {
            object.put("state", "idwrong");
            return object;
        }

    }

    // 返回所有团队列表
    @RequestMapping("/doctor/my_team")
    public List<Map<String,Object>> abc(){
        List<Map<String,Object>> list;
        if(session.getAttribute("id")!=null) {
            String id = (String) session.getAttribute("id");
            list = jdbcTemplate.queryForList("select * from team where leader_id=?",id);
            return list;
        }
        else{
            list=new ArrayList<>();
            return list;
        }
    }

    //已存在的团队加人 done

    @RequestMapping("/doctor/build")
    public JSONObject build(String id, int t_id) {
        JSONObject object = new JSONObject();
        if (session.getAttribute("kin") == "doctor") {
            if ((boolean) session.getAttribute("sta_team")) {

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
            } else {
                object.put("state", "noauthority");
            }
        } else {
            object.put("state", "Login");
        }
        return object;
    }

    //返回所有不在组中的人 done
    @RequestMapping("/doctor/people_notin_team")
    public List<Map<String, Object>> check(int t_id) {
        List<Map<String, Object>> list;
        if (session.getAttribute("kin") == "doctor") {
            list = jdbcTemplate.queryForList("select id,name from doctor where id not in (select id from take_in where team_id='" + t_id + "')");
            System.out.println(t_id);
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("state", "Login");
            list = new ArrayList<Map<String, Object>>();
            list.add(map);
        }
        return list;
    }

    //返回所有在组中的人
    @RequestMapping("/doctor/people_in_team")
    public List<Map<String, Object>> check_a(int t_id) {
        List<Map<String, Object>> list;
        if (session.getAttribute("kin") == "doctor") {
            list = jdbcTemplate.queryForList("select id,name from doctor where id in (select id from take_in where team_id='" + t_id + "')");
            System.out.println(t_id);
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("state", "Login");
            list = new ArrayList<Map<String, Object>>();
            list.add(map);
        }
        return list;
    }

    //删除团队中的人
    @RequestMapping("/doctor/delete_member")
    public JSONObject dele(String id, int t_id) {
        JSONObject object = new JSONObject();
        if (session.getAttribute("kin") == "doctor") {
            if ((boolean) session.getAttribute("sta_team")) {
                int a = jdbcTemplate.queryForObject("select count(*) from team where t_id=" + t_id, int.class);//没有这个队伍
                int b = jdbcTemplate.queryForObject("select count(*) from doctor where id=" + id, int.class);//没有这个人
                if (a == 0 || b == 0) {
                    object.put("state", "notEx");
                } else {
                    try {
                        jdbcTemplate.update("delete from take_in where id=? and team_id=?", id, t_id);
                        object.put("state", "success");
                    } catch (Exception e) {
                        //记录已经添加
                        object.put("state", "nobody");
                    }
                }
            } else {
                object.put("state", "noauthority");
            }
        } else {
            object.put("state", "Login");
        }
        return object;
    }

    //新建团队
    @RequestMapping("/doctor/build_team")
    public JSONObject build_team(String theme) {
        when_test();
        JSONObject jsonObject = new JSONObject();
        String type = "1";
        String id = (String) session.getAttribute("id");
        Date date = new Date(System.currentTimeMillis());
        if (session.getAttribute("kin") == "doctor" && (boolean) session.getAttribute("sta_team")) {
            jdbcTemplate.update("insert into team(date,type,leader_id,theme,statement)values (?,?,?,?,?)", date, type, id, theme, false);
            int t_id = jdbcTemplate.queryForObject("select last_insert_id()", int.class);
            jdbcTemplate.update("insert into take_in values(?,?)", id, t_id);
            jsonObject.put("state", "success");
            jsonObject.put("t_id", t_id);
        } else {
            jsonObject.put("state", "wrong");
            jsonObject.put("t_id", "null");
        }
        return jsonObject;
    }

    //新建团队事务
    @RequestMapping("/doctor/new_team_activity")
    public JSONObject newateam(String id, String date1, String time_start1, String time_end1, String detail) {
        Date date = Date.valueOf(date1);
        String type = "1";
        Time time_start = Time.valueOf(time_start1);
        Time time_end = Time.valueOf(time_end1);
        JSONObject object = new JSONObject();
        if (session.getAttribute("kin") == "doctor") {
            String activity_id = new String(String.valueOf(new java.util.Date().getTime()));
            jdbcTemplate.update("insert into activity_personal values(?,?,?,?,?,?,?,?)", id, activity_id, date, time_start, time_end, detail, type, false);
            object.put("state", "success");
            object.put("activityid", activity_id);
        } else {
            object.put("state", "Login");
            object.put("activityid", "null");
        }
        return object;
    }

    @RequestMapping("/doctor/new_t_activity")
    public JSONObject newactivityteam(int t_id, String date1, String time_start1, String time_end1, String detail) {
        when_test();
        Date date = Date.valueOf(date1);
        Time time_start = Time.valueOf(time_start1);
        Time time_end = Time.valueOf(time_end1);
        JSONObject object = new JSONObject();
        if (session.getAttribute("kin") == "doctor") {
            String activity_id = new String(String.valueOf(new java.util.Date().getTime()));
            List<Map<String, Object>> list = jdbcTemplate.queryForList("select id from  take_in where team_id=?", t_id);
            list.forEach((result) -> hk(result, activity_id, date, time_start, time_end, detail));
            object.put("state", "success");
            object.put("activityid", "null");
        } else {
            object.put("state", "Login");
            object.put("activityid", "null");
        }
        return object;
    }
    public void hk(Map<String,Object> map, String activity_id,Date date ,Time time_start,Time time_end,String detail){
        String id = (String)map.get("id");
        jdbcTemplate.update("insert into activity_personal values (?,?,?,?,?,?,?,?)",id,activity_id,date,time_start,time_end,detail,"1",false);
    }
    //修改团队 输入团队号要修改的主题
    @RequestMapping("/doctor/correct_team")
    public JSONObject correct_team(String t_id,String theme){
        JSONObject jsonObject = new JSONObject();
        String id = (String) session.getAttribute("id");
        if(session.getAttribute("kin")=="doctor" && (boolean)session.getAttribute("sta_team")){
            jdbcTemplate.update("update team set theme=? where t_id=? ",theme,t_id);
            jsonObject.put("state","success");
        }
        else{
            jsonObject.put("state","wrong");
        }
        return jsonObject;
    }



    //返回所有医生信息 done

    @RequestMapping("/doctor/doctor_info")
    public List<Doctor> select(String dep){
        List<Doctor> doctors = new ArrayList<Doctor>();
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from doctor where department=?",dep);
        list.forEach((result) -> kh(result, doctors));
        return doctors;
    }
    public void kh(Map<String,Object> map, List<Doctor> list){
        Doctor doctor = new Doctor(map);
        list.add(doctor);
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

