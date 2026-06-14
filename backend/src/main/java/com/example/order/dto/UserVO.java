package com.example.order.dto;

import com.example.order.entity.User;
import lombok.Data;

@Data
public class UserVO {

    private Long id;
    private String nickname;
    private String avatarUrl;

    public static UserVO from(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        return vo;
    }
}
