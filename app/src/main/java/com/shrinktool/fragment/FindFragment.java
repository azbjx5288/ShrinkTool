package com.shrinktool.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.component.BannerHelper;
import com.shrinktool.component.CycleViewPager;
import com.shrinktool.component.FindRadio;
import com.shrinktool.component.FindRadioArticle;
import com.shrinktool.component.FindRadioLotteryHistory;
import com.shrinktool.component.FindRadioTrend;

import butterknife.Bind;
import butterknife.OnItemClick;

/**
 * 发现页
 * Created by Alashi on 2017/2/10.
 */
public class FindFragment extends BaseFragment {
    @Bind(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;
    @Bind(R.id.list) ListView listView;

    private int radioIndex;
    private FindRadio[] findRadios;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.refreshable_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"), Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
        refreshLayout.setOnRefreshListener(()->findRadios[radioIndex].reload());
        initHeaderView();

        FindRadio lotteryHistory = new FindRadioLotteryHistory(this, refreshLayout, adapter);
        FindRadio article = new FindRadioArticle(this, refreshLayout, adapter);
        FindRadio trend = new FindRadioTrend(this, refreshLayout, adapter);
        findRadios = new FindRadio[]{lotteryHistory, article, trend};
        selectPage(0);

        listView.setAdapter(adapter);
    }

    @OnItemClick(R.id.list)
    public void onItemClick(int position) {
        findRadios[radioIndex].onItemClick(position - listView.getHeaderViewsCount());
    }

    private void initHeaderView(){
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_find_head, null);
        listView.addHeaderView(headerView);

        CycleViewPager cycleViewPager = (CycleViewPager) getActivity().getFragmentManager().findFragmentById(R.id
                .lotteryHistoryCycleViewPager);
        new BannerHelper(getActivity(), cycleViewPager, 2, notice -> {
            if (TextUtils.isEmpty(notice.getLink())) {
                //NoticeDetailsFragment.launch(FragmentHome.this, false, notices.get(position));
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(notice.getLink()));
                startActivity(browserIntent);
            }
        });

        RadioGroup radioGroup = (RadioGroup) headerView.findViewById(R.id.findRadioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    selectPage(i);
                    break;
                }
            }
        });
    }

    private void selectPage(int index) {
        radioIndex = index;
        adapter.notifyDataSetChanged();
        findRadios[radioIndex].reload();
    }

    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return findRadios[radioIndex].getCount();
        }

        @Override
        public int getViewTypeCount() {
            return findRadios.length;
        }

        @Override
        public int getItemViewType(int position) {
            return radioIndex;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return findRadios[radioIndex].getView(position, convertView, parent);
        }
    };
}
