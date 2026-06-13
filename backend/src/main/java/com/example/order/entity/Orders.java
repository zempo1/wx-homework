package com.example.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders {

    private Long id;
    private Long userId;
    private String orderNo;
    private String pickupNo;
    private BigDecimal totalAmount;
    private String remark;
    private Integer status;
    private Integer payStatus;
    private LocalDateTime payTime;
    private LocalDateTime pickupTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
