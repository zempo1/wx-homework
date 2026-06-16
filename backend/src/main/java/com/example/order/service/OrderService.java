package com.example.order.service;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.CreatedOrderVO;
import com.example.order.dto.HistoryRecordVO;
import com.example.order.dto.OrderDetailVO;
import com.example.order.dto.OrderListVO;
import com.example.order.dto.PageResult;

public interface OrderService {

    CreatedOrderVO createOrder(Long userId, CreateOrderRequest request);

    OrderDetailVO getOrderDetail(Long userId, Long orderId);

    PageResult<OrderListVO> pageOrders(Long userId, Long page, Long pageSize);

    PageResult<HistoryRecordVO> pageHistory(Long userId, Long page, Long pageSize);

    void pickupOrder(Long userId, Long orderId);

    void cancelOrder(Long userId, Long orderId);
}
