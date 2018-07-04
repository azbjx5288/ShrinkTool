package com.shrinktool.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.shrinktool.BuildConfig;
import com.shrinktool.R;
import com.shrinktool.component.TitleBarHelper;
import com.shrinktool.component.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * 用于显示本地网页
 * Created by Alashi on 2016/8/31.
 */
public class WebViewActivity extends Activity {
    private static final String TAG = "WebViewActivity";

    private WebView webView;
    private TitleBarHelper titleBarHelper;

    public static void start(Activity activity, String assetSubPath) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("assetSubPath", assetSubPath);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_bar_fragment);
        Utils.statusColor(this);
        RelativeLayout topLayout = (RelativeLayout) findViewById(R.id.titleBarLayout);
        titleBarHelper = new TitleBarHelper(this, topLayout, true);
        titleBarHelper.setTitle("");
        LayoutInflater.from(this).inflate(R.layout.activity_webview,
                (ViewGroup) topLayout.findViewById(R.id.title_bar_fragment_content), true);

        webView = (WebView) findViewById(R.id.webView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                titleBarHelper.setTitle(title);
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                titleBarHelper.setTitle(view.getTitle());
            }
        });
        webView.loadUrl("file:///android_asset/" + getIntent().getStringExtra("assetSubPath"));//web/base.html
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }
}
