package com.example.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "code不能为空")
    private String code;

    private String mockClientId;
    private String nickname;
    private String avatarUrl;
}
