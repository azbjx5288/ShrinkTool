package com.shrinktool.data;

import com.shrinktool.base.net.RequestConfig;

/**
 * 修改密码（登录密码或资金密码）
 * Created by Alashi on 2016/5/2.
 */
@RequestConfig(api = "?c=user&a=changePassword")
public class ChangePasswordCommand {
    private String oldpassword;//当sa=modifyPassword时
    private String password;//当sa=modifyPassword时
    private String password2;//当sa=modifyPassword时

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}
