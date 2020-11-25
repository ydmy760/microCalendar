package com.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;


public class PatientController {
}



//定义接口
@RestController
class Action {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("user/login")
    public String login(String id, String name, String pass, String sex, String age, String detail, String tel) {
        System.out.println(id);
        jdbcTemplate.update("INSERT INTO patient (id,name,sex,age,tel,detail,passwd) VALUES (?,?,?,?,?,?,?)", id, name, sex, age, tel, detail, pass);
        return "success";
    }
    @RequestMapping("user/test")
    public ModelAndView test(HttpServletRequest request) {
        RedirectView view = new RedirectView("/hello.html");
        String name = request.getParameter("abc");
        System.out.println(name);
        return new ModelAndView(view);
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
    @Bean
    @GetMapping("user/hello")

    public Doctor hello() {
        Doctor doctor = new Doctor();
        doctor.setAge(59);
        doctor.setDepartment("骨科");
        doctor.setDetails("经验丰富，妙手回春");
        doctor.setId(9);
        doctor.setTel("17317812xxx");
        doctor.setName("Alex");
        return doctor;
    }

}