package com.shrinktool.component;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用QQ登录
 * Created by Alashi on 2016/12/27.
 */

public class LoginQQ implements IUiListener {
    private static final String TAG = "LoginQQ";

    private Activity activity;
    private ThirdLoginCallBack callBack;
    private Tencent tencent;
    private ProgressDialog progressDialog;

    public LoginQQ(Activity activity, ThirdLoginCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
    }

    public void login() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("登录中...");
        progressDialog.show();

        tencent = Tencent.createInstance("1105757316", activity.getApplicationContext());
        tencent.logout(activity);
        tencent.login(activity, "all", this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, this);
    }

    @Override
    public void onComplete(Object response) {
        //QQ登录授权成功
        Log.d(TAG, "onComplete: " + response);
        JSONObject json = ((JSONObject) response);
        try {
            if (json.getInt("ret") == 0) {
                String openID = json.getString("openid");
                String accessToken = json.getString("access_token");
                String expires = json.getString("expires_in");
                Log.d(TAG, "onComplete: " + String.format("openID=%s, accessToken=%s, expires=%s",
                        openID, accessToken, expires));

                //下面两个方法非常重要，否则会出现client request's parameters are invalid, invalid openid
                tencent.setOpenId(openID);
                tencent.setAccessToken(accessToken, expires);

                getUserInfo();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo() {
        UserInfo userInfo = new UserInfo(activity, tencent.getQQToken());
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object response) {
                Log.d(TAG, "onComplete: " + response);
                JSONObject json = ((JSONObject) response);
                try {
                    if (json.getInt("ret") == 0) {
                        String nickname = json.getString("nickname");
                        String figure = json.getString("figureurl_2");
                        Log.d(TAG, "onComplete: " + nickname + ", " + figure);
                        //TODO:登录到服务器
                        notifySucceed(tencent.getOpenId(), nickname);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                Log.d(TAG, "onError: " + uiError);
                notifyFail();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
                notifyFail();
            }
        });
    }

    @Override
    public void onError(UiError uiError) {
        Log.d(TAG, "onError: " + uiError);
        notifyFail();
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "onCancel: ");
        notifyFail();
    }

    private void notifySucceed(String openId, String nickname) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        callBack.onCallBack(true, "qqLogin", openId, nickname);
    }

    private void notifyFail() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        callBack.onCallBack(false, null, null, null);
    }
}
