const config = require('./config')

function request(options) {
  const showLoading = options.showLoading !== false
  const needAuth = options.auth !== false

  if (showLoading) {
    wx.showLoading({
      title: '加载中',
      mask: true
    })
  }

  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    const header = Object.assign({
      'content-type': 'application/json'
    }, options.header || {})

    if (needAuth && token) {
      header.Authorization = 'Bearer ' + token
    }

    wx.request({
      url: config.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      success(res) {
        const body = res.data || {}

        if (res.statusCode < 200 || res.statusCode >= 300) {
          showError('服务器异常')
          reject(res)
          return
        }

        if (body.code === 200) {
          resolve(body.data)
          return
        }

        if (body.code === 401) {
          wx.removeStorageSync('token')
          showError('请先登录')
          reject(body)
          return
        }

        showError(body.message || '请求失败')
        reject(body)
      },
      fail(err) {
        showError('网络连接失败')
        reject(err)
      },
      complete() {
        if (showLoading) {
          wx.hideLoading()
        }
      }
    })
  })
}

function showError(title) {
  wx.showToast({
    title,
    icon: 'none',
    duration: 1800
  })
}

function get(url, data, options) {
  return request(Object.assign({}, options || {}, {
    url,
    data,
    method: 'GET'
  }))
}

function post(url, data, options) {
  return request(Object.assign({}, options || {}, {
    url,
    data,
    method: 'POST'
  }))
}

function put(url, data, options) {
  return request(Object.assign({}, options || {}, {
    url,
    data,
    method: 'PUT'
  }))
}

function del(url, data, options) {
  return request(Object.assign({}, options || {}, {
    url,
    data,
    method: 'DELETE'
  }))
}

module.exports = {
  request,
  get,
  post,
  put,
  del
}
