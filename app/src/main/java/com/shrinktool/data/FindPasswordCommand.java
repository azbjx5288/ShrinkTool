package com.shrinktool.data;

import com.shrinktool.base.net.RequestConfig;

/**
 * 通过手机验证码找回密码
 * Created by Alashi on 2016/12/16.
 */
@RequestConfig(api = "?c=user&a=findPassword")
public class FindPasswordCommand {
    private String mobile;
    private String code;
    private String password;

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
