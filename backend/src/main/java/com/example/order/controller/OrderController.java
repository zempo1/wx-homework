package com.example.order.controller;

import com.example.order.common.ApiResponse;
import com.example.order.context.UserContext;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.CreatedOrderVO;
import com.example.order.dto.OrderDetailVO;
import com.example.order.dto.OrderListVO;
import com.example.order.dto.PageResult;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<CreatedOrderVO> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(orderService.createOrder(UserContext.getUserId(), request));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderDetail(UserContext.getUserId(), id));
    }

    @GetMapping
    public ApiResponse<PageResult<OrderListVO>> list(@RequestParam(defaultValue = "1") Long page,
                                                     @RequestParam(defaultValue = "10") Long pageSize) {
        return ApiResponse.success(orderService.pageOrders(UserContext.getUserId(), page, pageSize));
    }
}
