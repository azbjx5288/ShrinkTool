package com.shrinktool.data;

import com.android.volley.Request;
import com.shrinktool.base.net.RequestConfig;

/**
 * Banner列表
 * Created by Alashi on 2016/1/19.
 */
@RequestConfig(api = "?a=bannerList", method = Request.Method.GET)
public class BannerListCommand {
    /** 1：登录页；2：开奖页 */
    private int type;

    public void setType(int type) {
        this.type = type;
    }
}
