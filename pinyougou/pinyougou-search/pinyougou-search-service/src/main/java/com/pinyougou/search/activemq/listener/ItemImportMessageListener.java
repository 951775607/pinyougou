package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 * @Description 监听器，manager修改信息发送到mq后，在这可以监听到mq的信息
 * @Date 2018/12/11 15:46
 * @Version 1.0
 **/
public class ItemImportMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //1、接收消息、转换为itemList列表
        TextMessage textMessage = (TextMessage) message;

        List<TbItem> itemList = JSONArray.parseArray(textMessage.getText(), TbItem.class);

        for (TbItem item : itemList) {
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);
        }


        //2、更新到solr中
        itemSearchService.importItemList(itemList);
        System.out.println("同步索引库数据完成。 ");
    }
}
