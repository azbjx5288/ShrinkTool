package com.shrinktool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.shrinktool.app.BaseFragment;
import com.shrinktool.game.GameConfig;

/**
 * 玩法tab的显示网页内容
 * Created by Alashi on 2016/12/7.
 */

public class GameExplainFragment extends BaseFragment{
    private WebView webView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        webView = new WebView(getContext());
        return webView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int id = getArguments().getInt("id", 0);
        String assetSubPath = GameConfig.getLotteryPlayingHelp(id);
        webView.loadUrl("file:///android_asset/" + assetSubPath);//web/base.html
    }

    @Override
    public void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onDestroyView() {
        webView.destroy();
        super.onDestroyView();
    }
}
