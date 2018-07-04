package com.shrinktool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.ChangePasswordCommand;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 登录密码设置
 * Created by Alashi on 2016/5/2.
 */
public class LoginPasswordSetting extends BaseFragment {

    @Bind(R.id.nowPassword) EditText nowPassword;
    @Bind(R.id.newPassword) EditText newPassword;
    @Bind(R.id.newPasswordVerify) EditText newPasswordVerify;
    @Bind(R.id.nowPasswordClear) View nowPasswordClear;
    @Bind(R.id.newPasswordClear) View newPasswordClear;
    @Bind(R.id.newPasswordVerifyClear) View newPasswordVerifyClear;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater, container, "修改密码", R.layout.login_password);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nowPasswordClear.setVisibility(View.INVISIBLE);
        nowPassword.setOnFocusChangeListener((view1, b) -> {
            if (nowPasswordClear != null) {
                nowPasswordClear.setVisibility(b? View.VISIBLE : View.INVISIBLE);
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

    @OnClick({R.id.submit, R.id.nowPasswordClear, R.id.newPasswordClear, R.id.newPasswordVerifyClear})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.submit:{
                if (!(check())) {
                    return;
                }

                ChangePasswordCommand command = new ChangePasswordCommand();
                command.setOldpassword(nowPassword.getText().toString());
                command.setPassword(newPassword.getText().toString());
                command.setPassword2(newPasswordVerify.getText().toString());

                executeCommand(command, callback);
                break;
            }

            case R.id.nowPasswordClear:
                nowPassword.setText(null);
                nowPassword.requestFocus();
                break;

            case R.id.newPasswordClear:
                newPassword.setText(null);
                newPassword.requestFocus();
                break;

            case R.id.newPasswordVerifyClear:
                newPasswordVerify.setText(null);
                newPasswordVerify.requestFocus();
                break;
        }

    }

    private RestCallback callback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            showToast("登录密码修改成功", Toast.LENGTH_SHORT);
            getActivity().finish();
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            showToast("登录密码修改失败：" + errDesc, Toast.LENGTH_SHORT);
            return true;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (state == RestRequest.RUNNING) {
                showProgress("正在修改登录密码...");
            } else {
                hideProgress();
            }
        }
    };

    private boolean check() {
        String now = nowPassword.getText().toString();
        String newP = newPassword.getText().toString();
        String newPv = newPasswordVerify.getText().toString();

        if (now.isEmpty()) {
            showToast("请输入当前密码", Toast.LENGTH_SHORT);
            return false;
        }
        if (newP.isEmpty() || newPv.isEmpty()) {
            showToast("请输入新密码", Toast.LENGTH_SHORT);
            return false;
        }

        if (!newP.equals(newPv)) {
            showToast("输入的新密码不一样，请重新输入", Toast.LENGTH_SHORT);
            return false;
        }
        if (now.equals(newP)) {
            showToast("当前密码和新密码一样，请重新输入", Toast.LENGTH_SHORT);
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
