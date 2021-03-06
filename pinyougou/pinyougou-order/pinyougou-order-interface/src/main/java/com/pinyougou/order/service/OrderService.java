package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    /**
     * 功能描述:将购物车列表中的商品保存成订单基本、明细信息和支付日志信息
     *
     * @param:order 订单基本信息
     * @return:支付业务id
     **/
    String addOrder(TbOrder order);

    /**
     * 功能描述:将购物车列表中的商品保存成订单基本，明细信息和支付日志信息
     *
     * @param: order订单基本信息
     * @return: 支付业务id
     **/
    TbPayLog findPayLogByOutTradeNo(String outTradeNo);


    /**
     * 功能描述:根据支付日志id更新订单支付状态和支付日志支付状态为已支付
     *
     * @param: outTradeNo 支付日志id
     * @return: transactionId 微信中对应的支付id
     **/
    void updateOrderStatus(String outTradeNo, String transactionId);
}