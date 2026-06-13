const request = require('../../utils/request')

Page({
  data: {
    shop: {
      name: '校园美食屋',
      address: '学生中心一楼 A 区',
      businessHours: '周一至周日 09:00-21:00',
      phone: '13800000000',
      notice: '欢迎使用微信小程序点餐，请凭取餐号到前台取餐。'
    }
  },

  onLoad() {
    this.loadShopInfo()
  },

  loadShopInfo() {
    request.get('/shop/info', {}, {
      auth: false,
      showLoading: false
    }).then((shop) => {
      this.setData({ shop })
    })
  },

  startOrder() {
    wx.navigateTo({
      url: '/pages/menu/index'
    })
  }
})
