package com.tan.user.service;

import com.tan.pojo.TbUser;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;

public interface UserService extends BaseService<TbUser> {

    PageResult search(Integer page, Integer rows, TbUser user);

    void sendSmsCode(String phone);

    boolean checkSmsCode(String phone, String smsCode);
}