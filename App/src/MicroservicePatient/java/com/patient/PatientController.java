package com.patient;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PatientController {
}
//患者
@RestController
class Action {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private Patient patient;
    @Autowired
    HttpSession session ;

    //注册账号
    @RequestMapping("patient/register")
    public JSONObject login(String id, String name, String pass, String sex, int age, String detail, String tel) {
        JSONObject object = new JSONObject();
        patient = new Patient(id, pass, name, sex, age, detail, tel);
        System.out.println(id);
        try {
            jdbcTemplate.update("INSERT INTO patient (id,name,sex,age,tel,detail,passwd) VALUES (?,?,?,?,?,?,?)", id, name, sex, age, tel, detail, pass);
            object.put("state", "success");
        } catch (Exception e) {
            object.put("state", "wrong");
        }
        return object;
    }

    //登录
    @RequestMapping("patient/login")
    public JSONObject test(String name,String passwd1) {

        JSONObject jsonObject = new JSONObject();
        try {
            String password = jdbcTemplate.queryForObject("select passwd from patient where id = '" + name + "'", String.class);
            System.out.println(password);
            System.out.println(passwd1);
            if (passwd1.equals(password) == true) {
                session.setAttribute("kin", "patient");

                jsonObject.put("state", "success");
            } else {
                jsonObject.put("state", "wrong_password");
            }
        } catch (Exception e) {
            jsonObject.put("state", "wrong_db");
        }
        return jsonObject;
    }

    @RequestMapping("/patient/doctor_info")
    public List<Doctor> select(String dep) {
        List<Doctor> doctors = new ArrayList<Doctor>();
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from doctor where department=?", dep);
        list.forEach((result) -> kh(result, doctors));
        return doctors;
    }

    public void kh(Map<String, Object> map, List<Doctor> list) {
        System.out.println(map.get("detail"));
        Doctor doctor = new Doctor(map);
        list.add(doctor);
    }

    //返回患者信息
    @RequestMapping("/patient/information")
    public Patient patient(String id) {
        Patient patient;
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from patient where id = " + id);
            Map<String, Object> map = list.get(0);
            String id1 = map.get("id").toString();
            String name = map.get("name").toString();
            String tel = map.get("tel").toString();
            int age = (int) map.get("age");
            String sex = map.get("sex").toString();
            String detail = map.get("detail").toString();
            String passwd = map.get("passwd").toString();
            patient = new Patient(id1, passwd, name, sex, age, detail, tel);
            return patient;
        }
        catch(Exception e){
            patient = new Patient("Login","Login","Login","Login",0,"Login","Login");
            return patient;
        }

    }

    //获取可以预约的时间
    @RequestMapping("/patient/get_doctor_time")
    public Aggregate timeUse(String doctor_id) {
        Aggregate timeUses = new Aggregate();
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from doc_pat where doctor_id=?", doctor_id);

        list.forEach((result) -> gh(result, timeUses));
        return timeUses;
    }

    public void gh(Map<String, Object> map, Aggregate timeUses) {
        Time_use timeUse = new Time_use(map);
        System.out.println(map.get("doctor_id"));
        timeUses.append(timeUse);
    }

    //预约
    @RequestMapping("/patient/getready")
    public JSONObject ab(String doctor_id, Date d, int number) {
        JSONObject object = new JSONObject();
        session.setAttribute("kin","patient");
        if (session.getAttribute("kin") == "patient") {
            try {
                int cap = jdbcTemplate.queryForObject("select capacity from doc_pat where doctor_id='" + doctor_id + "'and date='" + d + "'and item_id='" + number + "'", int.class);
                if (cap >= 0) {
                    jdbcTemplate.update("update doc_pat set capacity=? where doctor_id=? and date=? and item_id=?", cap - 1, doctor_id, d, number);
                    object.put("state", "success");
                } else {
                    object.put("state", "full");
                }
            } catch (Exception e) {
                object.put("state", "wrong_db");
            }
        } else {
            object.put("state", "Login");
        }
        return object;
    }

    //添加
    @RequestMapping("/patient/add_doc")
    public boolean addc(String doctor_id,String date1,int number){
        System.out.println(doctor_id);
        System.out.println(date1);
        Date date= Date.valueOf(date1);
        try {
            jdbcTemplate.update("insert into doc_pat values(?,?,?,?)", doctor_id, number, date, 20);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }
}
//    @GetMapping("/user/name")
//    public Long get() {
//        return jdbcTemplate.queryForObject("select count(*) from patient", Long.class);
//    }
//    //实验
//    @GetMapping("user/hello")
//    public Doctor hello(HttpSession session) {
//        if (session.getAttribute("kin") == "patient") {
//            Doctor doctor = new Doctor();
//            doctor.setAge(59);
//            doctor.setDepartment("骨科");
//            doctor.setDetails("经验丰富，妙手回春");
//            doctor.setId(9);
//            doctor.setTel("17317812xxx");
//            doctor.setName("Alex");
//            return doctor;
//        } else {
//            Doctor doctor = new Doctor("请登录");
//            return doctor;
//        }
//    }
//}
//预约功能
//时间表以半小时为粒度

//@Configuration
//@RestController
//class Try {
//
//    @Bean
//    public RestTemplate getRestTemplate(){
//        return new RestTemplate();
//    }
//    @Autowired
//    private RestTemplate restTemplate;
//    @GetMapping("/try")
//    public String test() {
//        return restTemplate.getForObject("https://api.ipify.org/?format=jsonp&callback=?",String.class);
//    }
//}
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
