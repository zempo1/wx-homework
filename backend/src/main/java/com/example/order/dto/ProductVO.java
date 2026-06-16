package com.example.order.dto;

import com.example.order.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {

    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Integer sales;

    public static ProductVO from(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setName(product.getName());
        vo.setDescription(product.getDescription());
        vo.setImageUrl(product.getImageUrl());
        vo.setPrice(product.getPrice());
        vo.setSales(product.getSales());
        return vo;
    }
}
