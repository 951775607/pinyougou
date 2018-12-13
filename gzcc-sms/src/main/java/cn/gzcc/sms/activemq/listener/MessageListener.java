package cn.gzcc.sms.activemq.listener;

import cn.gzcc.sms.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @Description TODO
 * @Date 2018/12/13 20:01
 * @Version 1.0
 **/
@Component
public class MessageListener {

    @Autowired
    private HttpClientUtil clientl;

    @JmsListener(destination = "itcast_sms_queue")
    public void receiveMsg(Map<String, String> map) {

//        HttpClientUtil client = HttpClientUtil.getInstance();
        HttpClientUtil client = clientl.getInstance();

        Set<Map.Entry<String, String>> entries = map.entrySet();
        String Uid = map.get("Uid");
        String Key = map.get("Key");
        String smsText = map.get("smsText");
        String smsMob = map.get("smsMbo");
        System.out.println(Uid);
        System.out.println(Key);
        System.out.println(smsText);
        System.out.printf(smsMob);


        int result = client.sendMsgUtf8(Uid, Key, smsText, smsMob);
        if(result>0){
            System.out.println("UTF8成功发送条数=="+result);
        }else{
            System.out.println(client.getErrorMsg(result));
        }
        System.out.println("接收到！");
    }
}
