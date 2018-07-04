package com.shrinktool.component;

/**
 * 第三方登录时的回调
 * Created by Alashi on 2017/1/2.
 */

public interface ThirdLoginCallBack {
    void onCallBack(boolean isOk, String verify, String openId, String nickname);
}
