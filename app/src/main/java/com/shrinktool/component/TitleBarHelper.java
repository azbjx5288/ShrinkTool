package com.shrinktool.component;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.base.net.NetStateHelper;

/**
 * 一般公用标题栏的处理
 * Created by Alashi on 2016/8/16.
 */
public class TitleBarHelper implements NetStateHelper.NetStateListener, View.OnClickListener{
    private static final String TAG = "TitleBarHelper";

    private Activity activity;
    private RelativeLayout topLayout;

    private TextView titleBarTitle;
    private TextView globalNetState;
    protected LinearLayout actionBarMenuLayout;
    protected RelativeLayout titleBarProgressLayout;
    protected TextView titleBarProgressMsg;

    public TitleBarHelper(Activity activity, RelativeLayout topLayout, boolean homeButton) {
        this.topLayout = topLayout;
        this.activity = activity;
        init(homeButton);
    }

    public void setTitle(int resId) {
        titleBarTitle.setText(resId);
    }

    public void setTitle(String title) {
        titleBarTitle.setText(title);
    }

    private void init(boolean homeButton){
        globalNetState = (TextView)topLayout.findViewById(R.id.global_net_state);
        titleBarTitle = (TextView)topLayout.findViewById(android.R.id.title);
        actionBarMenuLayout = (LinearLayout) topLayout.findViewById(R.id.action_bar_menu_layout);
        titleBarProgressLayout = (RelativeLayout) topLayout.findViewById(R.id.titleBarProgressLayout);
        titleBarProgressMsg = (TextView) topLayout.findViewById(R.id.titleBarProgressMsg);
        titleBarProgressLayout.setOnClickListener(view -> view.setVisibility(View.GONE));
        if (homeButton) {
            topLayout.findViewById(android.R.id.home).setOnClickListener(this);
        } else {
            topLayout.findViewById(android.R.id.home).setVisibility(View.GONE);
        }

        NetStateHelper netStateHelper = GoldenAsiaApp.getNetStateHelper();
        globalNetState.setVisibility(netStateHelper.isConnected()? View.GONE : View.VISIBLE);
        netStateHelper.addListener(this);
    }

    @Override
    public void onStateChange(boolean isConnected) {
        if (globalNetState != null) {
            globalNetState.setVisibility(isConnected? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == android.R.id.home) {
            activity.finish();
        }
    }

    public void addMenuItem(View view) {
        LinearLayout menuLayout = getActionBarMenuLayout();
        if (menuLayout == null) {
            return;
        }
        menuLayout.addView(view);
    }

    public LinearLayout getActionBarMenuLayout() {
        if (actionBarMenuLayout == null) {
            actionBarMenuLayout = (LinearLayout) activity.findViewById(R.id.action_bar_menu_layout);
            if (actionBarMenuLayout == null) {
                Log.e(TAG, "addMenuItem: can not add menu, actionBarMenuLayout is null", new Throwable());
                return null;
            }
        }
        return actionBarMenuLayout;
    }

    /** 在ActionBar添加图标菜单 */
    public View addMenuItem(@DrawableRes int resID, View.OnClickListener listener) {
        LinearLayout menuLayout = getActionBarMenuLayout();
        if (menuLayout == null) {
            return null;
        }
        ImageView view = (ImageView) LayoutInflater.from(activity)
                .inflate(R.layout.actionbar_menu_image, menuLayout, false);
        view.setOnClickListener(listener);
        view.setImageResource(resID);
        menuLayout.addView(view);
        return view;
    }

    /** 在ActionBar添加文字菜单 */
    public View addMenuItem(String text, View.OnClickListener listener) {
        LinearLayout menuLayout = getActionBarMenuLayout();
        if (menuLayout == null) {
            return null;
        }

        TextView view = (TextView) LayoutInflater.from(activity)
                .inflate(R.layout.actionbar_menu_text, menuLayout, false);
        view.setText(text);
        view.setOnClickListener(listener);
        menuLayout.addView(view);
        return view;
    }

    public void removeAllMenu() {
        if (actionBarMenuLayout != null) {
            actionBarMenuLayout.removeAllViews();
        }
    }

    public void showProgress(String msg) {
        titleBarProgressMsg.setText(msg);
        titleBarProgressLayout.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        titleBarProgressLayout.setVisibility(View.GONE);
    }

    public void onDestroyView() {
        GoldenAsiaApp.getNetStateHelper().removeListener(this);
    }
}
