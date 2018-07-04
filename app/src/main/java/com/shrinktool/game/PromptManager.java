package com.shrinktool.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.component.CustomDialog;
import com.shrinktool.component.CustomDialogLayout;
import com.shrinktool.data.LogoutCommand;
import com.shrinktool.app.GoldenLogin;
import com.shrinktool.material.RefiningCart;


/**
 * 提示信息的管理
 *
 * @author Ace
 */

public class PromptManager {

    private Activity activity;
    private static final int ID_LOGOUT = 1;

    public PromptManager(Activity activity) {
        this.activity = activity;

    }

    public static void showCustomDialog(CustomDialog.Builder builder) {
        builder.create().show();
    }

    public static void showCustomDialog(Context context, String msg) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle("温馨提示");
        builder.setLayoutSet(CustomDialogLayout.SINGLE);
        builder.setPositiveButton("知道了", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    public static CustomDialog showCustomDialog(final Activity atv, String title, String msg, String butText, final int status) {
        final PromptManager promptManager=new PromptManager(atv);
        CustomDialog.Builder builder = new CustomDialog.Builder(atv);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setLayoutSet(CustomDialogLayout.SINGLE);
        builder.setPositiveButton(butText, (dialog, which) -> {
            if (status == 7006) {
                RestRequestManager.executeCommand(atv,
                        new LogoutCommand(), promptManager.restCallback, ID_LOGOUT, atv);
                RefiningCart.getInstance().clear();
            }
            dialog.dismiss();
        });
        return builder.create();
    }

    private void handleExit() {
        GoldenAsiaApp.getUserCentre().logout();
        activity.finish();
        //FragmentLauncher.launch(activity, GoldenLogin.class);
        activity.startActivity(new Intent(activity, GoldenLogin.class));
        RestRequestManager.cancelAll();
    }


    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            if (request.getId() == ID_LOGOUT) {
                handleExit();
            }
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            if (request.getId() == ID_LOGOUT) {
                handleExit();
                return true;
            } else if (errCode == 7003) {
                Toast.makeText(activity, "正在更新服务器请稍等", Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
        }
    };
}
