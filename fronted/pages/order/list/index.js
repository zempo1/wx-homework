const app = getApp()
const request = require('../../../utils/request')

Page({
  data: {
    orders: [],
    page: 1,
    pageSize: 10,
    finished: false,
    loading: false
  },

  onShow() {
    this.reload()
  },

  onPullDownRefresh() {
    this.reload(() => {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom() {
    if (this.data.finished || this.data.loading) {
      return
    }
    this.loadOrders(this.data.page + 1)
  },

  reload(done) {
    this.setData({
      orders: [],
      page: 1,
      finished: false
    })
    this.loadOrders(1, done)
  },

  loadOrders(page, done) {
    this.setData({ loading: true })
    app.ensureLogin().then(() => {
      return request.get('/orders', {
        page,
        pageSize: this.data.pageSize
      }, {
        showLoading: page === 1
      })
    }).then((res) => {
      const records = (res.records || []).map((item) => {
        return Object.assign({}, item, this.getStatusInfo(item.status))
      })
      this.setData({
        orders: page === 1 ? records : this.data.orders.concat(records),
        page,
        finished: records.length < this.data.pageSize,
        loading: false
      })
      if (done) {
        done()
      }
    }).catch(() => {
      this.setData({ loading: false })
      if (done) {
        done()
      }
    })
  },

  getStatusInfo(status) {
    if (status === 1) {
      return {
        statusText: '已取餐',
        statusClass: 'done'
      }
    }
    if (status === 2) {
      return {
        statusText: '已取消',
        statusClass: 'cancel'
      }
    }
    return {
      statusText: '未取餐',
      statusClass: 'waiting'
    }
  },

  goDetail(e) {
    wx.navigateTo({
      url: '/pages/order/detail/index?id=' + e.currentTarget.dataset.id
    })
  }
})
