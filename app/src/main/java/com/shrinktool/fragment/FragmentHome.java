package com.shrinktool.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.GameActivity;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.component.BannerHelper;
import com.shrinktool.component.CycleViewPager;
import com.shrinktool.component.HomeLotteryHolder;
import com.shrinktool.data.Lottery;
import com.shrinktool.data.LotteryListCommand;
import com.shrinktool.game.PromptManager;
import com.shrinktool.material.ConstantInformation;
import com.shrinktool.material.VersionChecker;
import com.shrinktool.user.UserCentre;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created on 2016/01/04.
 *
 * @author ACE
 * @功能描述: 首页
 */

public class FragmentHome extends BaseFragment {
    private static final String TAG = FragmentHome.class.getSimpleName();

    private static final int LOTTERY_TRACE_ID = 1;

    private UserCentre userCentre;
    private TextView loginLayout;

    private HomeLotteryHolder lotteryHolder;

    @Bind(R.id.homeHot1) View hot1View;
    @Bind(R.id.homeHot1Name) TextView hot1Name;
    @Bind(R.id.homeHot1Icon) ImageView hot1Icon;
    @Bind(R.id.homeHot2) View hot2View;
    @Bind(R.id.homeHot2Name) TextView hot2Name;
    @Bind(R.id.homeHot2Icon) ImageView hot2Icon;
    @Bind(R.id.lotteryHolder) LinearLayout holderView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflateView(inflater, container, false, "彩种大厅", R.layout.fragment_home);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new VersionChecker(this).startCheck(false);
        userCentre = GoldenAsiaApp.getUserCentre();
        CycleViewPager cycleViewPager = (CycleViewPager) getActivity().getFragmentManager().findFragmentById(R.id
                .fragment_cycle_viewpager_content);
        new BannerHelper(getActivity(), cycleViewPager, 1, notice -> {
            if (TextUtils.isEmpty(notice.getLink())) {
                //NoticeDetailsFragment.launch(FragmentHome.this, false, notices.get(position));
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(notice.getLink()));
                startActivity(browserIntent);
            }
        });

        lotteryHolder = new HomeLotteryHolder(this, holderView);

        loadLotteryListOnline();
    }

    private void loadLotteryListOnline() {
        LotteryListCommand lotteryListCommand = new LotteryListCommand();
        lotteryListCommand.setLotteryID(0);
        TypeToken typeToken = new TypeToken<RestResponse<ArrayList<Lottery>>>() {
        };
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), lotteryListCommand, typeToken, restCallback, LOTTERY_TRACE_ID, this);
        RestResponse restResponse = restRequest.getCache();
        if (restResponse != null && restResponse.getData() instanceof ArrayList) {
            updateLotteryUI((ArrayList<Lottery>) restResponse.getData());
        }
        restRequest.execute();
    }

    private void loadDataFromFile() {
        String json = loadAssets("json/lotteryList.json");
        if (json == null) {
            loadLotteryListOnline();
            return;
        }
        TypeToken typeToken = new TypeToken<ArrayList<Lottery>>() {};
        /*lotteryList = GsonHelper.fromJson(json, typeToken.getType());
        lotteryIco.notifyDataSetChanged();*/
    }

    private void updateLotteryUI(ArrayList<Lottery> lotteries){
        userCentre.setLotteryList(lotteries);

        Lottery hot1 = lotteries.remove(0);
        hot1Name.setText(hot1.getCname());
        hot1Icon.setImageResource(ConstantInformation.getLotteryLogo(hot1.getLotteryId()));
        hot1View.setOnClickListener(v -> apply(hot1));

        Lottery hot2 = lotteries.remove(0);
        hot2Name.setText(hot2.getCname());
        hot2Icon.setImageResource(ConstantInformation.getLotteryLogo(hot2.getLotteryId()));
        hot2View.setOnClickListener(v -> apply(hot2));

        lotteryHolder.setLottery(lotteries);
    }

    private void apply(Lottery lottery){
        if (lottery.isAvailable()) {
            //GameFragment.launch(FragmentHome.this, lottery);
            GameActivity.launch(getActivity(), lottery.getLotteryId(), 0);
        } else {
            PromptManager.showCustomDialog(getContext(), lottery.getYearlyStartClosed());
        }
    }

    private RestCallback restCallback = new RestCallback<ArrayList<?>>() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            if (LOTTERY_TRACE_ID == request.getId()){
                updateLotteryUI((ArrayList<Lottery>) response.getData());
            }
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
}
