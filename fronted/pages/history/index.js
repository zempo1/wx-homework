const app = getApp();
const request = require("../../utils/request");

Page({
  data: {
    userInfo: {
      nickname: "微信用户",
      avatarUrl: "",
    },
    records: [],
    page: 1,
    pageSize: 10,
    finished: false,
    loading: false,
  },

  onLoad() {
    this.loadUserInfo();
  },

  onShow() {
    this.loadUserInfo();
    this.reload();
  },

  loadUserInfo() {
    const userInfo = wx.getStorageSync("userInfo") || {};
    this.setData({
      userInfo: {
        nickname: userInfo.nickname || "微信用户",
        avatarUrl: userInfo.avatarUrl || "",
      },
    });
  },

  onNicknameInput(e) {
    this.setData({
      "userInfo.nickname": e.detail.value,
    });
  },

  onChooseAvatar(e) {
    const tempAvatarUrl = e.detail.avatarUrl;
    if (!tempAvatarUrl) {
      return;
    }

    wx.getFileSystemManager().saveFile({
      tempFilePath: tempAvatarUrl,
      success: (res) => {
        const userInfo = Object.assign({}, this.data.userInfo, {
          avatarUrl: res.savedFilePath,
        });
        wx.setStorageSync("userInfo", userInfo);
        app.globalData.userInfo = userInfo;
        this.setData({ userInfo });
        this.saveProfile();
      },
      fail: () => {
        wx.showToast({
          title: "头像保存失败",
          icon: "none",
        });
      },
    });
  },

  saveProfile() {
    const nickname = (this.data.userInfo.nickname || "").trim();
    if (!nickname) {
      wx.showToast({
        title: "请输入昵称",
        icon: "none",
      });
      return;
    }

    const userInfo = Object.assign({}, this.data.userInfo, { nickname });
    wx.setStorageSync("userInfo", userInfo);
    app.globalData.userInfo = userInfo;

    app.login({ nickname }).then((data) => {
      const mergedUserInfo = Object.assign({}, data.user || {}, {
        nickname,
        avatarUrl: userInfo.avatarUrl || "",
      });
      wx.setStorageSync("userInfo", mergedUserInfo);
      app.globalData.userInfo = mergedUserInfo;
      this.setData({ userInfo: mergedUserInfo });
      wx.showToast({
        title: "保存成功",
        icon: "success",
      });
    });
  },

  onPullDownRefresh() {
    this.reload(() => {
      wx.stopPullDownRefresh();
    });
  },

  onReachBottom() {
    if (this.data.finished || this.data.loading) {
      return;
    }
    this.loadHistory(this.data.page + 1);
  },

  reload(done) {
    this.setData({
      records: [],
      page: 1,
      finished: false,
    });
    this.loadHistory(1, done);
  },

  loadHistory(page, done) {
    this.setData({ loading: true });
    app
      .ensureLogin()
      .then(() => {
        return request.get(
          "/history",
          {
            page,
            pageSize: this.data.pageSize,
          },
          {
            showLoading: page === 1,
          },
        );
      })
      .then((res) => {
        const records = res.records || [];
        this.setData({
          records: page === 1 ? records : this.data.records.concat(records),
          page,
          finished: records.length < this.data.pageSize,
          loading: false,
        });
        if (done) {
          done();
        }
      })
      .catch(() => {
        this.setData({ loading: false });
        if (done) {
          done();
        }
      });
  },
});
