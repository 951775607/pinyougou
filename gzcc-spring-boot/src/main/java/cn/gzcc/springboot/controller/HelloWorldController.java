package cn.gzcc.springboot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Date 2018/12/13 17:32
 * @Version 1.0
 **/

@RestController
public class HelloWorldController {

    @Autowired
    private Environment environment;

    @GetMapping("/info")
    public String info() {
        return "Hello World!"+environment.getProperty("url");
    }
}
