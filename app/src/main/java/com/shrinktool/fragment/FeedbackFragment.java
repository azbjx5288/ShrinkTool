package com.shrinktool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.shrinktool.BuildConfig;
import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.FeedbackCommand;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 意见反馈
 * Created by Alashi on 2017/3/29.
 */

public class FeedbackFragment extends BaseFragment {
    @Bind(R.id.text)
    EditText editText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater, container, "意见反馈", R.layout.fragment_feedback);
    }

    @OnClick({R.id.cancel, R.id.submit})
    public void onClickButton(View view) {
        if (view.getId() == R.id.cancel){
            getActivity().finish();
            return;
        }

        if (TextUtils.isEmpty(editText.getText())) {
            showToast("请输入反馈内容！");
            return;
        }

        submit(editText.getText().toString());
    }

    private void submit(String text) {
        FeedbackCommand command = new FeedbackCommand();
        command.setContent(text);
        command.setVersion(BuildConfig.VERSION_NAME);
        command.setMobileType(android.os.Build.MODEL);

        executeCommand(command, restCallback);
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            showToast("成功提交意见");
            getActivity().finish();
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (state == RestRequest.RUNNING) {
                showProgress("提交中...");
            } else {
                hideProgress();
            }
        }
    };
}
