package com.pinyougou.search.activemq.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.Arrays;

/**
 * @Description 监听删除商品，同步实现删除索引库数据
 * @Date 2018/12/11 16:49
 * @Version 1.0
 **/
public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //1、接收消息、转换为数组
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();

        //2、更新到solr中,需要把数据转换成列表
        itemSearchService.deleteItemByGoodsIdList(Arrays.asList(goodsIds));
        System.out.println("同步删除索引库中数据完成。");


    }
}
