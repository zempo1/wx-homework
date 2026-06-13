-- 点餐微信小程序数据库初始化脚本
-- MySQL 8.x / InnoDB / utf8mb4

CREATE DATABASE IF NOT EXISTS order_miniprogram
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE order_miniprogram;

DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  openid VARCHAR(100) NOT NULL UNIQUE COMMENT '微信用户唯一标识',
  nickname VARCHAR(50) DEFAULT NULL COMMENT '用户昵称',
  avatar_url VARCHAR(255) DEFAULT NULL COMMENT '用户头像地址',
  phone VARCHAR(20) DEFAULT NULL COMMENT '用户手机号',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '用户状态：1正常，0禁用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '分类状态：1启用，0禁用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

CREATE TABLE product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
  category_id BIGINT NOT NULL COMMENT '所属分类ID',
  name VARCHAR(80) NOT NULL COMMENT '商品名称',
  description VARCHAR(255) DEFAULT NULL COMMENT '商品描述',
  image_url VARCHAR(255) DEFAULT NULL COMMENT '商品图片地址',
  price DECIMAL(10,2) NOT NULL COMMENT '商品单价',
  stock INT NOT NULL DEFAULT 999 COMMENT '库存数量',
  sales INT NOT NULL DEFAULT 0 COMMENT '销量',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '商品状态：1上架，0下架',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_product_category_id (category_id),
  CONSTRAINT fk_product_category
    FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  user_id BIGINT NOT NULL COMMENT '下单用户ID',
  order_no VARCHAR(40) NOT NULL UNIQUE COMMENT '订单编号',
  pickup_no VARCHAR(10) NOT NULL COMMENT '取餐号，如A018',
  total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  remark VARCHAR(255) DEFAULT NULL COMMENT '订单备注',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0未取餐，1已取餐，2已取消',
  pay_status TINYINT NOT NULL DEFAULT 1 COMMENT '支付状态：0未支付，1已支付',
  pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
  pickup_time DATETIME DEFAULT NULL COMMENT '取餐时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_orders_user_id (user_id),
  INDEX idx_orders_create_time (create_time),
  CONSTRAINT fk_orders_user
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单明细ID',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  product_name VARCHAR(80) NOT NULL COMMENT '下单时商品名称快照',
  product_image VARCHAR(255) DEFAULT NULL COMMENT '下单时商品图片快照',
  product_price DECIMAL(10,2) NOT NULL COMMENT '下单时商品单价快照',
  quantity INT NOT NULL COMMENT '购买数量',
  subtotal DECIMAL(10,2) NOT NULL COMMENT '小计金额',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_order_item_order_id (order_id),
  INDEX idx_order_item_product_id (product_id),
  CONSTRAINT fk_order_item_order
    FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_order_item_product
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

INSERT INTO category (id, name, sort, status) VALUES
(1, '热销套餐', 1, 1),
(2, '招牌主食', 2, 1),
(3, '小吃甜品', 3, 1),
(4, '饮品', 4, 1);

INSERT INTO product (category_id, name, description, image_url, price, stock, sales, status) VALUES
(1, '招牌牛肉饭套餐', '牛肉饭搭配例汤和小菜', '/images/food/beef-rice.jpg', 28.00, 999, 80, 1),
(1, '香辣鸡腿饭套餐', '香辣鸡腿饭搭配饮品', '/images/food/chicken-rice.jpg', 26.00, 999, 66, 1),
(2, '番茄鸡蛋面', '酸甜番茄汤底，适合清淡口味', '/images/food/noodle.jpg', 18.00, 999, 45, 1),
(2, '黑椒牛柳意面', '黑椒风味，口感浓郁', '/images/food/pasta.jpg', 32.00, 999, 39, 1),
(3, '黄金薯条', '外酥里软，现炸现卖', '/images/food/fries.jpg', 12.00, 999, 120, 1),
(3, '双皮奶', '经典广式甜品', '/images/food/milk-pudding.jpg', 10.00, 999, 52, 1),
(4, '柠檬红茶', '清爽解腻', '/images/food/lemon-tea.jpg', 8.00, 999, 98, 1),
(4, '冰美式咖啡', '冷萃风味，少糖低负担', '/images/food/coffee.jpg', 12.00, 999, 61, 1);
