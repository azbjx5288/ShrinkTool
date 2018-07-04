package com.shrinktool.material;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.shrinktool.app.BaseFragment;
import com.shrinktool.base.Preferences;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.Version;
import com.shrinktool.data.VersionCommand;

/**
 * 负责版本检查，弹窗提醒，强制更新
 * Created by Alashi on 2016/3/1.
 */
public class VersionChecker implements RestCallback<Version> {
    private static final String TAG = "VersionChecker";

    private static final String SHARED_KEY = "last-time-check-version";
    /** 最小提醒间隔 */
    private static final long SPACE_MIN_TIME = 1000L * 60 * 30;//30分钟

    private BaseFragment fragment;
    //用户主动触发版本检查时，需要提醒进度
    private boolean isUserAction;

    public VersionChecker(BaseFragment fragment) {
        this.fragment = fragment;
    }

    public void startCheck(){
        startCheck(false);
    }

    public void startCheck(boolean isUserAction) {
        this.isUserAction = isUserAction;
        RestRequestManager.executeCommand(fragment.getActivity(), new VersionCommand(), this, 0, fragment);
    }

    private void handleUpgrade(final Version version) {
        //Uri uri = Uri.parse("http://"+version.getSiteMainDomain()+"/download/" + version.getFileName());
        Uri uri = Uri.parse(version.getFile());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        fragment.getActivity().startActivity(intent);
        if (version.isForce()) {
            //延时调用Activity.finish()，否则上面startActivity弹出的选择对话框后出现的很慢
            new Handler().postDelayed(() -> fragment.getActivity().finish(), 500);
        }
    }

    private void showDialog(final Version version){
        if (fragment.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity())
                .setTitle("新版本")
                .setMessage(Html.fromHtml(version.getUpdateDescribe()))
                .setPositiveButton("马上升级", (dialog, which) -> handleUpgrade(version));

        if (!version.isForce()) {
            builder.setNegativeButton("稍后升级", (dialog, which) -> dialog.dismiss());
        }

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.i(TAG, "showDialog: ");
    }

    /** 获取版本号(内部识别号) */
    private static int getVersionCode(Context context) {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean onRestComplete(RestRequest request, RestResponse<Version> response) {
        Log.i(TAG, "onRestComplete: isAdded = " + fragment.isAdded());
        if (!fragment.isAdded()) {
            return true;
        }

        Version version = response.getData();
        if (version != null && version.getVersionNumber() > getVersionCode(request.getContext())) {
            long lastTime = Preferences.getLong(fragment.getActivity(), SHARED_KEY, 0);
            Log.i(TAG, "onRestComplete: isForce->" + version.isForce() + ", time->"
                    + (Math.abs(System.currentTimeMillis() - lastTime) > SPACE_MIN_TIME));
            if (isUserAction || version.isForce() || Math.abs(System.currentTimeMillis() - lastTime) > SPACE_MIN_TIME) {
                showDialog(version);
                Preferences.saveLong(fragment.getActivity(), SHARED_KEY, System.currentTimeMillis());
            }
        } else if (isUserAction) {
            fragment.showToast("已经是最新版本", Toast.LENGTH_SHORT);
        }
        return true;
    }

    @Override
    public boolean onRestError(RestRequest request, int errCode, String errDesc) {
        if (isUserAction && fragment.isAdded()) {
            fragment.showToast("版本检查失败，请稍后重试", Toast.LENGTH_SHORT);
        }
        return true;
    }

    @Override
    public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
        if (isUserAction) {
            if (state == RestRequest.RUNNING) {
                fragment.showProgress("正在检查版本...");
            } else {
                fragment.hideProgress();
            }
        }
    }
}
