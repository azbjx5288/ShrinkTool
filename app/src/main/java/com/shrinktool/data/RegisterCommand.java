package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;
import com.shrinktool.BuildConfig;
import com.shrinktool.base.net.RequestConfig;

/**
 * 注册
 * Created by Alashi on 2016/7/25.
 */
@RequestConfig(api = "?c=user&a=regUser", request = RegisterRequest.class, response = UserInfo.class)
public class RegisterCommand {
    @SerializedName("username")
    private String userName;
    private String password;
    private String password2;
    private int version = BuildConfig.VERSION_CODE;
    /** 设备来源，IOS为5，Android为4 */
    private int devicecode = 4;

    /** 手机号，手机验证码注册时必填 */
    private String mobile;
    /** 手机验证码，手机验证码注册时必填 */
    private String code;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
