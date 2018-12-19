package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 购物车
 * @Date 2018/12/18 16:07
 * @Version 1.0
 **/

@RestController
@RequestMapping("/cart")
public class CartController {

    //Cookie 中购物车列表的名称
    private static final String COOKIE_CART_LIST = "PYG_CART_LIST";

    //Cookie 中购物车列表的最大生存实践，1天
    private static final int COOKIE_CART_LIST_MAX_AGE = 3600 * 24;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;


    /**
     * 功能描述:实现登录，未登录下将商品加入购物车列表
     *
     * @param: itemId商品id
     * @return: num购买数量
     **/
    @GetMapping("/addItemToCartList")
//    @CrossOrigin(origins = "http://item.pinyougou.com", allowCredentials = "true")
    public Result addItemToCartList(Long itemId, Integer num) {

        //设置允许哪些域名的服务器可以获得资源，允许跨域请求响应
        //response.setHeader("Access-Control-Allow-Origin", "http://item.pinyougou.com");
        //是否允许接收或设置cookie
        //response.setHeader("Access-Control-Allow-Credentials", "true");

        try {
            //获取用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取购物车列表
            List<Cart> cartList = findCartList();
            //将商品加入到购物车列表
            List<Cart> newCartList = cartService.addItemToCartList(cartList, itemId, num);

            //因为配置了可以匿名访问所以如果是匿名访问的时候，返回的用户名为anonymousUser
            //如果未登录则用户名为：anonymousUser
            if ("anonymousUser".equals(userName)) {
                //未登录，将商品加入写到cookie中

                //把商品列表转换为json字符串
                String cartListJsonStr = JSON.toJSONString(newCartList);
                CookieUtils.setCookie(request, response, COOKIE_CART_LIST, cartListJsonStr, COOKIE_CART_LIST_MAX_AGE, true);
            } else {
                //已经登录,将商品加入写到redis中
                cartService.saveCartListByUsername(newCartList, userName);
            }
            return Result.ok("加入购物车成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("加入购物车失败！");
    }


    /**
     * 功能描述:获取购物车列表数据，如果登录了则从redis中获取，如果未登录则从cookie中获取
     *
     * @param:
     * @return: 购物车列表
     **/
    @GetMapping("/findCartList")
    public List<Cart> findCartList() {
        //获取用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //获取cookie中的购物车列表
        String cartListJsonStr = CookieUtils.getCookieValue(request, COOKIE_CART_LIST, true);
        List<Cart> cookie_carList;
        if (!StringUtils.isEmpty(cartListJsonStr)) {
            cookie_carList = JSONArray.parseArray(cartListJsonStr, Cart.class);
        }else {
            cookie_carList = new ArrayList<>();
        }

        if ("anonymousUser".equals(userName)) {
            //未登录；从cookie中获取购物车数据
            return cookie_carList;
        } else {
            //已登录，从redis中获取购物车列表
            List<Cart> redis_cartList = cartService.findCartListByUsername(userName);
            //合并购物车
            if (cookie_carList.size() > 0) {
                redis_cartList = cartService.margeCartList(cookie_carList, redis_cartList);

                //保存最新的购物车列表到redis中
                cartService.saveCartListByUsername(redis_cartList, userName);

                //删除cookie中购物车列表
                CookieUtils.deleteCookie(request, response, COOKIE_CART_LIST);
            }
            return redis_cartList;
        }
    }


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
