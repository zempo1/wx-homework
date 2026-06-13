package com.example.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {

    private Long id;
    private String orderNo;
    private String pickupNo;
    private BigDecimal totalAmount;
    private String remark;
    private Integer status;
    private Integer payStatus;
    private LocalDateTime payTime;
    private LocalDateTime pickupTime;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;
}
