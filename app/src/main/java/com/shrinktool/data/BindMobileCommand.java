package com.shrinktool.data;

import com.shrinktool.base.net.RequestConfig;

/**
 * 绑定手
 * Created by Alashi on 2016/12/19.
 */
@RequestConfig(api = "?c=user&a=bindMobile")
public class BindMobileCommand {
    private String mobile;
    private String code;

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
