package com.example.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatedOrderVO {

    private Long id;
    private String orderNo;
    private String pickupNo;
    private BigDecimal totalAmount;
}
