package cn.gzcc.springboot.activemq.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description TODO
 * @Date 2018/12/13 19:04
 * @Version 1.0
 **/

@Component
public class MessageListener {
    @JmsListener(destination = "spring.boot.map.queue")
    public void receiveMsg(Map<String,Object> map) {
        System.out.println(map);

    }
}
