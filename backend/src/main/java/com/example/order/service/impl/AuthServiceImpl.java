package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.order.dto.LoginRequest;
import com.example.order.dto.LoginResponse;
import com.example.order.dto.UserVO;
import com.example.order.entity.User;
import com.example.order.mapper.UserMapper;
import com.example.order.service.AuthService;
import com.example.order.service.WxAuthService;
import com.example.order.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final WxAuthService wxAuthService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        String openid = wxAuthService.getOpenid(request.getCode(), request.getMockClientId());
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, openid)
                .last("LIMIT 1"));

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : "微信用户");
            user.setAvatarUrl(request.getAvatarUrl());
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.insert(user);
        } else {
            boolean needUpdate = false;
            if (StringUtils.hasText(request.getNickname())) {
                user.setNickname(request.getNickname());
                needUpdate = true;
            }
            if (StringUtils.hasText(request.getAvatarUrl())) {
                user.setAvatarUrl(request.getAvatarUrl());
                needUpdate = true;
            }
            if (needUpdate) {
                user.setUpdateTime(LocalDateTime.now());
                userMapper.updateById(user);
            }
        }

        String token = jwtUtil.generateToken(user.getId());
        return new LoginResponse(token, UserVO.from(user));
    }
}
