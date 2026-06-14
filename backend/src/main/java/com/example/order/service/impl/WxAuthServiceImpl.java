package com.example.order.service.impl;

import com.example.order.common.BizException;
import com.example.order.service.WxAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WxAuthServiceImpl implements WxAuthService {

    private final RestTemplate restTemplate;

    @Value("${app.wx.appid:}")
    private String appid;

    @Value("${app.wx.secret:}")
    private String secret;

    @Override
    public String getOpenid(String code, String mockClientId) {
        if (!StringUtils.hasText(code)) {
            throw BizException.badRequest("code不能为空");
        }
        if (!StringUtils.hasText(appid) || !StringUtils.hasText(secret)) {
            if (!StringUtils.hasText(mockClientId)) {
                throw BizException.badRequest("mockClientId不能为空");
            }
            return "mock_openid_" + mockClientId;
        }

        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + appid
                + "&secret=" + secret
                + "&js_code=" + code
                + "&grant_type=authorization_code";
        Map<?, ?> result = restTemplate.getForObject(url, Map.class);
        if (result == null || result.get("openid") == null) {
            throw BizException.badRequest("微信登录失败");
        }
        return String.valueOf(result.get("openid"));
    }
}
