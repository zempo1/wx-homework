package com.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShopInfoVO {

    private String name;
    private String address;
    private String businessHours;
    private String phone;
    private String notice;
}
