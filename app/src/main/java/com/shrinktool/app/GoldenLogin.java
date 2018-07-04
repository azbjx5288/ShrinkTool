package com.shrinktool.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.shrinktool.BuildConfig;
import com.shrinktool.R;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.component.LoginQQ;
import com.shrinktool.component.LoginWeiXin;
import com.shrinktool.component.ThirdLoginCallBack;
import com.shrinktool.data.LoginCommand;
import com.shrinktool.data.ThirdLoginCommand;
import com.shrinktool.data.UserInfo;
import com.shrinktool.fragment.FindBackPasswordFragment;
import com.shrinktool.fragment.RegisterFragment;
import com.shrinktool.fragment.SmsLoginFragment;

import org.apache.commons.codec.digest.DigestUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 登录页面
 * Created on 2015/12/31.
 * @author ACE
 *
 */

public class GoldenLogin extends Activity {
    private static final String TAG = GoldenLogin.class.getSimpleName();

    @Bind(R.id.login_edit_account) EditText userName;
    @Bind(R.id.login_edit_password) EditText password;
    @Bind(R.id.login_account_edit_clear) View userNameClear;
    @Bind(R.id.login_password_edit_clear) View passwordClear;

    private ProgressDialog progressDialog;
    private LoginQQ loginQQ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.golden_login);
        ButterKnife.bind(this);

        userNameClear.setVisibility(View.INVISIBLE);
        passwordClear.setVisibility(View.INVISIBLE);
        userName.setOnFocusChangeListener((view1, b) -> {
            if (userNameClear != null) {
                userNameClear.setVisibility(b? View.VISIBLE : View.INVISIBLE);
            }
        });

        userName.setOnEditorActionListener((v, actionId, event) -> false);
        password.setOnFocusChangeListener((view1, b) -> {
            if (passwordClear != null) {
                passwordClear.setVisibility(b? View.VISIBLE : View.INVISIBLE);
            }
        });

        password.setOnEditorActionListener((v, actionId, event) -> {
            if (checkUserInfo()) {
                login();
            }
            return false;
        });

        String name = GoldenAsiaApp.getUserCentre().getLastLoginUserName();
        if (!TextUtils.isEmpty(name)) {
            userName.setText(name);
            password.requestFocus();
        }
    }

    @OnClick({R.id.login_login_btn, R.id.login_account_edit_clear, R.id.login_password_edit_clear,
            R.id.register, R.id.close, R.id.findBackPassword, R.id.loginSMS, R.id.loginQQ,
            R.id.loginWeixin})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_account_edit_clear: //帐号清空 点帐号清空时默认清空密码
                userName.setText(null);
                password.setText(null);
                userName.requestFocus();
                break;
            case R.id.login_password_edit_clear://密码清空
                password.setText(null);
                password.requestFocus();
                break;
            case R.id.login_login_btn: //帐号登录BUT
                if (checkUserInfo()) {
                    login();
                }
                break;

            case R.id.register://注册
                launchFragmentForResult(RegisterFragment.class, null, 1001);
                break;

            case R.id.close:
                finish();
                break;
            case R.id.findBackPassword:
                launchFragmentForResult(FindBackPasswordFragment.class, null, 1001);
                break;
            case R.id.loginSMS:
                launchFragmentForResult(SmsLoginFragment.class, null, 1001);
                break;

            case R.id.loginQQ:
                loginQQ = new LoginQQ(this, thirdLoginCallBack);
                loginQQ.login();
                break;

            case R.id.loginWeixin:
                new LoginWeiXin(this, thirdLoginCallBack).login();
                break;
        }
    }

    private ThirdLoginCallBack thirdLoginCallBack = new ThirdLoginCallBack() {
        @Override
        public void onCallBack(boolean isOk, String verify, String openId, String nickname) {
            if (isOk) {
                ThirdLoginCommand command = new ThirdLoginCommand();
                command.setNick_name(nickname);
                command.setOpenid(openId);
                command.setVerify(verify);
                executeCommand(command, restCallback);
            }
        }
    };

    @Override
    protected void onDestroy() {
        RestRequestManager.cancelAll(this);
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    protected void launchFragmentForResult(Class<? extends Fragment> fragment, Bundle bundle, int requestCode) {
        FragmentLauncher.launchForResult(this, fragment.getName(), bundle, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: " + requestCode + ", " + resultCode);
        if (loginQQ != null) {
            loginQQ.onActivityResult(requestCode, resultCode, data);
            loginQQ = null;
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

    private void login() {
        LoginCommand command = new LoginCommand();
        command.setUsername(userName.getText().toString());
        command.setEncpassword(DigestUtils.md5Hex(password.getText().toString()));
        executeCommand(command, restCallback);
    }

    private RestCallback restCallback = new RestCallback<UserInfo>() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse<UserInfo> response) {
            //startActivity(new Intent(getActivity(), ContainerActivity.class));
            finish();
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            if (7004 == errCode) {
                showToast("您的用户名或密码不正确");
                return true;
            }
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (state == RestRequest.RUNNING) {
                showProgress("登录中");
            } else {
                hideProgress();
            }
        }
    };

    private void showProgress(String msg) {
        if (null == progressDialog) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(msg);
        progressDialog.show();

    }

    private void hideProgress() {
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
    }

    private void showToast(CharSequence text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private RestRequest executeCommand(Object command, RestCallback callback) {
        return RestRequestManager.executeCommand(this, command, callback, 0, this);
    }

    /**
     * 用户信息验证
     */
    private boolean checkUserInfo() {
        if (TextUtils.isEmpty(userName.getText().toString())) {
            showToast("请输入用户名");
            return false;
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            if (BuildConfig.DEBUG) {
                //测试版本时，自动填写密码为"a123456"
                password.setText("a123456");
                return true;
            }
            showToast("请输入密码");
            return false;
        }

        return true;
    }
}
