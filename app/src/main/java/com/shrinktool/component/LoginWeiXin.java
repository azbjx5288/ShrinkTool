package com.shrinktool.component;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Keep;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shrinktool.base.net.GsonHelper;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 微信登陆
 * Created by Alashi on 2016/12/27.
 */

public class LoginWeiXin {
    public static final String LOGIN_KEY = "wechat_request_login";
    public static final String LOGIN_BROADCAST = "wechat_request_login_broadcast";
    private static final String TAG = "LoginWeiXin";

    private static final String WEIXIN_APP_ID = "wx9f419ba4a6b0b0a3";//应用唯一标识，在微信开放平台提交应用审核通过后获得
    private static final String WEIXIN_APP_SECRET = "591ddbbb38f85c5336f1d5d23c4a3776";//应用密钥AppSecret，在微信开放平台提交应用审核通过后获得

    private Activity activity;
    private ThirdLoginCallBack callBack;
    private BroadcastReceiver broadcastReceiver;
    private ProgressDialog progressDialog;

    public LoginWeiXin(Activity activity, ThirdLoginCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: ");
                LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
                SendAuth.Resp out = new SendAuth.Resp(intent.getExtras());
                if (out.errCode == 0 && LOGIN_KEY.equals(out.state)) {
                    //登录成功。
                    getToken(out.code);
                } else {
                    //不是登录的或登录失败
                    notifyFail();
                }
            }
        };

        LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiver,
                new IntentFilter(LOGIN_BROADCAST));
    }

    public void login() {
        Log.d(TAG, "login: ");
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("登录中...");
        progressDialog.show();
        
        IWXAPI wxApi = WXAPIFactory.createWXAPI(activity, WEIXIN_APP_ID);
        wxApi.registerApp(WEIXIN_APP_ID);
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = LOGIN_KEY;
        wxApi.sendReq(req);//执行完毕这句话之后，会在WXEntryActivity回调
    }

    //这个方法会取得accesstoken  和openID
    private void getToken(String code){
        // Instantiate the RequestQueue.
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + WEIXIN_APP_ID + "&secret="
                + WEIXIN_APP_SECRET + "&code="
                + code + "&grant_type=authorization_code";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    AccessInfo accessInfo = GsonHelper.fromJson(response, AccessInfo.class);
                    getUserInfo(accessInfo.access_token, accessInfo.openid);
                }, error -> notifyFail());
        // Add the request to the RequestQueue.
        Volley.newRequestQueue(activity).add(stringRequest);
    }

    @Keep
    private class AccessInfo{
        /**
         * access_token : ACCESS_TOKEN
         * expires_in : 7200
         * refresh_token : REFRESH_TOKEN
         * openid : OPENID
         * scope : SCOPE
         * unionid : o6_bmasdasdsad6_2sgVt7hMZOPfL
         */

        public String access_token;
        public int expires_in;
        public String refresh_token;
        public String openid;
        public String scope;
        public String unionid;
    }

    @Keep
    private class UserInfo {

        /**
         * openid : OPENID
         * nickname : NICKNAME
         * sex : 1
         * province : PROVINCE
         * city : CITY
         * country : COUNTRY
         * headimgurl : http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0
         * privilege : ["PRIVILEGE1","PRIVILEGE2"]
         * unionid :  o6_bmasdasdsad6_2sgVt7hMZOPfL
         */

        public String openid;
        public String nickname;
        public int sex;
        public String province;
        public String city;
        public String country;
        public String headimgurl;
        public String unionid;
        public List<String> privilege;
    }

    //获取到token和openID之后，调用此接口得到身份信息
    private void getUserInfo(String token,String openID){
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" +token+"&openid=" +openID;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> notifySucceed(GsonHelper.fromJson(response, UserInfo.class)), error -> notifyFail()){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        // Add the request to the RequestQueue.
        Volley.newRequestQueue(activity).add(stringRequest);
    }

    private void notifySucceed(UserInfo userInfo){
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        callBack.onCallBack(true, "weixinLogin", userInfo.openid, userInfo.nickname);
    }
    
    private void notifyFail() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (callBack != null) {
            callBack.onCallBack(false, null, null, null);
        }
    }
}
