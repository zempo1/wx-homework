package com.example.order.service;

import com.example.order.dto.CategoryWithProductsVO;
import com.example.order.dto.ProductVO;
import com.example.order.entity.Category;

import java.util.List;

public interface MenuService {

    List<Category> listCategories();

    List<ProductVO> listProducts(Long categoryId);

    List<CategoryWithProductsVO> menuTree();
}
