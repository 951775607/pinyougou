package cn.itcast.cas.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description TODO
 * @Date 2018/12/17 20:11
 * @Version 1.0
 **/

@RequestMapping("/user")
@RestController
public class UserController {
    @GetMapping("/getUsername")
    public String getUsername(HttpServletRequest request) {
        //从secutity中获取
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        //从请求中获取
        String userName2 = request.getRemoteUser();
        System.out.println(userName2);
        return userName;
    }
}
