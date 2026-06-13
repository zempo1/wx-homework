package com.example.order.dto;

import com.example.order.entity.Category;
import lombok.Data;

import java.util.List;

@Data
public class CategoryWithProductsVO {

    private Long id;
    private String name;
    private List<ProductVO> products;

    public static CategoryWithProductsVO from(Category category, List<ProductVO> products) {
        CategoryWithProductsVO vo = new CategoryWithProductsVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setProducts(products);
        return vo;
    }
}
