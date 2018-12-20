package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Transactional
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    //redis中购物车数据的key
    private static final String REDIS_CART_LIST = "CART_LIST";

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


    /**
     * 功能描述:将购物车列表中的商品保存成订单基本、明细信息和支付日志信息
     *
     * @param:order 订单基本信息
     * @return:支付业务id
     **/
    @Override
    public String addOrder(TbOrder order) {
        String outTradeNo = "";
        //1. 获取redis中的购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(order.getUserId());

        if (cartList != null && cartList.size() > 0) {
            //2. 遍历购物车列表，每个购物车对象Cart对应一个订单
            // 本次交易的总金额 = 所有订单的总金额
            double totalPayment = 0.0;
            //订单的id集合
            String orderIds = "";
            for (Cart cart : cartList) {
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(idWorker.nextId());

                tbOrder.setSourceType(order.getSourceType());
                tbOrder.setPaymentType(order.getPaymentType());
                tbOrder.setUserId(order.getUserId());
                //未支付0
                tbOrder.setStatus("0");
                tbOrder.setSellerId(cart.getSellerId());
                tbOrder.setCreateTime(new Date());
                tbOrder.setUpdateTime(tbOrder.getCreateTime());
                tbOrder.setReceiver(order.getReceiver());
                tbOrder.setReceiverMobile(order.getReceiverMobile());
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());

                //本笔订单支付总金额 = 所有订单明细的总金额之和
                double payment = 0.0;

                //3. 遍历购物车对象Cart中订单明细列表一个个的保存到订单明细表tb_order_item
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(tbOrder.getOrderId());

                    //累计本笔订单的总金额
                    payment += orderItem.getTotalFee().doubleValue();

                    //保存订单明细
                    orderItemMapper.insertSelective(orderItem);
                }

                //本笔订单支付总金额
                tbOrder.setPayment(new BigDecimal(payment));

                //累计本次交易的总金额
                totalPayment += payment;

                if (orderIds.length() > 0) {
                    orderIds += "," + tbOrder.getOrderId();
                } else {
                    orderIds = tbOrder.getOrderId().toString();
                }

                orderMapper.insertSelective(tbOrder);
            }

            //4. 如果为微信支付的话生成支付日志信息保存到tb_pay_log
            if ("1".equals(order.getPaymentType())) {
                TbPayLog payLog = new TbPayLog();
                outTradeNo = idWorker.nextId() + "";
                payLog.setOutTradeNo(outTradeNo);
                //1 未支付
                payLog.setTradeState("1");
                payLog.setPayType(order.getPaymentType());
                payLog.setUserId(order.getUserId());
                payLog.setCreateTime(new Date());
                //本次交易的总金额 = 所有订单的总金额；一般的电商对应价格都是使用整型的，不能使用小数点，因为小数点会出现精度不匹配的问题；
                //也就是金额的单位精确到分
                payLog.setTotalFee((long) (totalPayment * 100));

                //本次交易对应的所有订单id，使用,隔开
                payLog.setOrderList(orderIds);

                payLogMapper.insertSelective(payLog);
            }
            //5. 将redis中该用户对应的购物车数据删除
            redisTemplate.boundHashOps(REDIS_CART_LIST).delete(order.getUserId());
        }
        //6. 返回支付日志id
        return outTradeNo;
    }
}
