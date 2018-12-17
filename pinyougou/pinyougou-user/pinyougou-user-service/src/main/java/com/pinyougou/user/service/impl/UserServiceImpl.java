package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import tk.mybatis.mapper.entity.Example;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = UserService.class)
public class UserServiceImpl extends BaseServiceImpl<TbUser> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQQueue itcastSmsQueue;

    @Value("${signName}")
    private String signName;
    @Value("${templateCode}")
    private String templateCode;


    @Override

    public PageResult search(Integer page, Integer rows, TbUser user) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(user.get***())){
            criteria.andLike("***", "%" + user.get***() + "%");
        }*/

        List<TbUser> list = userMapper.selectByExample(example);
        PageInfo<TbUser> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }


    /**
     * 功能描述:发送短信验证码
     *
     * @param: phone手机号
     * @return:
     * @date: 2018/12/14 10:34
     **/
    //方法一，使用阿里大于
    @Override
//    public void sendSmsCode(String phone) {
//        //1、生成6位数值类型验证码
//        //TODO 如果是小于6位则修正为6位
//        String smsCode = (long)(Math.random()*1000000) + "";
//
//        System.out.println("------------验证码为：" + smsCode);
//
//        //2、保存到redis并设置过期时间5分钟
//        redisTemplate.boundValueOps(phone).set(smsCode);
//        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);
//
//        //3、发送MQ消息
//        jmsTemplate.send(itcastSmsQueue, new MessageCreator() {
//            @Override
//            public Message createMessage(Session session) throws JMSException {
//                MapMessage mapMessage = session.createMapMessage();
//                mapMessage.setString("mobile", phone);
//                mapMessage.setString("signName", signName);
//                mapMessage.setString("templateCode", templateCode);
//                mapMessage.setString("templateParam", "{\"code\":" + smsCode + "}");
//                return mapMessage;
//            }
//        });
//    }


    //方法二：使用中国网建短信服务系统
    public void sendSmsCode(String phone) {
        //1、生成6位数值类型验证码
        //TODO 如果是小于6位则修正为6位
        String smsCode = (long)(Math.random()*1000000) + "";

        System.out.println("------------验证码为：" + smsCode);

        //2、保存到redis并设置过期时间5分钟
        redisTemplate.boundValueOps(phone).set(smsCode);
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);

        //3、发送MQ消息
        jmsTemplate.send(itcastSmsQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("Uid", "l951775607");
                mapMessage.setString("Key", "d41d8cd98f00b204e980");
                mapMessage.setString("smsMbo", "18320375625");
                mapMessage.setString("smsText", "注册验证码:" + smsCode);
                return mapMessage;
            }
        });
    }





    /**
     * 功能描述:校验验证码是否存在
     *
     * @param: phone手机号
     * @return: smsCode验证码
     * @date: 2018/12/14 14:23
     **/
    @Override
    public boolean checkSmsCode(String phone, String smsCode) {
        //获取正确的验证码
        String code = (String) redisTemplate.boundValueOps(phone).get();
        if (smsCode.equals(code)) {
            //删除redis中验证码
            redisTemplate.delete(phone);

            return true;
        }

        return false;
    }
}
