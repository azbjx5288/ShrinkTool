package com.shrinktool.component;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.TrendActivity;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.Lottery;
import com.shrinktool.data.LotteryListCommand;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 发现页--走势图
 * Created by User on 2017/2/10.
 */

public class FindRadioTrend extends FindRadio {
    private ArrayList<Lottery> lotteryList;

    public FindRadioTrend(BaseFragment fragment, SwipeRefreshLayout refreshLayout, BaseAdapter adapter) {
        super(fragment, refreshLayout, adapter);
    }

    @Override
    public void reload() {
        LotteryListCommand lotteryListCommand = new LotteryListCommand();
        lotteryListCommand.setLotteryID(0);
        TypeToken typeToken = new TypeToken<RestResponse<ArrayList<Lottery>>>() {
        };
        RestRequest restRequest = RestRequestManager.createRequest(fragment.getActivity(), lotteryListCommand, typeToken,
                restCallback, 0, this);
        restRequest.execute();
    }

    private RestCallback restCallback = new RestCallback<ArrayList<?>>() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            lotteryList = (ArrayList<Lottery>) response.getData();
            adapter.notifyDataSetChanged();
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
    public void onItemClick(int position) {

    }

    private View.OnClickListener clickListener = v -> {
        int index = (int) v.getTag();
        if (index < lotteryList .size()) {
            TrendActivity.launch(fragment.getActivity(), lotteryList.get(index).getLotteryId());
        }
    };

    @Override
    public int getCount() {
        return lotteryList == null? 0 : (lotteryList.size() + 1) / 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.fragment_trend_tab_2_item, parent, false);
            holder = new ViewHolder(convertView);
            holder.item1.setOnClickListener(clickListener);
            holder.item2.setOnClickListener(clickListener);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Lottery info = lotteryList.get(position *2);
        holder.name1.setText(info.getCname());
        holder.item1.setTag(position *2);

        if (position *2 + 1 < lotteryList.size() - 1) {
            info = lotteryList.get(position * 2 + 1);
            holder.name2.setText(info.getCname());
            holder.item2.setVisibility(View.VISIBLE);
        } else {
            holder.item2.setVisibility(View.INVISIBLE);
        }
        holder.item2.setTag(position * 2 + 1);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.item1)
        View item1;
        @Bind(R.id.name)
        TextView name1;
        @Bind(R.id.item2)
        View item2;
        @Bind(R.id.name2)
        TextView name2;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
            convertView.setTag(this);
        }
    }
}
