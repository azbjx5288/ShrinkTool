package com.shrinktool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;

/**
 * 推送设置
 * Created by Alashi on 2016/9/7.
 */
public class PushSetting extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater, container, "开奖号码通知", R.layout.push_setting);
    }
}
