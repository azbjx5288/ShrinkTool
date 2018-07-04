package com.shrinktool.data;

import com.shrinktool.base.net.RequestConfig;

/**
 * 当前用户所有信息查询
 * Created by Alashi on 2016/1/28.
 */
@RequestConfig(api = "?c=user&a=getCurrentUser", response = UserInfo.class)
public class UserInfoCommand {
}