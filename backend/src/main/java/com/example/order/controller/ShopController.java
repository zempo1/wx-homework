package com.example.order.controller;

import com.example.order.common.ApiResponse;
import com.example.order.dto.ShopInfoVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @GetMapping("/info")
    public ApiResponse<ShopInfoVO> info() {
        ShopInfoVO info = new ShopInfoVO(
                "校园美食屋",
                "学生中心一楼 A 区",
                "周一至周日 09:00-21:00",
                "13800000000",
                "欢迎使用微信小程序点餐，请凭取餐号到前台取餐。"
        );
        return ApiResponse.success(info);
    }
}
