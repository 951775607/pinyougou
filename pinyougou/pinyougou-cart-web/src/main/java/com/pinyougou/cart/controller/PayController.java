package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
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


    /**
     * 功能描述:根据支付id查询订单支付状态
     *
     * @param: outTradeNo 支付日志
     * @return: 支付结果
     **/
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo) {
        Result result = Result.fail("支付失败");

        try {
            int count = 0;
            while (true) {
                //到微信支付查询支付状态
                Map<String, String> resultMap = weixinPayService.queryPayStatus(outTradeNo);

                if (resultMap == null) {
                    break;
                }
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    result = Result.ok("支付成功");
                    //需要更新订单，支付日志支付状态
                    orderService.updateOrderStatus(outTradeNo, resultMap.get("transaction_id"));
                    break;
                }
                //每3秒查询一次
                Thread.sleep(3000);

                //在3分钟里每3秒查询一次，如果过了3分钟返回，二维码超时页面中自动重新生成二维码
                count++;
                if (count > 60) {
                    result = Result.fail("二维码超时");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
