package com.shrinktool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 帮助页
 * Created by Alashi on 2016/9/12.
 */
public class FragmentHelp extends BaseFragment implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.radioButton1)
    RadioButton radioButton1;
    @Bind(R.id.radioButton2)
    RadioButton radioButton2;
    private List<View> views = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater, container, "", R.layout.help_table);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("帮助");
        radioButton1.setText("玩法规则");
        radioButton2.setText("使用说明");

        radioGroup.setOnCheckedChangeListener(this);
        initData();
        viewPager.setAdapter(myPageAdapter);
        viewPager.setOnPageChangeListener(this);
        radioGroup.check(radioGroup.getChildAt(0).getId());

        selectPage(0);
    }

    private void initData() {
        ListView listView = new ListView(getContext());
        ArrayList<DataItem> array1 = new ArrayList<>();
        array1.add(new DataItem("双色球玩法规则", "web2/ssq_wf.html"));
        array1.add(new DataItem("福彩3D玩法规则", "web2/fc3D_wf.html"));
        array1.add(new DataItem("排列3玩法规则", "web2/pl3_wf.html"));
        array1.add(new DataItem("天津时时彩玩法规则", "web2/tjssc_wf.html"));
        array1.add(new DataItem("新疆时时彩玩法规则", "web2/sjssc_wf.html"));
        array1.add(new DataItem("重庆时时彩玩法规则", "web2/cqssc_wf.html"));
        array1.add(new DataItem("北京11选5玩法规则", "web2/bj11x5_wf.html"));
        array1.add(new DataItem("广东11选五玩法规则", "web2/gd11x5_wf.html"));
        array1.add(new DataItem("江西11选5玩法规则", "web2/jx11x5_wf.html"));
        array1.add(new DataItem("山东11选5玩法规则", "web2/sd11x5_wf.html"));
        array1.add(new DataItem("上海11选5玩法规则", "web2/sh11x5_wf.html"));
        listView.setAdapter(new MyListAdapter(array1));
        listView.setOnItemClickListener(itemClickListener);
        views.add(listView);

        ListView listView2 = new ListView(getContext());
        ArrayList<DataItem> array2 = new ArrayList<>();

        array2.add(new DataItem("双色球过滤名词解释", "web2/ssq_dxjs.html"));
        array2.add(new DataItem("福彩3D单选过滤名词解释", "web2/fc3D_dxjs.html"));
        array2.add(new DataItem("排列三直选过滤名词解释", "web2/pl3_dxjs.html"));
        array2.add(new DataItem("天津时时彩三星直选过滤名词解释", "web2/tjssc_dxjs.html"));
        array2.add(new DataItem("新疆时时彩三星直选过滤名词解释", "web2/sjssc_dxjs.html"));
        array2.add(new DataItem("重庆时时彩三星直选过滤名词解释", "web2/cqssc_dxjs.html"));
        array2.add(new DataItem("北京11选5任选五过滤名词解释", "web2/bj11x5_dxjs.html"));
        array2.add(new DataItem("广东11选五任选五过滤名词解释", "web2/gd11x5_dxjs.html"));
        array2.add(new DataItem("江西11选5任选五过滤名词解释", "web2/jx11x5_dxjs.html"));
        array2.add(new DataItem("山东11选5任选五过滤名词解释", "web2/sd11x5_dxjs.html"));
        array2.add(new DataItem("上海11选5任选五过滤名词解释", "web2/sh11x5_dxjs.html"));

        listView2.setAdapter(new MyListAdapter(array2));
        listView2.setOnItemClickListener(itemClickListener);
        views.add(listView2);
    }

    private AdapterView.OnItemClickListener itemClickListener = (adapterView, view, index, l) -> {
        DataItem dataItem = (DataItem) adapterView.getAdapter().getItem(index);
        WebViewActivity.start(getActivity(), dataItem.subPath);
    };

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButton1:
                selectPage(0);
                break;
            case R.id.radioButton2:
                selectPage(1);
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

    private PagerAdapter myPageAdapter = new PagerAdapter() {
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

    private static class DataItem {
        String text;
        String subPath;

        public DataItem(String text, String subPath) {
            this.text = text;
            this.subPath = subPath;
        }
    }

    private class MyListAdapter extends BaseAdapter {
        private ArrayList<DataItem> array;

        public MyListAdapter(ArrayList<DataItem> array) {
            this.array = array;
        }

        @Override
        public int getCount() {
            return array.size();
        }

        @Override
        public Object getItem(int arg0) {
            return array.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null);
            }

            TextView view = (TextView) convertView.findViewById(android.R.id.text1);

            view.setText(array.get(position).text);

            return convertView;
        }
    }
}
