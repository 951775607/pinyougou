package com.pinyougou.cart.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 购物车
 * @Date 2018/12/18 16:07
 * @Version 1.0
 **/

@RestController
@RequestMapping("/cart")
public class CartController {
    /**
     * 功能描述:获取当前登录用户信息
     *
     * @param:
     * @return: 用户信息
     * @date: 2018/12/18 16:08
     **/

    @GetMapping("/getUsername")
    public Map<String, Object> getUsername() {
        Map<String, Object> map = new HashMap<String, Object>();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果未登录，那么获取到的username为：anonymousUser
        map.put("username", userName);
        return map;
    }
}
