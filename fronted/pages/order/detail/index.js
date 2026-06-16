const app = getApp()
const request = require('../../../utils/request')

Page({
  data: {
    order: {},
    statusText: ''
  },

  onLoad(options) {
    this.orderId = options.id
    this.loadDetail()
  },

  loadDetail() {
    if (!this.orderId) {
      wx.showToast({
        title: '订单不存在',
        icon: 'none'
      })
      return
    }

    app.ensureLogin().then(() => {
      return request.get('/orders/' + this.orderId)
    }).then((order) => {
      this.setData({
        order,
        statusText: this.getStatusText(order.status)
      })
    })
  },

  getStatusText(status) {
    if (status === 1) {
      return '已取餐'
    }
    if (status === 2) {
      return '已取消'
    }
    return '正在制作中，请等待叫号'
  },

  confirmPickup() {
    wx.showModal({
      title: '确认取餐',
      content: '确认已取餐？取餐后订单将无法修改。',
      success: (res) => {
        if (res.confirm) {
          this.doPickup()
        }
      }
    })
  },

  doPickup() {
    app.ensureLogin().then(() => {
      return request.put('/orders/' + this.orderId + '/pickup')
    }).then(() => {
      wx.showToast({
        title: '取餐成功',
        icon: 'success'
      })
      this.loadDetail()
    })
  }
})
