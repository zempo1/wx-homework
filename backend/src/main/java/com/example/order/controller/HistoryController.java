package com.example.order.controller;

import com.example.order.common.ApiResponse;
import com.example.order.context.UserContext;
import com.example.order.dto.HistoryRecordVO;
import com.example.order.dto.PageResult;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<PageResult<HistoryRecordVO>> list(@RequestParam(defaultValue = "1") Long page,
                                                         @RequestParam(defaultValue = "10") Long pageSize) {
        return ApiResponse.success(orderService.pageHistory(UserContext.getUserId(), page, pageSize));
    }

    @DeleteMapping("/{orderId}")
    public ApiResponse<Void> delete(@PathVariable Long orderId) {
        orderService.deleteHistoryRecord(UserContext.getUserId(), orderId);
        return ApiResponse.success(null);
    }
}
