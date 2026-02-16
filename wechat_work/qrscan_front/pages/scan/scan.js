// pages/scan/scan.js
Page({
  data: {
    // 状态控制
    loading: true,           // 加载中
    loginSuccess: false,     // 登录成功
    loginError: false,       // 登录失败
    qrExpired: false,        // 二维码过期
    showInfoCard: true,      // 是否显示信息卡片
    
    // 文本内容
    loadingText: '正在获取登录信息...',
    successMessage: '您已成功登录',
    errorMessage: '登录过程中出现问题',
    
    // 用户信息
    openid: '',
    phoneNumber: '',
    loginTime: '',
    
    // 登录token
    loginToken: ''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('扫码进入，参数：', options);
    
    // 获取二维码中的login_token
    const loginToken = options.token || options.scene; // 兼容不同传参方式
    
    if (!loginToken) {
      this.handleError('无效的二维码，请重新扫描');
      return;
    }
    
    this.setData({
      loginToken: loginToken,
      loadingText: '正在验证二维码...'
    });
    
    // 检查二维码是否过期
    this.checkQRCodeExpired();
    
    // 自动执行登录确认
    this.confirmLogin();
  },

  /**
   * 检查二维码是否过期
   */
  checkQRCodeExpired() {
    // 这里可以调用后端接口检查二维码状态
    // 示例：如果二维码过期，直接显示过期页面
    wx.request({
      url: 'https://你的后端域名/api/qrcode/check',
      data: { token: this.data.loginToken },
      success: (res) => {
        if (res.data.expired) {
          this.setData({
            loading: false,
            qrExpired: true
          });
        }
      }
    });
  },

  /**
   * 确认登录
   */
  confirmLogin() {
    wx.showLoading({
      title: '登录中...',
      mask: true
    });

    wx.login({
      success: (loginRes) => {
        if (loginRes.code) {
          this.sendToBackend(loginRes.code);
        } else {
          this.handleError('获取登录凭证失败');
        }
      },
      fail: (err) => {
        console.error('wx.login失败', err);
        this.handleError('网络错误，请重试');
      }
    });
  },

  /**
   * 发送到后端
   */
  sendToBackend(code) {
    wx.request({
      url: 'https://你的后端域名/api/qrcode/scan-callback',
      method: 'POST',
      data: {
        loginToken: this.data.loginToken,
        code: code
      },
      success: (res) => {
        wx.hideLoading();
        
        if (res.data.success) {
          // 获取当前时间
          const now = new Date();
          const loginTime = `${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`;
          
          this.setData({
            loading: false,
            loginSuccess: true,
            openid: res.data.openid || res.data.data?.openid,
            phoneNumber: res.data.phoneNumber || res.data.data?.phoneNumber,
            loginTime: loginTime,
            successMessage: '登录成功，欢迎回来！'
          });

          // 可以设置定时器自动返回
          setTimeout(() => {
            this.goBack();
          }, 3000);

        } else {
          this.handleError(res.data.message || '登录失败');
        }
      },
      fail: (err) => {
        wx.hideLoading();
        console.error('请求失败', err);
        this.handleError('网络连接失败');
      }
    });
  },

  /**
   * 处理错误
   */
  handleError(message) {
    this.setData({
      loading: false,
      loginError: true,
      errorMessage: message || '登录失败，请重试'
    });
  },

  /**
   * 返回上一页
   */
  goBack() {
    wx.navigateBack({
      delta: 1,
      fail: () => {
        // 如果返回失败，跳转到首页
        wx.switchTab({
          url: '/pages/index/index'
        });
      }
    });
  },

  /**
   * 重试登录
   */
  retryLogin() {
    this.setData({
      loading: true,
      loginError: false,
      loadingText: '重新登录中...'
    });
    this.confirmLogin();
  }
});