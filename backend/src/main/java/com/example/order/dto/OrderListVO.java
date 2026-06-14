package com.example.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderListVO {

    private Long id;
    private String orderNo;
    private String pickupNo;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime createTime;
    private String summary;
}
