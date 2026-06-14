package com.example.order.service;

public interface WxAuthService {

    String getOpenid(String code, String mockClientId);
}
