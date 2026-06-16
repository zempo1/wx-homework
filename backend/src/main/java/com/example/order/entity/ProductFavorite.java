package com.example.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_favorite")
public class ProductFavorite {

    private Long id;
    private Long userId;
    private Long productId;
    private LocalDateTime createTime;
}
