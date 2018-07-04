package com.shrinktool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.FragmentLauncher;
import com.shrinktool.component.TabPageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 两页Table的页面
 * Created by Alashi on 2016/3/17.
 */
public class ResultTableFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    private List<Fragment> fragments = new ArrayList<>();

    public static void launcher(BaseFragment fragment, int selectPage) {
        Bundle bundle = new Bundle();
        bundle.putInt("selectPage", selectPage);
        FragmentLauncher.launch(fragment.getActivity(), ResultTableFragment.class, bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater, container, "我的方案", R.layout.result_table);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();

        radioGroup.setOnCheckedChangeListener(this);
        TabPageAdapter tabPageAdapter = new TabPageAdapter(getActivity().getSupportFragmentManager(), fragments);
        viewPager.setAdapter(tabPageAdapter);
        viewPager.setOnPageChangeListener(this);

        int selectPage = getArguments().getInt("selectPage");
        radioGroup.check(radioGroup.getChildAt(selectPage).getId());
        selectPage(selectPage);
    }

    protected void initData() {
        addFragment(0);
        addFragment(1);
        addFragment(2);
        addFragment(3);
    }

    private void addFragment(int type) {
        ResultRecordListFragment fragment = new ResultRecordListFragment();
        fragment.setType(type);
        fragments.add(fragment);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButton1:
                selectPage(0);
                break;
            case R.id.radioButton2:
                selectPage(1);
                break;
            case R.id.radioButton3:
                selectPage(2);
                break;
            case R.id.radioButton4:
                selectPage(3);
                break;
        }
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

    private void selectPage(int position) {
        radioGroup.check(radioGroup.getChildAt(position).getId());
        viewPager.setCurrentItem(position, true);
    }
}
