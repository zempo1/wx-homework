const app = getApp();
const request = require("../../utils/request");

const DELETE_BTN_WIDTH = 80;
const SWIPE_THRESHOLD = 40;

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
    swipeIndex: -1,
    swipeOffsetX: 0,
    touching: false,
  },

  onLoad() {
    this.loadUserInfo();
  },

  onShow() {
    this.loadUserInfo();
    this.reload();
  },

  onTouchStart(e) {
    const touch = e.touches[0];
    this.touchStartX = touch.clientX;
    this.touchStartY = touch.clientY;
    this.touchMoved = false;
    this.setData({ touching: true });
  },

  onTouchMove(e) {
    const touch = e.touches[0];
    const deltaX = touch.clientX - this.touchStartX;
    const deltaY = touch.clientY - this.touchStartY;

    if (!this.touchMoved && Math.abs(deltaY) > Math.abs(deltaX)) {
      return;
    }
    this.touchMoved = true;

    const index = e.currentTarget.dataset.index;
    let offsetX = deltaX;
    if (offsetX > 0) {
      offsetX = 0;
    }
    if (offsetX < -DELETE_BTN_WIDTH) {
      offsetX = -DELETE_BTN_WIDTH;
    }
    this.setData({
      swipeIndex: index,
      swipeOffsetX: offsetX,
    });
  },

  onTouchEnd() {
    this.setData({ touching: false });
    if (!this.touchMoved) {
      return;
    }
    if (this.data.swipeOffsetX < -SWIPE_THRESHOLD) {
      this.setData({ swipeOffsetX: -DELETE_BTN_WIDTH });
    } else {
      this.setData({ swipeIndex: -1, swipeOffsetX: 0 });
    }
  },

  onRecordTap(e) {
    if (this.data.swipeIndex >= 0) {
      this.closeSwipe();
      return;
    }
  },

  closeSwipe() {
    this.setData({ swipeIndex: -1, swipeOffsetX: 0 });
  },

  deleteRecord(e) {
    const orderId = e.currentTarget.dataset.orderId;
    const index = e.currentTarget.dataset.index;
    wx.showModal({
      title: "确认删除",
      content: "删除后无法恢复，是否继续？",
      success: (res) => {
        if (!res.confirm) return;
        request.del("/history/" + orderId).then(() => {
          const records = this.data.records.slice();
          records.splice(index, 1);
          this.setData({
            records,
            swipeIndex: -1,
            swipeOffsetX: 0,
          });
          wx.showToast({ title: "已删除", icon: "success" });
        });
      },
    });
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
