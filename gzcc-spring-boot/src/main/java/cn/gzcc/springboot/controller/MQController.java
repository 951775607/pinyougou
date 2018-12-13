package cn.gzcc.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Date 2018/12/13 18:55
 * @Version 1.0
 **/

@RequestMapping("/mq")
@RestController
public class MQController {


    //用户名
    private String Uid = "l951775607";

    //接口安全秘钥
    private String Key = "d41d8cd98f00b204e980";

    //手机号码，多个号码如13800000000,13800000001,13800000002
    private String smsMob = "18320375625";

    //短信内容
    private String smsText = "注册验证码：8888";


    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    /**
     * 发送一个 map 消息到 MQ 的队列
     * @return
     */
    @GetMapping("/send")
    public String sendMapMsg() {
        Map<String, Object> map = new HashMap<>();

        map.put("id", 123L);
        map.put("name", "传智播客");
        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue", map);
        return "发送信息完成";
    }

    /**
     * 发送一个手机短信消息到 MQ 的队列
     * @return
     */
    @GetMapping("/sendSms")
    public String sendSmsMsg(){
        Map<String, String> map = new HashMap<>();
//        map.put("Uid", "l951775607");
//        map.put("Key", "d41d8cd98f00b204e980");
//        map.put("smsText", "注册验证码：8811");
//        map.put("smsMbo", "18320375625");
        map.put("Uid", Uid);
        map.put("Key", Key);
        map.put("smsText", smsText);
        map.put("smsMbo", smsMob);
        jmsMessagingTemplate.convertAndSend("itcast_sms_queue", map);


        return "发送 sms 消息完成。 ";
    }
}
