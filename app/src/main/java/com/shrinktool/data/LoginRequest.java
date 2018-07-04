package com.shrinktool.data;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.user.UserCentre;

/**
 * 登录处理，主要是处理session
 * Created by Alashi on 2016/1/6.
 */
public class LoginRequest extends RestRequest {
    public LoginRequest(Context context) {
        super(context);
    }

    @Override
    protected void onBackgroundResult(NetworkResponse networkResponse, RestResponse response) {
        UserCentre userCentre = GoldenAsiaApp.getUserCentre();
        if (response.getErrNo() != 0) {
            userCentre.saveSession(null);
            return;
        }

        if (command instanceof LoginCommand) {
            userCentre.saveLoginInfo(((LoginCommand) command).getUsername(),
                    ((LoginCommand) command).getEncpassword());
        }

        UserInfo userInfo = (UserInfo) response.getData();
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
