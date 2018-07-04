package com.shrinktool.game;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.shrinktool.R;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.data.Lottery;
import com.shrinktool.data.Method;
import com.shrinktool.data.MethodList;
import com.shrinktool.view.TableMenu;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 用于显示弹出菜单，选择玩法
 * Created by Alashi on 2016/2/17.
 */
public class MenuController {
    private static final String TAG = MenuController.class.getSimpleName();

    @Bind(R.id.tableMenu) TableMenu tableMenu;

    private PopupWindow popupWindow;
    private Activity activity;

    private TableMenu.OnClickMethodListener onClickMethodListener;
    private Method currentMethod;
    private ArrayList<MethodList> methodList;
    private ChooserModel chooserModel;
    private int lotteryId;
    private boolean dataChanged;

    public MenuController(FragmentActivity activity, Lottery lottery) {
        this.activity = activity;
        this.lotteryId = lottery.getLotteryId();
    }

    private ChooserModel getChooserModel() {
        if (chooserModel == null) {
            chooserModel = ChooserModel.get(activity, "model_history_"
                    + GoldenAsiaApp.getUserCentre().getUserID() +"_"+ lotteryId);
        }
        return chooserModel;
    }

    public void setCurrentMethod(Method currentMethod) {
        this.currentMethod = currentMethod;
        if (tableMenu != null) {
            tableMenu.setCurrentMethod(currentMethod);
        }
    }

    public void setMethodList(ArrayList<MethodList> methodList) {
        this.methodList = methodList;
        dataChanged = true;
    }

    public boolean isShowing() {
        return popupWindow != null && popupWindow.isShowing();
    }

    public void show(View anchor) {
        if (methodList == null || methodList.size() == 0) {
            return;
        }
        if (popupWindow == null) {
            View topView = LayoutInflater.from(activity).inflate(R.layout.game_menu_layout, null);
            tableMenu = (TableMenu) topView.findViewById(R.id.tableMenu);
            ButterKnife.bind(this, topView);
            tableMenu.setOnClickMethodListener(onClickMethodListener);

            popupWindow = new PopupWindow(activity);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(topView);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            popupWindow.setAnimationStyle(R.style.pulldown_in_out);
        }

        if (dataChanged) {
            dataChanged = false;
            getChooserModel().setMethodList(methodList);
            tableMenu.setMethodList(methodList);
            tableMenu.setCurrentMethod(currentMethod);
        }

        popupWindow.showAsDropDown(anchor);
    }

    public void addPreference(Method method) {
        getChooserModel().addChoosedMethod(method);
    }

    public void hide() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public void setOnClickMethodListener(TableMenu.OnClickMethodListener onClickMethodListener) {
        this.onClickMethodListener = onClickMethodListener;
        if (tableMenu != null) {
            tableMenu.setOnClickMethodListener(onClickMethodListener);
        }
    }
}
