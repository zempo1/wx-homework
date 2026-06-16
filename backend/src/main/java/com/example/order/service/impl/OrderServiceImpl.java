package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.order.common.BizException;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.CreatedOrderVO;
import com.example.order.dto.HistoryRecordVO;
import com.example.order.dto.OrderDetailVO;
import com.example.order.dto.OrderItemVO;
import com.example.order.dto.OrderListVO;
import com.example.order.dto.PageResult;
import com.example.order.entity.OrderItem;
import com.example.order.entity.Orders;
import com.example.order.entity.Product;
import com.example.order.mapper.OrderItemMapper;
import com.example.order.mapper.OrdersMapper;
import com.example.order.mapper.ProductMapper;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreatedOrderVO createOrder(Long userId, CreateOrderRequest request) {
        Map<Long, Integer> productQuantityMap = mergeItems(request.getItems());
        List<Product> products = productMapper.selectBatchIds(productQuantityMap.keySet());
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : productQuantityMap.entrySet()) {
            Product product = productMap.get(entry.getKey());
            if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                throw BizException.badRequest("商品不存在或已下架");
            }
            if (product.getStock() != null && product.getStock() < entry.getValue()) {
                throw BizException.badRequest(product.getName() + "库存不足");
            }

            BigDecimal quantity = BigDecimal.valueOf(entry.getValue());
            BigDecimal subtotal = product.getPrice().multiply(quantity);
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getImageUrl());
            item.setProductPrice(product.getPrice());
            item.setQuantity(entry.getValue());
            item.setSubtotal(subtotal);
            item.setCreateTime(LocalDateTime.now());
            orderItems.add(item);
        }

        Orders order = new Orders();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setPickupNo(generatePickupNo());
        order.setTotalAmount(totalAmount);
        order.setRemark(request.getRemark());
        order.setStatus(0);
        order.setPayStatus(1);
        order.setPayTime(LocalDateTime.now());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.insert(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        CreatedOrderVO vo = new CreatedOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setPickupNo(order.getPickupNo());
        vo.setTotalAmount(order.getTotalAmount());
        return vo;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        Orders order = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .last("LIMIT 1"));
        if (order == null) {
            throw BizException.notFound("订单不存在");
        }
        List<OrderItemVO> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
                        .orderByAsc(OrderItem::getId))
                .stream()
                .map(OrderItemVO::from)
                .toList();

        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setPickupNo(order.getPickupNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setRemark(order.getRemark());
        vo.setStatus(order.getStatus());
        vo.setPayStatus(order.getPayStatus());
        vo.setPayTime(order.getPayTime());
        vo.setPickupTime(order.getPickupTime());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(items);
        return vo;
    }

    @Override
    public PageResult<OrderListVO> pageOrders(Long userId, Long page, Long pageSize) {
        IPage<Orders> result = ordersMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getUserId, userId)
                        .orderByDesc(Orders::getCreateTime));
        List<OrderListVO> records = result.getRecords().stream().map(order -> {
            OrderListVO vo = new OrderListVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setPickupNo(order.getPickupNo());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setStatus(order.getStatus());
            vo.setCreateTime(order.getCreateTime());
            vo.setSummary(summaryFor(order.getId()));
            return vo;
        }).toList();
        return new PageResult<>(result.getTotal(), page, pageSize, records);
    }

    @Override
    public PageResult<HistoryRecordVO> pageHistory(Long userId, Long page, Long pageSize) {
        IPage<Orders> result = ordersMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getUserId, userId)
                        .eq(Orders::getPayStatus, 1)
                        .orderByDesc(Orders::getPayTime)
                        .orderByDesc(Orders::getCreateTime));
        List<HistoryRecordVO> records = result.getRecords().stream().map(order -> {
            HistoryRecordVO vo = new HistoryRecordVO();
            vo.setOrderId(order.getId());
            vo.setConsumeTime(order.getPayTime() == null ? order.getCreateTime() : order.getPayTime());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setSummary(summaryFor(order.getId()));
            return vo;
        }).toList();
        return new PageResult<>(result.getTotal(), page, pageSize, records);
    }

    private Map<Long, Integer> mergeItems(List<CreateOrderRequest.Item> items) {
        Map<Long, Integer> result = new LinkedHashMap<>();
        for (CreateOrderRequest.Item item : items) {
            result.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
        return result;
    }

    private String summaryFor(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
                .orderByAsc(OrderItem::getId));
        if (items.isEmpty()) {
            return "暂无商品";
        }
        int totalQuantity = items.stream().mapToInt(OrderItem::getQuantity).sum();
        return items.get(0).getProductName() + "等" + totalQuantity + "件商品";
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD" + time + random;
    }

    private String generatePickupNo() {
        return String.format("A%03d", ThreadLocalRandom.current().nextInt(1, 1000));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pickupOrder(Long userId, Long orderId) {
        Orders order = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .last("LIMIT 1"));
        if (order == null) {
            throw BizException.notFound("订单不存在");
        }
        if (order.getStatus() != null && order.getStatus() != 0) {
            throw BizException.badRequest("该订单无法取餐");
        }
        order.setStatus(1);
        order.setPickupTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }
}
