const request = require("../../utils/request");
const cart = require("../../utils/cart");

let categoryPosition = [];

Page({
  data: {
    categories: [],
    activeIndex: 0,
    tapIndex: 0,
    cartList: [],
    cartCount: 0,
    cartTotal: "0.00",
    showCart: false,
  },

  disableNextScroll: false,

  onLoad() {
    this.loadMenu();
  },

  onShow() {
    this.refreshCart();
  },

  loadMenu() {
    request
      .get(
        "/menu/tree",
        {},
        {
          auth: false,
        },
      )
      .then((categories) => {
        const list = (categories || []).map((category) => {
          const products = (category.products || []).map((product) => {
            return Object.assign({}, product, {
              imageUrl: this.resolveImageUrl(product.imageUrl),
              cartQuantity: 0,
            });
          });
          return Object.assign({}, category, { products });
        });
        this.setData(
          {
            categories: this.syncProductQuantity(list),
          },
          () => {
            this.calculateCategoryPosition();
          },
        );
      });
  },

  resolveImageUrl(imageUrl) {
    if (!imageUrl || imageUrl.indexOf("/images/food/") === 0) {
      return "";
    }
    return imageUrl;
  },

  calculateCategoryPosition() {
    categoryPosition = [];
    const query = wx.createSelectorQuery();
    let top = 0;
    let height = 0;

    query.select(".product-scroll").boundingClientRect((rect) => {
      if (rect) {
        top = rect.top;
        height = rect.height;
      }
    });
    query.selectAll(".category-title").boundingClientRect((rects) => {
      (rects || []).forEach((rect) => {
        categoryPosition.push(rect.top - top - height / 3);
      });
    });
    query.exec();
  },

  tapCategory(e) {
    const index = e.currentTarget.dataset.index;
    this.disableNextScroll = true;
    this.setData({
      activeIndex: index,
      tapIndex: index,
    });
  },

  onProductScroll(e) {
    if (this.disableNextScroll) {
      this.disableNextScroll = false;
      return;
    }

    const scrollTop = e.detail.scrollTop;
    let activeIndex = 0;
    categoryPosition.forEach((position, index) => {
      if (scrollTop >= position) {
        activeIndex = index;
      }
    });

    if (activeIndex !== this.data.activeIndex) {
      this.setData({ activeIndex });
    }
  },

  addToCart(e) {
    const categoryIndex = e.currentTarget.dataset.categoryIndex;
    const productIndex = e.currentTarget.dataset.productIndex;
    const product = this.data.categories[categoryIndex].products[productIndex];
    cart.add(product);
    this.refreshCart();
    wx.showToast({
      title: "已加入购物车",
      icon: "success",
      duration: 700,
    });
  },

  decreaseProduct(e) {
    cart.decrease(Number(e.currentTarget.dataset.id));
    this.refreshCart();
  },

  refreshCart() {
    const summary = cart.getCartSummary();
    const cartList = summary.cart.map((item) => {
      const subtotal = Number(item.price * item.quantity).toFixed(2);
      return Object.assign({}, item, { subtotal });
    });
    const categories = this.syncProductQuantity(this.data.categories);

    this.setData({
      categories,
      cartList,
      cartCount: summary.count,
      cartTotal: summary.totalPrice.toFixed(2),
      showCart: summary.count > 0 ? this.data.showCart : false,
    });
  },

  syncProductQuantity(categories) {
    const cartList = cart.getCart();
    const quantityMap = {};

    cartList.forEach((item) => {
      quantityMap[item.productId] = Number(item.quantity || 0);
    });

    return (categories || []).map((category) => {
      const products = (category.products || []).map((product) => {
        return Object.assign({}, product, {
          cartQuantity: quantityMap[product.id] || 0,
        });
      });
      return Object.assign({}, category, { products });
    });
  },

  toggleCart() {
    if (this.data.cartCount === 0) {
      return;
    }
    this.setData({
      showCart: !this.data.showCart,
    });
  },

  increaseCart(e) {
    cart.increase(Number(e.currentTarget.dataset.id));
    this.refreshCart();
  },

  decreaseCart(e) {
    cart.decrease(Number(e.currentTarget.dataset.id));
    this.refreshCart();
  },

  clearCart() {
    cart.clearCart();
    this.refreshCart();
  },

  goConfirm() {
    if (this.data.cartCount === 0) {
      return;
    }
    wx.navigateTo({
      url: "/pages/order/confirm/index",
    });
  },
});
