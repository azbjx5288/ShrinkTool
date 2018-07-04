package com.shrinktool.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.shrinktool.component.LoginWeiXin;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信分享的
 * Created by Alashi on 2016/11/4.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        IWXAPI api = WXAPIFactory.createWXAPI(this, "APP_ID");
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.i(TAG, "onReq: req: " + req.toString());
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onResp: resp.errCode:" + resp.errCode + ",resp.errStr:"
                + resp.errStr + ", " + resp.toString());
        SendAuth.Resp out = new SendAuth.Resp(getIntent().getExtras());
        Log.d(TAG, "onResp: " + String.format("out.errCode=%s, out.code=%s, out.state=%s, out.lang=%s, out.country=%s",
                out.errCode, out.code, out.state, out.lang, out.country));
        //微信的分享，登录都是跑到这里，但只有登录成功时，对LoginWeiXin才有用
        Intent intent = new Intent(LoginWeiXin.LOGIN_BROADCAST);
        intent.putExtras(getIntent().getExtras());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //成功
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //拒绝
                break;
        }
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        finish();
    }
}
