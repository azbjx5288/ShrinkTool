package com.shrinktool.data;

import com.shrinktool.BuildConfig;
import com.shrinktool.base.net.RequestConfig;

/**
 * 手机客户端登录验证
 * Created by Alashi on 2016/1/5.
 */
@RequestConfig(api = "?c=user&a=login", response = UserInfo.class,
        request = LoginRequest.class)
public class SmsLoginCommand {
    /** 手机号码 */
    private String mobile;
    /** 验证码 */
    private String code;
    /**字符串	不可为空	随机产生的长度为6的字符串*/
    private String str;
    private int version = BuildConfig.VERSION_CODE;
    private int devicecode = 4;
    private String verify = "captchaLogin";

    public SmsLoginCommand() {
        str = String.format("BUILD_TYPE[%s],VERSION_CODE[%s],VERSION_NAME[%s]", BuildConfig.BUILD_TYPE,
                BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
