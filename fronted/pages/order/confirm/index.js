const app = getApp()
const request = require('../../../utils/request')
const cart = require('../../../utils/cart')

Page({
  data: {
    cartList: [],
    totalPrice: '0.00',
    remark: '',
    submitting: false
  },

  onLoad() {
    this.loadCart()
  },

  onShow() {
    this.loadCart()
  },

  loadCart() {
    const summary = cart.getCartSummary()
    if (summary.count === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 600)
      return
    }

    this.setData({
      cartList: summary.cart.map((item) => {
        return Object.assign({}, item, {
          subtotal: Number(item.price * item.quantity).toFixed(2)
        })
      }),
      totalPrice: summary.totalPrice.toFixed(2)
    })
  },

  onRemarkInput(e) {
    this.setData({
      remark: e.detail.value
    })
  },

  submitOrder() {
    if (this.data.submitting) {
      return
    }

    this.setData({ submitting: true })
    app.ensureLogin().then(() => {
      return request.post('/orders', {
        items: cart.toOrderItems(),
        totalAmount: Number(this.data.totalPrice),
        remark: this.data.remark
      })
    }).then((order) => {
      cart.clearCart()
      wx.showToast({
        title: '提交成功',
        icon: 'success'
      })
      wx.redirectTo({
        url: '/pages/order/detail/index?id=' + order.id
      })
    }).catch(() => {
      this.setData({ submitting: false })
    })
  }
})
