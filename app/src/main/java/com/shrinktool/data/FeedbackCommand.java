package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;
import com.shrinktool.base.net.RequestConfig;

/**
 * 意见反馈
 * Created by Alashi on 2017/3/29.
 */
@RequestConfig(api = "?c=default&a=saveGuestBook")
public class FeedbackCommand {
    private String content;
    private String version;
    @SerializedName("mobile_type")
    private String mobileType;

    public void setContent(String content) {
        this.content = content;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setMobileType(String mobileType) {
        this.mobileType = mobileType;
    }
}
