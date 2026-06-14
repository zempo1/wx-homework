package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.order.dto.CategoryWithProductsVO;
import com.example.order.dto.ProductVO;
import com.example.order.entity.Category;
import com.example.order.entity.Product;
import com.example.order.mapper.CategoryMapper;
import com.example.order.mapper.ProductMapper;
import com.example.order.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public List<Category> listCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId));
    }

    @Override
    public List<ProductVO> listProducts(Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales)
                .orderByAsc(Product::getId);
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        return productMapper.selectList(wrapper).stream().map(ProductVO::from).toList();
    }

    @Override
    public List<CategoryWithProductsVO> menuTree() {
        return listCategories().stream()
                .map(category -> CategoryWithProductsVO.from(category, listProducts(category.getId())))
                .toList();
    }
}
