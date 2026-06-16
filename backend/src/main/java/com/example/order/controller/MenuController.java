package com.example.order.controller;

import com.example.order.common.ApiResponse;
import com.example.order.context.UserContext;
import com.example.order.dto.CategoryWithProductsVO;
import com.example.order.dto.ProductVO;
import com.example.order.entity.Category;
import com.example.order.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/categories")
    public ApiResponse<List<Category>> categories() {
        return ApiResponse.success(menuService.listCategories());
    }

    @GetMapping("/products")
    public ApiResponse<List<ProductVO>> products(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(menuService.listProducts(categoryId));
    }

    @GetMapping("/menu/tree")
    public ApiResponse<List<CategoryWithProductsVO>> menuTree() {
        Long userId = UserContext.getUserId();
        if (userId != null) {
            return ApiResponse.success(menuService.menuTreeWithFavorites(userId));
        }
        return ApiResponse.success(menuService.menuTree());
    }

    @PutMapping("/favorites/{productId}")
    public ApiResponse<Void> toggleFavorite(@PathVariable Long productId) {
        menuService.toggleFavorite(UserContext.getUserId(), productId);
        return ApiResponse.success(null);
    }

    @GetMapping("/favorites")
    public ApiResponse<Set<Long>> listFavorites() {
        return ApiResponse.success(menuService.listFavoriteProductIds(UserContext.getUserId()));
    }
}
