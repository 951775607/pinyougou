package com.pinyougou.item.activemq.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/**
 * 整合删除商品的主题，接收该主题的消息并根据消息删除指定路径下的静态html页面。
 */
public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {
    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;
    @Override
    public void onMessage(Message message, Session session) throws
            JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();
        for (Long goodsId : goodsIds) {
            String filename = ITEM_HTML_PATH + goodsId + ".html";
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
