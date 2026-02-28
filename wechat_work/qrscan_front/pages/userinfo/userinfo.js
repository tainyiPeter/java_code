// pages/index/index.js
Page({
  data: {
    code: '',
    phoneNumber: '',
    userInfo: {}
  },

  onLoad(options) {
    console.log('扫码参数：', options);
    // 页面加载时获取登录 code
    this.login();
  },

  login() {
    wx.login({
      success: (res) => {
        if (res.code) {
          this.setData({ code: res.code });
          console.log('获取code成功：', res.code);
        }
      }
    });
  },

  onGetPhoneNumber(e) {
    if (e.detail.errMsg !== 'getPhoneNumber:ok') {
      wx.showToast({ title: '需要授权手机号', icon: 'none' });
      return;
    }

    const { encryptedData, iv } = e.detail;
    
    // 调用 Python 后端接口
    wx.request({
      //url: 'http://你的服务器IP:5000/api/decode-phone',  // 替换为你的服务器地址
      url: 'https://91qj1470uc04.vicp.fun/api/decode-phone',
      method: 'POST',
      data: {
        code: this.data.code,
        encryptedData: encryptedData,
        iv: iv
      },
      success: (res) => {
        if (res.data.success) {
          this.setData({
            phoneNumber: res.data.phoneNumber
          });
          console.log('手机号解密成功：', res.data);
          wx.showToast({ title: '获取成功', icon: 'success' });
        } else {
          wx.showToast({ title: res.data.message || '获取失败', icon: 'none' });
        }
      },
      fail: (err) => {
        console.error('请求失败：', err);
        wx.showToast({ title: '网络错误', icon: 'none' });
      }
    });
  },

  onGetUserProfile() {
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        this.setData({ userInfo: res.userInfo });
        console.log('用户信息：', res.userInfo);
      }
    });
  }
});