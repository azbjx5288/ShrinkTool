package com.shrinktool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;

/**
 * 主页Tab页的“更多”
 * Created by Alashi on 2016/5/25.
 */
public class FragmentMore extends BaseFragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater, container, false, "更多", R.layout.fragment_more);
    }
}
