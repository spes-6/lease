package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.web.app.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {


    @Autowired
    private SmsServiceImpl smsService;
    @Override
    public void getCode(String phone) {
//        smsService.sendCode(phone,);
    }
}
