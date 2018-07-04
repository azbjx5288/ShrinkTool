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
import android.widget.Toast;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.BindMobileCommand;
import com.shrinktool.data.CaptchaCommand;
import com.shrinktool.data.UserInfo;
import com.shrinktool.user.UserCentre;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 绑定手机
 * Created by Alashi on 2016/7/25.
 */
public class BindPhoneFragment extends BaseFragment {
    private static final String TAG = "BindPhoneFragment";

    private static final int ID_CAPTCHA = 1;
    private static final int ID_BIND = 2;

    @Bind(R.id.cellphoneNumber) EditText cellphoneNumber;
    @Bind(R.id.cellphoneVerificationCode) EditText cellphoneVerificationCode;
    @Bind(R.id.cellphoneNumberClear) View cellphoneNumberClear;
    @Bind(R.id.cellphoneVerificationCodeButton) Button cellphoneVerificationCodeButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater, container, "手机绑定", R.layout.bind_phone);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCellphoneLayout();
    }

    private void initCellphoneLayout(){
        cellphoneNumberClear.setVisibility(View.INVISIBLE);
        cellphoneNumber.setOnFocusChangeListener((view1, b) -> {
            if (cellphoneNumberClear != null) {
                cellphoneNumberClear.setVisibility(b? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    @OnClick({R.id.cellphoneNumberClear, R.id.cellphoneVerificationCodeButton, R.id.cellphoneOk, R.id.agreement })
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.cellphoneNumberClear:
                cellphoneNumber.setText(null);
                cellphoneVerificationCode.setText(null);
                cellphoneNumber.requestFocus();
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
            case R.id.agreement:
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

        BindMobileCommand command = new BindMobileCommand();
        command.setMobile(cellphoneNumber.getText().toString());
        command.setCode(cellphoneVerificationCode.getText().toString());
        executeCommand(command, callback, ID_BIND);
    }

    private void requestVerificationCode() {
        if (!isValidCellphoneNumber()) {
            return;
        }

        cellphoneVerificationCodeButton.setEnabled(false);
        startCountDown();

        CaptchaCommand command = new CaptchaCommand();
        command.setMobile(cellphoneNumber.getText().toString());
        command.setType("BindMobile");
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

    private RestCallback callback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            if (request.getId() == ID_CAPTCHA) {
                showToast("已发送短信验证码");
            } else if (request.getId() == ID_BIND) {
                UserCentre userCentre = GoldenAsiaApp.getUserCentre();
                UserInfo userInfo = userCentre.getUserInfo();
                userInfo.setMobile(cellphoneNumber.getText().toString());
                userCentre.setUserInfo(userInfo);

                showToast("成功绑定手机！");
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            if (request.getId() == ID_CAPTCHA) {
                showToast("短信验证码发送失败");
            }
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {

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
}
