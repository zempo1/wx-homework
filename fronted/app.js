const request = require('./utils/request')

App({
  globalData: {
    userInfo: null
  },
  loginPromise: null,

  onLaunch() {
    this.login()
  },

  login(profile) {
    if (this.loginPromise) {
      if (profile && profile.nickname) {
        return this.loginPromise.then(() => this.login(profile))
      }
      return this.loginPromise
    }

    const localUserInfo = wx.getStorageSync('userInfo') || {}
    const nickname = profile && profile.nickname ? profile.nickname : localUserInfo.nickname

    this.loginPromise = new Promise((resolve, reject) => {
      wx.login({
        success: (res) => {
          if (!res.code) {
            wx.showToast({
              title: '登录失败',
              icon: 'none'
            })
            this.loginPromise = null
            reject(new Error('wx.login no code'))
            return
          }

          const loginData = {
            code: res.code,
            mockClientId: this.getMockClientId()
          }
          if (nickname) {
            loginData.nickname = nickname
          }

          request.post('/auth/login', loginData, {
            showLoading: false,
            auth: false
          }).then((data) => {
            const userInfo = Object.assign({}, data.user || {}, {
              nickname: data.user && data.user.nickname ? data.user.nickname : (nickname || '微信用户'),
              avatarUrl: localUserInfo.avatarUrl || ''
            })
            wx.setStorageSync('token', data.token)
            wx.setStorageSync('userInfo', userInfo)
            this.globalData.userInfo = userInfo
            this.loginPromise = null
            resolve(Object.assign({}, data, { user: userInfo }))
          }).catch((err) => {
            wx.showToast({
              title: '登录失败，请稍后重试',
              icon: 'none'
            })
            this.loginPromise = null
            reject(err)
          })
        },
        fail: (err) => {
          wx.showToast({
            title: '微信登录失败',
            icon: 'none'
          })
          this.loginPromise = null
          reject(err)
        }
      })
    })

    return this.loginPromise
  },

  getMockClientId() {
    let mockClientId = wx.getStorageSync('mockClientId')
    if (!mockClientId) {
      mockClientId = 'client_' + Date.now() + '_' + Math.random().toString(16).slice(2)
      wx.setStorageSync('mockClientId', mockClientId)
    }
    return mockClientId
  },

  ensureLogin() {
    if (wx.getStorageSync('token')) {
      return Promise.resolve()
    }
    return this.login()
  }
})
