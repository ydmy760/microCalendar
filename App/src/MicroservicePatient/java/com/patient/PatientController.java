package com.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    //注册账号
    @RequestMapping("user/login")
    public String login(String id, String name, String pass, String sex, int age, String detail, String tel) {
        patient = new Patient( id, pass, name, sex, age, detail, tel);
        System.out.println(id);
        try {
            jdbcTemplate.update("INSERT INTO patient (id,name,sex,age,tel,detail,passwd) VALUES (?,?,?,?,?,?,?)", id, name, sex, age, tel, detail, pass);
            return "success";
        }
        catch (Exception e){
            return "帐号已被注册";
        }
    }
    //登录
    @RequestMapping("user/test")
    public ModelAndView test(HttpServletRequest request) {

        RedirectView view = new RedirectView("/hello.html");
        RedirectView view2 = new RedirectView("/login.html");
        String name = request.getParameter("abc");
        ModelAndView mav = new ModelAndView();
        mav.setView(view2);

        HttpSession httpSession = request.getSession();
        String passwd1 = request.getParameter("passwd");
        try {
            String password = jdbcTemplate.queryForObject("select passwd from patient where id = '" + name + "'", String.class);
            System.out.println(password);
            System.out.println(passwd1);
            if (passwd1.equals(password) == true) {
                httpSession.setAttribute("kin","patient");
                return new ModelAndView(view);
            } else {
                mav.addObject("alert","密码错误");
                return mav;
            }
        }
        catch(Exception e){
            mav.addObject("alert","用户不存在");
            return mav;
        }

    }
}

@RestController
class next {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/user/name")
    public Long get() {
        return jdbcTemplate.queryForObject("select count(*) from patient", Long.class);
    }
}
@RestController
class Uscona {
    @GetMapping("user/hello")

    public Doctor hello(HttpSession session) {
        if(session.getAttribute("kin") == "patient") {
            Doctor doctor = new Doctor();
            doctor.setAge(59);
            doctor.setDepartment("骨科");
            doctor.setDetails("经验丰富，妙手回春");
            doctor.setId(9);
            doctor.setTel("17317812xxx");
            doctor.setName("Alex");
            return doctor;
        }
        else {
            Doctor doctor=new Doctor("请登录");
            return doctor;
        }
    }

}

@RestController
class patient_check{
    @Autowired
    JdbcTemplate jdbcTemplate = new JdbcTemplate();


    @RequestMapping("user/patient")
    public Patient patient(String id){

        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from patient where id = "+id);
        Map<String, Object> map = list.get(0);
        String id1 = map.get("id").toString();
        String name = map.get("name").toString();
        String tel = map.get("tel").toString();
        int age = (int)map.get("age");
        String sex = map.get("sex").toString();
        String detail = map.get("detail").toString();
        String passwd = map.get("passwd").toString();
        Patient patient = new Patient(id1, passwd, name, sex, age, detail, tel);
        return patient;
    }
}
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