package com.shrinktool.data;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.user.UserCentre;

/**
 * 注册的处理，主要是处理session
 * Created by Alashi on 2016/1/6.
 */
public class RegisterRequest extends RestRequest {
    public RegisterRequest(Context context) {
        super(context);
    }

    @Override
    protected void onBackgroundResult(NetworkResponse networkResponse, RestResponse response) {
        UserCentre userCentre = GoldenAsiaApp.getUserCentre();
        if (response.getErrNo() != 0) {
            userCentre.saveSession(null);
            return;
        }
        userCentre.saveLoginInfo(((RegisterCommand)command).getUserName(),
                ((RegisterCommand)command).getPassword());
        UserInfo userInfo = (UserInfo) response.getData();
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUserName(((RegisterCommand)command).getUserName());
        }
        userCentre.setUserInfo(userInfo);

        String cookie = networkResponse.headers.get("Set-Cookie");
        if (cookie == null) {
            return;
        }
        String[] cookies = cookie.split(";");
        for (String s: cookies) {
            if (s.startsWith("filterSESSID=")) {
                userCentre.saveSession(s);
                break;
            }
        }
    }
}
