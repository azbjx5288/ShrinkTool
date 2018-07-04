package com.shrinktool.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.CaptchaCommand;
import com.shrinktool.data.RegisterCommand;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 用户注册
 * Created by Alashi on 2016/7/25.
 */
public class RegisterFragment extends BaseFragment {
    private static final String TAG = "RegisterFragment";

    private static final int ID_CAPTCHA = 1;
    private static final int ID_REGISTER_BY_PHONE = 2;
    private static final int ID_REGISTER_BY_NAME = 3;

    @Bind(R.id.findRadioGroup) RadioGroup radioGroup;

    @Bind(R.id.userNameLayout) View userNameLayout;
    @Bind(R.id.userName) EditText userName;
    @Bind(R.id.newPassword) EditText newPassword;
    @Bind(R.id.newPasswordVerify) EditText newPasswordVerify;
    @Bind(R.id.userNameClear) View userNameClear;
    @Bind(R.id.newPasswordClear) View newPasswordClear;
    @Bind(R.id.newPasswordVerifyClear) View newPasswordVerifyClear;

    @Bind(R.id.cellphoneLayout) View cellphoneLayout;
    @Bind(R.id.cellphoneNumber) EditText cellphoneNumber;
    @Bind(R.id.cellphoneVerificationCode) EditText cellphoneVerificationCode;
    @Bind(R.id.cellphonePassword) EditText cellphonePassword;
    @Bind(R.id.cellphoneNumberClear) View cellphoneNumberClear;
    @Bind(R.id.cellphonePasswordClear) View cellphonePasswordClear;
    @Bind(R.id.cellphoneVerificationCodeButton) Button cellphoneVerificationCodeButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater, container, "注册", R.layout.register);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTab();
        initCellphoneLayout();
        initUserNameLayout();
    }

    private void initTab() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton1) {
                cellphoneLayout.setVisibility(View.VISIBLE);
                userNameLayout.setVisibility(View.GONE);
            } else {
                cellphoneLayout.setVisibility(View.GONE);
                userNameLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initCellphoneLayout(){
        cellphoneNumberClear.setVisibility(View.INVISIBLE);
        cellphoneNumber.setOnFocusChangeListener((view1, b) -> {
            if (cellphoneNumberClear != null) {
                cellphoneNumberClear.setVisibility(b? View.VISIBLE : View.INVISIBLE);
            }
        });

        cellphonePasswordClear.setVisibility(View.INVISIBLE);
        cellphonePassword.setOnFocusChangeListener((view1, b) -> {
            if (cellphonePasswordClear != null) {
                cellphonePasswordClear.setVisibility(b? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private void initUserNameLayout(){
        userNameClear.setVisibility(View.INVISIBLE);
        userName.setOnFocusChangeListener((view1, b) -> {
            if (userNameClear != null) {
                userNameClear.setVisibility(b? View.VISIBLE : View.INVISIBLE);
            }
        });

        newPasswordClear.setVisibility(View.INVISIBLE);
        newPassword.setOnFocusChangeListener((view1, b) -> {
            if (newPasswordClear != null) {
                newPasswordClear.setVisibility(b? View.VISIBLE : View.INVISIBLE);
            }
        });

        newPasswordVerifyClear.setVisibility(View.INVISIBLE);
        newPasswordVerify.setOnFocusChangeListener((view1, b) -> {
            if (newPasswordVerifyClear != null) {
                newPasswordVerifyClear.setVisibility(b? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    @OnClick({R.id.cellphoneNumberClear, R.id.cellphonePasswordClear, R.id.submit,
            R.id.cellphoneVerificationCodeButton, R.id.cellphoneOk, R.id.cellphoneAgreement,
            R.id.userNameAgreement, R.id.userNameClear, R.id.newPasswordClear, R.id.newPasswordVerifyClear })
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.cellphoneNumberClear:
                cellphoneNumber.setText(null);
                cellphoneVerificationCode.setText(null);
                cellphonePassword.setText(null);
                cellphoneNumber.requestFocus();
                break;

            case R.id.cellphonePasswordClear:
                cellphonePassword.setText(null);
                cellphonePassword.requestFocus();
                break;

            case R.id.userNameClear:
                userName.setText(null);
                userName.requestFocus();
                break;

            case R.id.newPasswordClear:
                newPassword.setText(null);
                newPassword.requestFocus();
                break;

            case R.id.newPasswordVerifyClear:
                newPasswordVerify.setText(null);
                newPasswordVerify.requestFocus();
                break;

            case R.id.submit:
                registerByUserName();
                break;

            case R.id.cellphoneVerificationCodeButton:
                if (view.isEnabled()) {
                    requestVerificationCode();
                }
                break;

            case R.id.cellphoneOk:
                //用手机号注册
                registerByCellphone();
                break;
            case R.id.userNameAgreement:
            case R.id.cellphoneAgreement:
                showAgreement();
                break;
        }
    }

    private void showAgreement() {
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);
        dialog.setContentView(R.layout.register_agreement);
        dialog.findViewById(R.id.registerAgreementOK).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.registerAgreementClose).setOnClickListener(v -> dialog.dismiss());
        WebView webView = (WebView) dialog.findViewById(R.id.registerAgreementWebView);
        webView.loadUrl("file:///android_asset/web2/agreement.html");
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window .setGravity(Gravity.BOTTOM);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window .setAttributes(lp);
        dialog.show();
    }

    private void registerByCellphone() {
        if (!isValidCellphoneNumber()) {
            return;
        }
        if (!checkCellphoneCode()) {
            return;
        }
        if (!checkCellphonePassword()) {
            return;
        }

        RegisterCommand command = new RegisterCommand();
        command.setMobile(cellphoneNumber.getText().toString());
        command.setCode(cellphoneVerificationCode.getText().toString());
        command.setPassword(cellphonePassword.getText().toString());
        command.setPassword2(cellphonePassword.getText().toString());

        executeCommand(command, callback, ID_REGISTER_BY_PHONE);
    }

    private void requestVerificationCode() {
        if (!isValidCellphoneNumber()) {
            return;
        }

        cellphoneVerificationCodeButton.setEnabled(false);
        startCountDown();

        CaptchaCommand command = new CaptchaCommand();
        command.setMobile(cellphoneNumber.getText().toString());
        command.setType("Register");
        executeCommand(command, callback, ID_CAPTCHA);
    }

    private void startCountDown() {
        long startTime = System.currentTimeMillis();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) {
                    return;
                }
                long second = 60 - (System.currentTimeMillis() - startTime) / 1000;
                if (second <= 0) {
                    cellphoneVerificationCodeButton.setText("获取验证码");
                    cellphoneVerificationCodeButton.setEnabled(true);
                } else {
                    cellphoneVerificationCodeButton.setText(second + "s");
                    handler.postDelayed(this, 300);
                }
            }
        };

        handler.post(runnable);
    }

    private boolean isValidCellphoneNumber() {
        String number = cellphoneNumber.getText().toString();
        if (!number.matches("^1\\d{10}$")) {
            showToast("请输入正确手机号");
            return false;
        }
        return true;
    }

    private void registerByUserName() {
        if (!(checkUserName())) {
            return;
        }

        RegisterCommand command = new RegisterCommand();
        command.setUserName(userName.getText().toString());
        command.setPassword(newPassword.getText().toString());
        command.setPassword2(newPasswordVerify.getText().toString());

        executeCommand(command, callback, ID_REGISTER_BY_NAME);
    }

    private RestCallback callback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            switch (request.getId()) {
                case ID_REGISTER_BY_NAME:
                    //用户注册时多一步“绑定手机”
                    launchFragment(BindPhoneFragment.class);
                case ID_REGISTER_BY_PHONE:
                    showToast("注册成功", Toast.LENGTH_SHORT);
                    //startActivity(new Intent(getContext(), ContainerActivity.class));
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                    break;
                case ID_CAPTCHA:
                    showToast("已发送短信验证码");
                    return true;
            }
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            switch (request.getId()) {
                case ID_REGISTER_BY_PHONE:
                case ID_REGISTER_BY_NAME:
                    showToast(errDesc, Toast.LENGTH_SHORT);
                    break;
                case ID_CAPTCHA:
                    showToast("短信验证码发送失败");
                    return false;
            }
            return true;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (request.getId() == ID_REGISTER_BY_PHONE || request.getId() == ID_REGISTER_BY_NAME) {
                if (state == RestRequest.RUNNING) {
                    showProgress("正在注册用户...");
                } else {
                    hideProgress();
                }
            }
        }
    };

    private boolean checkCellphoneCode() {
        String code = cellphoneVerificationCode.getText().toString();
        if (code.isEmpty()) {
            showToast("请输入验证码", Toast.LENGTH_SHORT);
            return false;
        }

        if (!code.matches("^\\d+$")) {
            showToast("请输入正确验证码", Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    private boolean checkCellphonePassword() {
        String newP = cellphonePassword.getText().toString();
        if (newP.isEmpty()) {
            showToast("请输入密码", Toast.LENGTH_SHORT);
            return false;
        }

        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,15}$";
        if (!newP.matches(regex)) {
            showToast("密码长度为6-15字符，不能为纯数字或纯字母", Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    /**
     * 1：用户名
     提示;用户名长度为5-12个字符，且必须以字母开头
     2：密码
     提示;密码长度为6-15字符，不能为纯数字或纯字母
     */
    private boolean checkUserName() {
        String name = userName.getText().toString();
        String newP = newPassword.getText().toString();
        String newPv = newPasswordVerify.getText().toString();

        if (name.isEmpty()) {
            showToast("请输入用户名", Toast.LENGTH_SHORT);
            return false;
        }

        if (!name.matches("^[a-zA-Z][\\w]{4,11}")) {
            showToast("用户名长度为5-12个字符，且必须以字母开头", Toast.LENGTH_SHORT);
            return false;
        }

        if (newP.isEmpty() || newPv.isEmpty()) {
            showToast("请输入密码", Toast.LENGTH_SHORT);
            return false;
        }

        if (!newP.equals(newPv)) {
            showToast("输入的密码不一样，请重新输入", Toast.LENGTH_SHORT);
            return false;
        }

        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,15}$";
        if (!newP.matches(regex)) {
            showToast("密码长度为6-15字符，不能为纯数字或纯字母", Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }
}
