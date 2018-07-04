package com.shrinktool.data;

import com.shrinktool.base.net.RequestConfig;

/**
 * 发送短信验证码
 * Created by Alashi on 2016/12/16.
 */
@RequestConfig(api = "?c=user&a=captcha")
public class CaptchaCommand {
    private String mobile;
    /** 验证码类型：
     Login 短信登录
     Register 手机注册
     BindMobile 手机绑定
     FindPassword 找回密码
     */
    private String type;

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setType(String type) {
        this.type = type;
    }
}
