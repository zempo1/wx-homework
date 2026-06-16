package com.example.order.service;

import com.example.order.dto.CategoryWithProductsVO;
import com.example.order.dto.ProductVO;
import com.example.order.entity.Category;

import java.util.List;
import java.util.Set;

public interface MenuService {

    List<Category> listCategories();

    List<ProductVO> listProducts(Long categoryId);

    List<CategoryWithProductsVO> menuTree();

    void toggleFavorite(Long userId, Long productId);

    Set<Long> listFavoriteProductIds(Long userId);

    List<CategoryWithProductsVO> menuTreeWithFavorites(Long userId);
}
