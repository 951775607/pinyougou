package com.pinyougou.shop.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Date:2018/11/27
 * Author:Leon
 * Desc
 */

@RestController
@RequestMapping("/login")
public class LoginController {
    /**
     * 从 security 认证信息中获取当前登录人信息
     * @return 当前登录人
     */
    @GetMapping("/getUsername")
    public Map<String,String> getUsername() {
        Map<String, String> map = new HashMap<>();

        /**
         * 获取用户名
         * */
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username", username);
        return map;
    }
}
