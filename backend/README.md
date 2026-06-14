# 点餐微信小程序后端

## 技术栈

- Java 17
- Spring Boot 3.2.5
- MyBatis-Plus 3.5.7
- MySQL 8.x

## 启动步骤

1. 在 MySQL 中执行 `sql/schema.sql`。
2. 修改 `src/main/resources/application.yml` 中的数据库账号和密码。
3. 在 IDEA 中打开 `backend`，刷新 Maven 依赖。
4. 运行 `com.example.order.OrderApplication`。

默认端口为 `8080`，接口统一前缀为 `/api`。

## 登录说明

`POST /api/auth/login` 接收微信小程序 `wx.login` 返回的 `code`。

如果 `application.yml` 中没有配置真实的 `app.wx.appid` 和 `app.wx.secret`，后端会自动使用 `mock_openid_ + code` 作为测试用户 openid，方便本地答辩运行。

前端请求订单相关接口时，请在请求头中携带：

```text
Authorization: Bearer <token>
```

## 核心接口

| 方法 | 地址 | 说明 | 是否需要登录 |
|---|---|---|---|
| POST | `/api/auth/login` | 用户登录/自动注册 | 否 |
| GET | `/api/shop/info` | 商家首页信息 | 否 |
| GET | `/api/categories` | 商品分类列表 | 否 |
| GET | `/api/products?categoryId=1` | 分类商品列表 | 否 |
| GET | `/api/menu/tree` | 分类和商品树 | 否 |
| POST | `/api/orders` | 创建订单 | 是 |
| GET | `/api/orders/{id}` | 订单详情 | 是 |
| GET | `/api/orders?page=1&pageSize=10` | 订单列表 | 是 |
| GET | `/api/history?page=1&pageSize=10` | 消费记录 | 是 |

## 创建订单请求示例

```json
{
  "remark": "不要辣",
  "totalAmount": 36.00,
  "items": [
    {
      "productId": 1,
      "quantity": 1
    },
    {
      "productId": 7,
      "quantity": 1
    }
  ]
}
```

注意：`totalAmount` 只作为前端展示参考，后端会根据数据库商品价格重新计算订单总金额。
