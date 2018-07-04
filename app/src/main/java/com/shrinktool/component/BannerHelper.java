package com.shrinktool.component;

import android.app.Activity;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.BannerListCommand;
import com.shrinktool.data.Notice;
import com.shrinktool.user.UserCentre;

import java.util.ArrayList;
import java.util.List;

/**
 * Banner相关操作
 * Created by Alashi on 2016/12/7.
 */

public class BannerHelper implements CycleViewPager.ImageCycleViewListener{

    private ArrayList<Notice> notices;
    private CycleViewPager cycleViewPager;
    private Activity activity;
    private OnBannerClickListener bannerClickListener;

    public interface OnBannerClickListener{
        void onBannerClick(Notice notice);
    }

    public BannerHelper(Activity activity, CycleViewPager cycleViewPager, int type, OnBannerClickListener listener) {
        this.activity = activity;
        this.cycleViewPager = cycleViewPager;
        bannerClickListener = listener;
        loadBanner(type);
    }

    private void loadBanner(int type) {
        BannerListCommand command = new BannerListCommand();
        command.setType(type);
        TypeToken typeToken = new TypeToken<RestResponse<ArrayList<Notice>>>() {
        };
        RestRequest restRequest = RestRequestManager.createRequest(activity, command, typeToken, restCallback,
                0, this);
        RestResponse restResponse = restRequest.getCache();
        if (restResponse != null && restResponse.getData() instanceof ArrayList) {
            updateBanner((ArrayList<Notice>) restResponse.getData());
        }
        restRequest.execute();
    }

    private RestCallback restCallback = new RestCallback<ArrayList<?>>() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            updateBanner((ArrayList<Notice>) response.getData());
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {

        }
    };

    @Override
    public void onImageClick(int position, View imageView) {
        bannerClickListener.onBannerClick(notices.get(position));
    }

    private void updateBanner(ArrayList<Notice> notices) {
        this.notices = notices;
        if (notices == null) {
            return;
        }
        int size = notices.size();
        if (size == 0) {
            cycleViewPager.setData(new ArrayList<>(), this);
            cycleViewPager.setWheel(false);
            return;
        }

        UserCentre userCentre = GoldenAsiaApp.getUserCentre();
        List<View> views = new ArrayList<>();
        if (size > 1) {
            // 将最后一个view添加进来
            views.add(ViewFactory.getImageView(activity, userCentre.getUrl(notices.get(notices.size() - 1)
                    .getImgPath()), notices.get(notices.size() - 1).getTitle()));
            for (int i = 0; i < size; i++) {
                views.add(ViewFactory.getImageView(activity, userCentre.getUrl(notices.get(i).getImgPath()),
                        notices.get(i).getTitle()));
            }
            // 将第一个view添加进来
            views.add(ViewFactory.getImageView(activity, userCentre.getUrl(notices.get(0).getImgPath()),
                    notices.get(0).getTitle()));
        } else {
            views.add(ViewFactory.getImageView(activity, userCentre.getUrl(notices.get(0).getImgPath()),
                    notices.get(0).getTitle()));
        }

        cycleViewPager.setCycle(size > 1);
        cycleViewPager.setData(views, this);
        cycleViewPager.setWheel(size > 1);
    }
}
