package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.order.dto.CategoryWithProductsVO;
import com.example.order.dto.ProductVO;
import com.example.order.entity.Category;
import com.example.order.entity.Product;
import com.example.order.entity.ProductFavorite;
import com.example.order.mapper.CategoryMapper;
import com.example.order.mapper.ProductFavoriteMapper;
import com.example.order.mapper.ProductMapper;
import com.example.order.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final ProductFavoriteMapper productFavoriteMapper;

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

    @Override
    public void toggleFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<ProductFavorite> wrapper = new LambdaQueryWrapper<ProductFavorite>()
                .eq(ProductFavorite::getUserId, userId)
                .eq(ProductFavorite::getProductId, productId);
        ProductFavorite existing = productFavoriteMapper.selectOne(wrapper);
        if (existing != null) {
            productFavoriteMapper.deleteById(existing.getId());
        } else {
            ProductFavorite fav = new ProductFavorite();
            fav.setUserId(userId);
            fav.setProductId(productId);
            fav.setCreateTime(LocalDateTime.now());
            productFavoriteMapper.insert(fav);
        }
    }

    @Override
    public Set<Long> listFavoriteProductIds(Long userId) {
        return productFavoriteMapper.selectList(new LambdaQueryWrapper<ProductFavorite>()
                        .eq(ProductFavorite::getUserId, userId))
                .stream()
                .map(ProductFavorite::getProductId)
                .collect(Collectors.toSet());
    }

    @Override
    public List<CategoryWithProductsVO> menuTreeWithFavorites(Long userId) {
        Set<Long> favoriteIds = listFavoriteProductIds(userId);
        return listCategories().stream()
                .map(category -> {
                    List<ProductVO> products = listProducts(category.getId()).stream().map(p -> {
                        p.setFavorited(favoriteIds.contains(p.getId()));
                        return p;
                    }).toList();
                    return CategoryWithProductsVO.from(category, products);
                })
                .toList();
    }
}
