package com.example.order.controller;

import com.example.order.common.ApiResponse;
import com.example.order.dto.CategoryWithProductsVO;
import com.example.order.dto.ProductVO;
import com.example.order.entity.Category;
import com.example.order.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        return ApiResponse.success(menuService.menuTree());
    }
}
