package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface UserService extends BaseService<TbUser> {

    PageResult search(Integer page, Integer rows, TbUser user);


    /**
     * 功能描述:发送短信验证码
     *
     * @param: phone手机号
     * @return:
     * @date: 2018/12/14 10:34
     **/
    void sendSmsCode(String phone);

    /**
     * 功能描述:校验验证码是否存在
     *
     * @param: phone手机号
     * @return: smsCode验证码
     * @date: 2018/12/14 14:23
     **/
    boolean checkSmsCode(String phone,String smsCode);
}