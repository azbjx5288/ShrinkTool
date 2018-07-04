package com.shrinktool.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shrinktool.BuildConfig;
import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.app.GoldenLogin;
import com.shrinktool.base.net.GsonHelper;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.component.PicturePicker;
import com.shrinktool.component.ShareToDialog;
import com.shrinktool.component.Utils;
import com.shrinktool.data.LogoutCommand;
import com.shrinktool.data.MultipartEntity;
import com.shrinktool.data.MultipartRequest;
import com.shrinktool.data.UserInfo;
import com.shrinktool.material.VersionChecker;
import com.shrinktool.user.UserCentre;
import com.shrinktool.view.CircleImageView;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class FragmentUser extends BaseFragment {
    private static final String TAG = TrendTabFragment.class.getSimpleName();
    private static final int REQUEST_LOGIN_CODE = 1;
    private static final int REQUEST_CODE_PORTRAIT = 2;

    private static final int ID_LOGOUT = 1;

    @Bind(R.id.avatar) CircleImageView avatar;
    @Bind(R.id.user_name) TextView userName;
    @Bind(R.id.login_btn) Button loginButton;
    //@Bind(R.id.userInfoLayout) LinearLayout userInfoLayout;
    @Bind(R.id.loginLayout) LinearLayout loginLayout;
    @Bind(R.id.userGridview) GridView gridView;

    private UserCentre userCentre;
    private ShareToDialog shareToDialog;

    private ArrayList<DataItem> dataItems;
    private String avatarPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflateView(inflater, container, false, "账户中心", R.layout.fragment_user);
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userCentre = GoldenAsiaApp.getUserCentre();
        updateUserInfo();
        initData();
        gridView.setAdapter(new ImageAdapter());
    }

    @OnItemClick(R.id.userGridview)
    public void onItemClick(int position){
        DataItem item = dataItems.get(position);
        if (item.id == 0) {
            return;
        }
        if (item.id > 0 && !userCentre.isLogin()) {
            startActivityForResult(new Intent(getContext(), GoldenLogin.class), REQUEST_LOGIN_CODE);
            //FragmentLauncher.launchForResult(this, GoldenLogin.class, null, REQUEST_LOGIN_CODE);
            return;
        }
        switch (item.id) {
            case 1://过滤记录
                launchFragment(ResultRecordListFragment.class);
                break;
            case 2://修改密码
                launchFragment(LoginPasswordSetting.class);
                break;
            case 3://开奖号码通知
                launchFragment(PushSetting.class);
                break;
            case 4://升级
                new VersionChecker(this).startCheck(true);
                break;
            case 5://手机绑定
                if (TextUtils.isEmpty(userCentre.getUserInfo().getMobile())) {
                    launchFragment(BindPhoneFragment.class);
                } else {
                    showToast("您已经绑定手机");
                }
                break;
            case 6://我的收藏
                ArticleListFragment.launch(this, 1);
                break;
            case 7://浏览历史
                ArticleListFragment.launch(this, 2);
                break;
            case 8://意见反馈
                launchFragment(FeedbackFragment.class);
                break;

            case -1://帮助
                launchFragment(FragmentHelp.class);
                break;
            case -2://推荐给朋友
                if (shareToDialog == null) {
                    shareToDialog = new ShareToDialog(getActivity());
                }
                shareToDialog.show();
                break;
            case -3://退出
                clickLogout();
                break;

            default:
                Log.w(TAG, "onItemClick: 未添加的操作：" + item.id);
        }
    }

    private void initData(){
        int textColor = Color.GRAY;
        dataItems = new ArrayList<>();
        //DataItem.id大于0的是要登录才能操作的
        //dataItems.add(new DataItem(1, R.drawable.my_icon_gljl, "过滤记录", textColor));
        dataItems.add(new DataItem(6, R.drawable.my_icon_wdsc, "我的收藏", textColor));
        dataItems.add(new DataItem(7, R.drawable.my_icon_lljl, "浏览历史", textColor));
        dataItems.add(new DataItem(2, R.drawable.my_icon_xgmm, "修改密码", textColor));
        dataItems.add(new DataItem(5, R.drawable.my_icon_phone, "手机绑定", textColor));
        dataItems.add(new DataItem(-2, R.drawable.my_icon_yqhy, "邀请好友", textColor));
        dataItems.add(new DataItem(4, R.drawable.my_icon_jcgx, "检测更新", textColor));
        dataItems.add(new DataItem(-1, R.drawable.my_icon_bzzx, "帮助中心", textColor));
        dataItems.add(new DataItem(8, R.drawable.my_icon_yjfk, "意见反馈", textColor));
        dataItems.add(new DataItem(-3, R.drawable.my_icon_out, "退出", Color.parseColor("#999999")));
        while (0 != dataItems.size() % 3) {
            dataItems.add(new DataItem(0, 0, null, textColor));
        }
    }

    private class DataItem{
        int id;
        int iconId;
        String text;
        int textColor;

        public DataItem(int id, int iconId, String text, int textColor) {
            this.id = id;
            this.iconId = iconId;
            this.text = text;
            this.textColor = textColor;
        }
    }

    private void clickLogout() {
        if (userCentre.isLogin()) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("退出当前账号")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("退出", (dialog, which) ->
                            executeCommand(new LogoutCommand(), restCallback, ID_LOGOUT))
                    .create().show();
        } else {
            getActivity().finish();
        }
    }

    private void showSetAvatar(){
        File file = new File(getActivity().getExternalCacheDir(),
                "portrait_tmp_" + System.currentTimeMillis() + "" + ".jpg");
        avatarPath = file.toString();
        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("修改头像");
        dialog.setContentView(R.layout.set_avatar_dialog);
        dialog.findViewById(R.id.camera).setOnClickListener(v -> {
            PicturePicker.action(FragmentUser.this, REQUEST_CODE_PORTRAIT, PicturePicker.TYPE.TYPE_CAMERA,
                    400, 400, avatarPath);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.album).setOnClickListener(v -> {
            PicturePicker.action(FragmentUser.this, REQUEST_CODE_PORTRAIT, PicturePicker.TYPE.TYPE_ALBUM,
                    400, 400, avatarPath);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateUserInfo() {
        if (userName == null || userCentre == null) {
            return;
        }

        if (userCentre.isLogin()) {
            userName.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
            UserInfo info = userCentre.getUserInfo();
            if (TextUtils.isEmpty(info.getNickName())) {
                userName.setText(info.getUserName());
            } else {
                userName.setText(info.getNickName());
            }
            if (!TextUtils.isEmpty(info.getHeadimg())){
                ImageLoader.getInstance().displayImage(info.getHeadimg(), avatar);
            }
        } else {
            userName.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            userName.setText("");
        }
    }

    @OnClick({R.id.avatar, R.id.login_btn, R.id.my_sub_dkj, R.id.my_sub_wdfa, R.id.my_sub_wzj, R.id.my_sub_zj})
    public void onClickButton(View view) {
        if (!userCentre.isLogin()) {
            startActivityForResult(new Intent(getContext(), GoldenLogin.class), REQUEST_LOGIN_CODE);
            return;
        }
        switch (view.getId()) {
            /*case R.id.login_btn:
                startActivityForResult(new Intent(getContext(), GoldenLogin.class), REQUEST_LOGIN_CODE);
                break;*/

            case R.id.my_sub_wdfa:
                ResultTableFragment.launcher(this, 0);
                break;
            case R.id.my_sub_dkj:
                ResultTableFragment.launcher(this, 1);
                break;
            case R.id.my_sub_zj:
                ResultTableFragment.launcher(this, 2);
                break;
            case R.id.my_sub_wzj:
                ResultTableFragment.launcher(this, 3);
                break;
            case R.id.avatar:
                showSetAvatar();
                break;
            default:
                Log.e(TAG, "onClickButton: 未知ID:" + view.getId());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN_CODE) {
            updateUserInfo();
        } else if (requestCode == REQUEST_CODE_PORTRAIT) {
            submitAvatar();
        }
    }

    @Keep
    private class AvatarResponse {
        private int errno;
        private String errstr;
        private String data;
    }

    private void submitAvatar() {
        if (null == avatarPath || !new File(avatarPath).exists()) {
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(getContext());
        MultipartRequest multipartRequest = new MultipartRequest(
                BuildConfig.BASE_URL + "/index.jsp?c=user&a=uploadHeadimg",
                response -> {
                    //调用成功
                    Log.d(TAG, "onResponse: " + response);
                    AvatarResponse avatarResponse = GsonHelper.fromJson(response, AvatarResponse.class);
                    if (avatarResponse.errno == 0) {
                        //avatarResponse.data;
                        Log.d(TAG, "onResponse: url = " + avatarResponse.data);
                        ImageLoader.getInstance().displayImage(avatarResponse.data, avatar);
                        UserInfo userInfo = userCentre.getUserInfo();
                        userInfo.setHeadimg(avatarResponse.data);
                        userCentre.setUserInfo(userInfo);
                    } else {
                        showToast(avatarResponse.errstr);
                    }
                },
                error -> {
                    //调用失败
                    Log.d(TAG, "onErrorResponse: " + error);
                    showToast("修改图像失败");
                });
        multipartRequest.addHeader("User-Agent", "Android App");
        multipartRequest.addHeader("Cookie", GoldenAsiaApp.getUserCentre().getSession());

        // 通过MultipartEntity来设置参数
        MultipartEntity multi = multipartRequest.getMultiPartEntity();
        byte[] image = Utils.getBitmap(avatarPath, 400, 400);
        multi.addBinaryPart("headimg", image, "image/png", null, "??.png");

        queue.add(multipartRequest);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        updateUserInfo();
    }

    private void handleExit() {
        userCentre.logout();
        //getActivity().finish();
        //launchFragment(GoldenLogin.class);
        RestRequestManager.cancelAll();
        startActivityForResult(new Intent(getContext(), GoldenLogin.class), REQUEST_LOGIN_CODE);
        //FragmentLauncher.launchForResult(this, GoldenLogin.class, null, REQUEST_LOGIN_CODE);
    }

    private RestCallback restCallback = new RestCallback<UserInfo>() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse<UserInfo> response) {
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
            }
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (request.getId() == ID_LOGOUT) {
                if (state == RestRequest.RUNNING) {
                    showProgress("退出中...");
                } else {
                    hideProgress();
                }
            }
        }
    };

    public class ImageAdapter extends BaseAdapter {

        private class GirdHolder {
            ImageView logo;
            TextView name;
        }

        public int getCount() {
            return dataItems != null ? dataItems.size() : 0;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            GirdHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.user_fragment_item, parent, false);
                holder = new GirdHolder();
                holder.logo = (ImageView) convertView.findViewById(R.id.icon);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (GirdHolder) convertView.getTag();
            }

            DataItem dataItem = dataItems.get(position);
            if (dataItem.id == 0) {
                holder.logo.setImageBitmap(null);
                holder.name.setText(null);
            } else {
                holder.logo.setImageResource(dataItem.iconId);
                holder.name.setText(dataItem.text);
                holder.name.setTextColor(dataItem.textColor);
            }
            return convertView;
        }
    }
}