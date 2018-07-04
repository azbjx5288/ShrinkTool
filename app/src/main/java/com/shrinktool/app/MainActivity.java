package com.shrinktool.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shrinktool.BuildConfig;
import com.shrinktool.R;
import com.shrinktool.base.Preferences;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.fragment.ArticleListFragment;
import com.shrinktool.fragment.ResultTableFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 启动Activity，显示启动界面，引导界面
 * Created by Alashi on 2016/1/12.
 */
public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1001;
    /** 在cache与BuildConfig.VERSION_CODE版本不一致时，需要重新登录 */
    private static Boolean isSameVersion;

    private List<View> views;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (false) {
            findViewById(R.id.launcher).setOnClickListener(v ->
                    startActivityForResult(new Intent(this, ContainerActivity.class), REQUEST_CODE));
            FragmentLauncher.launch(this, ArticleListFragment.class);
            //FragmentLauncher.launch(this, FindFragment.class);
            return;
        }
        if (!isInSameVersion()) {
            GoldenAsiaApp.getUserCentre().logout();
            RestRequestManager.cancelAll();

            Preferences.saveInt(this, "app-version-code", BuildConfig.VERSION_CODE);
            isSameVersion = true;
        }

        if (false) {
            findViewById(R.id.launcher).setOnClickListener(v ->
                    startActivityForResult(new Intent(this, ContainerActivity.class), REQUEST_CODE));
            Bundle bundle = new Bundle();
            bundle.putInt("selectPage", 0);
            FragmentLauncher.launch(this, ResultTableFragment.class, bundle);
            return;
        }

        if (BuildConfig.DEBUG) {
            startActivityForResult(new Intent(this, ContainerActivity.class), REQUEST_CODE);
        } else {
            new Handler(getMainLooper()).postDelayed(runnable, 500);
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
        MobclickAgent.onPause(this);
    }

    private Runnable runnable = () -> {
        if (isFinishing()) {
            return;
        }

        if (Preferences.getBoolean(this, "app-first-time-launcher", true)) {
            showViewPager();
        } else {
            startActivityForResult(new Intent(this, ContainerActivity.class), REQUEST_CODE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

    public boolean isInSameVersion() {
        if (isSameVersion == null) {
            isSameVersion = Preferences.getInt(this, "app-version-code", 0) == BuildConfig.VERSION_CODE;
        }
        return isSameVersion;
    }

    private void showViewPager() {
        findViewById(R.id.launcher).setVisibility(View.GONE);
        ViewGroup viewGroup = (ViewGroup) ((ViewStub)findViewById(R.id.viewStub)).inflate();
        ViewPager viewPager = (ViewPager) viewGroup.findViewById(R.id.viewPager);
        View nextButton = viewGroup.findViewById(R.id.next);
        LinearLayout viewpagerIndex = (LinearLayout) viewGroup.findViewById(R.id.viewpagerIndex);
        viewGroup.findViewById(R.id.start).setOnClickListener(view -> {
            Preferences.saveBoolean(this, "app-first-time-launcher", false);
            startActivityForResult(new Intent(this, ContainerActivity.class), REQUEST_CODE);
        });
        nextButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() != views.size() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                Preferences.saveBoolean(this, "app-first-time-launcher", false);
                startActivityForResult(new Intent(this, ContainerActivity.class), REQUEST_CODE);
            }
        });
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<>();
        views.add(inflaterGuide(inflater, R.drawable.yd_img1));
        views.add(inflaterGuide(inflater, R.drawable.yd_img2));
        views.add(inflaterGuide(inflater, R.drawable.yd_img3));

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                for (int i = 0, count = viewpagerIndex.getChildCount(); i < count; i++) {
                    ((ImageView)viewpagerIndex.getChildAt(i)).setImageResource(
                            i == position? R.drawable.yd_point_play : R.drawable.yd_point_no);
                }
                super.onPageSelected(position);
            }
        });
    }

    private View inflaterGuide(LayoutInflater inflater, int id) {
        ImageView view1 = (ImageView) inflater.inflate(R.layout.activity_main_guide_item, null);
        view1.setImageResource(id);
        return view1;
    }

    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }
    };

}
