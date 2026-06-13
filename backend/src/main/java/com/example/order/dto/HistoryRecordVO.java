package com.example.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HistoryRecordVO {

    private Long orderId;
    private LocalDateTime consumeTime;
    private BigDecimal totalAmount;
    private String summary;
}
