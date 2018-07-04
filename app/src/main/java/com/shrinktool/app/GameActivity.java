package com.shrinktool.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.base.Preferences;
import com.shrinktool.component.TabPageAdapter;
import com.shrinktool.component.Utils;
import com.shrinktool.data.Lottery;
import com.shrinktool.fragment.ArticleListFragment;
import com.shrinktool.fragment.GameExplainFragment;
import com.shrinktool.fragment.GameFragment;
import com.shrinktool.fragment.OpenCodeHistoryFragment;
import com.shrinktool.user.UserCentre;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选号、开奖、玩法的TAB页
 * Created by Alashi on 2016/12/7.
 */
public class GameActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {
    private static final String TAG = "GameActivity";

    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.lotteryName)
    TextView lotteryName;

    private List<Fragment> fragments = new ArrayList<>();
    private Lottery lottery;

    public static void launch(Activity activity, int lotteryId, int tabIndex) {
        Intent intent = new Intent(activity, GameActivity.class);
        intent.putExtra("lotteryId", lotteryId);
        intent.putExtra("tabIndex", tabIndex);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_tab);
        ButterKnife.bind(this);
        int tabIndex = getIntent().getIntExtra("tabIndex", 0);
        UserCentre userCentre = GoldenAsiaApp.getUserCentre();
        lottery = userCentre.getLottery(getIntent().getIntExtra("lotteryId", 0));
        lotteryName.setText(lottery.getCname());

        initDate(tabIndex);
        initView();
        selectPage(tabIndex); // 默认选中首页

        Utils.statusColor(this);
    }

    @OnClick({android.R.id.home, R.id.trend})
    public void onClickButton(View view) {
        switch (view.getId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.trend:
                TrendActivity.launch(this, lottery.getLotteryId());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Preferences.saveInt(this, "GameActivity_last_lottery", lottery.getLotteryId());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    private void initDate(int tabIndex) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("lottery", lottery);
        bundle.putInt("id", lottery.getLotteryId());

        BaseFragment tab0 = new GameFragment();
        tab0.setArguments(bundle);
        BaseFragment tab1;
        if (tabIndex == 1) {
            tab1 = new OpenCodeHistoryFragment();
            tab1.setArguments(bundle);
        } else {
            tab1 = FragmentDelayer.newInstance(R.drawable.cz_bj11x5,
                    OpenCodeHistoryFragment.class.getName(), bundle);
        }
        BaseFragment tab2  = FragmentDelayer.newInstance(R.drawable.cz_cqssc,
                ArticleListFragment.class.getName(), bundle);
        BaseFragment tab3 = FragmentDelayer.newInstance(R.drawable.cz_fc3d,
                GameExplainFragment.class.getName(), bundle);

        fragments.add(tab0);
        fragments.add(tab1);
        fragments.add(tab2);
        fragments.add(tab3);
    }

    private void initView() {
        radioGroup.setOnCheckedChangeListener(this);
        TabPageAdapter tabPageAdapter = new TabPageAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(tabPageAdapter);
        viewPager.setOnPageChangeListener(this);
        radioGroup.check(radioGroup.getChildAt(0).getId());
    }

    private void selectPage(int position) {
        radioGroup.check(radioGroup.getChildAt(position).getId());
        // 切换页面
        viewPager.setCurrentItem(position, true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.gameSelection:
                selectPage(0);
                break;
            case R.id.gameDraw:
                selectPage(1);
                break;
            case R.id.gameArticle:
                selectPage(2);
                break;
            case R.id.gameDescribe:
                selectPage(3);
                break;
        }
    }
}
