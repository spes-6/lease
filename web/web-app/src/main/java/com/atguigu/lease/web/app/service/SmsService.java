package com.atguigu.lease.web.app.service;

public interface SmsService {
    void getCode(String phone);
    void sendCode(String phone, String code);
}
