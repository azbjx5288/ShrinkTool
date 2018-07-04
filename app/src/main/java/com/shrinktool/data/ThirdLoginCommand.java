package com.shrinktool.data;

import com.shrinktool.BuildConfig;
import com.shrinktool.base.net.RequestConfig;

/**
 * 第三方登录
 * Created by Alashi on 2016/1/5.
 */
@RequestConfig(api = "?c=user&a=login", response = UserInfo.class,
        request = LoginRequest.class)
public class ThirdLoginCommand {
    /** 第三方的用户唯一标识符 */
    private String openid;
    /** 第三方帐号的昵称 */
    private String nick_name;
    /**字符串	不可为空	随机产生的长度为6的字符串*/
    private String str;
    private int version = BuildConfig.VERSION_CODE;
    private int devicecode = 4;
    /** 固定值：
     QQ登录：qqLogin
     微信登录：weixinLogin
     */
    private String verify;

    public ThirdLoginCommand() {
        str = String.format("BUILD_TYPE[%s],VERSION_CODE[%s],VERSION_NAME[%s]", BuildConfig.BUILD_TYPE,
                BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }
}
