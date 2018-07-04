package com.shrinktool.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shrinktool.R;
import com.shrinktool.component.TabPageAdapter;
import com.shrinktool.component.Utils;
import com.shrinktool.fragment.FindFragment;
import com.shrinktool.fragment.FragmentHome;
import com.shrinktool.fragment.FragmentUser;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/01/04.
 *
 * @author ACE
 */

public class ContainerActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {
    private static final String TAG = ContainerActivity.class.getSimpleName();

    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private int[] unselectedIcon;
    private int[] selectedIcon;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        initMenu();
        initView();
        selectPage(0); // 默认选中首页

        Utils.statusColor(this);
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

    protected void initMenu() {
        unselectedIcon = new int[]{R.drawable.menu_icon_xuanhao,
                R.drawable.menu_icon_faxian,
                R.drawable.menu_icon_wo};
        selectedIcon = new int[]{R.drawable.menu_icon_xuanhao_co,
                R.drawable.menu_icon_faxian_co,
                R.drawable.menu_icon_wo_co};

        BaseFragment homeFragment = new FragmentHome();
        Fragment tab1 = FragmentDelayer.newInstance(R.drawable.ic_tab_classify,
                FindFragment.class.getName(), null);
        Fragment tab2 = FragmentDelayer.newInstance(R.drawable.ic_tab_me,
                FragmentUser.class.getName(), null);

        fragments.add(homeFragment);
        fragments.add(tab1);
        fragments.add(tab2);
    }

    private void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mRadioGroup.setOnCheckedChangeListener(this);
        TabPageAdapter tabPageAdapter = new TabPageAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(tabPageAdapter);
        mViewPager.setOnPageChangeListener(this);
        mRadioGroup.check(mRadioGroup.getChildAt(0).getId());
    }

    /**
     * 选择某页
     *
     * @param position 页面的位置
     */
    private void selectPage(int position) {
        mRadioGroup.check(mRadioGroup.getChildAt(position).getId());
        // 将所有的tab的icon变成灰色的
        for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
            RadioButton child = (RadioButton) mRadioGroup.getChildAt(i);
            setIcon(child, unselectedIcon[i]);
        }
        // 切换页面
        mViewPager.setCurrentItem(position, true);
        // 改变图标
        RadioButton select = (RadioButton) mRadioGroup.getChildAt(position);
        setIcon(select, selectedIcon[position]);
    }

    private void setIcon(RadioButton radioButton, int drawableId){
        Drawable drawable = getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        radioButton.setCompoundDrawables(null, drawable, null, null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {

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
            case R.id.btn_home:
                selectPage(0);
                break;
            case R.id.btn_classify:
                selectPage(1);
                break;
            case R.id.btn_me:
                selectPage(2);
                break;
        }
    }
}
