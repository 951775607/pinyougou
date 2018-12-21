package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pinyougou.pay.service.WeixinPayService;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Date 2018/12/21 19:27
 * @Version 1.0
 **/

@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private OrderService orderService;

    @Reference
    private WeixinPayService weixinPayService;

    /**
     * 根据支付日志 id 到微信支付创建支付订单并返回支付二维码地址等信息
     *
     * @param outTradeNo 支付日志 id
     * @return 支付二维码地址等信息
     */
    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo) {
        //查找支付日志信息
        TbPayLog payLog = orderService.findPayLogByOutTradeNo(outTradeNo);
        if (payLog != null) {
            //到支付系统进行提交订单并返回支付地址
            return weixinPayService.createNative(outTradeNo, payLog.getTotalFee().toString());
        }
        return new HashMap<>();
    }
}
