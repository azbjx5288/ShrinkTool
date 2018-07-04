package com.shrinktool.component;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.GameActivity;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.ALLOpenCodeCommand;
import com.shrinktool.data.LotteriesHistory;
import com.shrinktool.material.ConstantInformation;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 发现页--开奖号码
 * Created by Alashi on 2017/2/10.
 */

public class FindRadioLotteryHistory extends FindRadio{

    private List<List<LotteriesHistory>> historyList;

    public FindRadioLotteryHistory(BaseFragment fragment, SwipeRefreshLayout refreshLayout, BaseAdapter adapter) {
        super(fragment, refreshLayout, adapter);
    }


    @Override
    public void reload() {
        ALLOpenCodeCommand command = new ALLOpenCodeCommand();
        TypeToken typeToken = new TypeToken<RestResponse<List<List<LotteriesHistory>>>>() {
        };
        RestRequest restRequest = RestRequestManager.createRequest(fragment.getActivity(), command, typeToken,
                restCallback, 0, this);
        restRequest.execute();
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            historyList = (List<List<LotteriesHistory>>) response.getData();
            adapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            refreshLayout.setRefreshing(state == RestRequest.RUNNING);
        }
    };

    @Override
    public void onItemClick(int position) {
        LotteriesHistory info = historyList.get(position).get(0);
        GameActivity.launch(fragment.getActivity(), info.getLotteryId(), 1);
    }

    @Override
    public int getCount() {
        return historyList == null? 0 : historyList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lottery_list_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LotteriesHistory info = historyList.get(position).get(0);
        holder.lotteryIco.setImageResource(ConstantInformation.getLotteryLogo(info.getLotteryId()));
        holder.issue.setText("第" + info.getIssue() + "期");
        holder.name.setText(info.getCname());
        int childCount = holder.ballList.getChildCount();
        if (info.getCode().contains(" ")) {
            String[] codes = info.getCode().split(" ");
            for (int i = 0; i < childCount; i++) {
                TextView ball = (TextView) holder.ballList.getChildAt(i);
                if (i < codes.length) {
                    ball.setVisibility(View.VISIBLE);
                    ball.setText(codes[i].length() == 1? "0" + codes[i] : codes[i]);
                } else {
                    ball.setVisibility(View.GONE);
                }
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                TextView ball = (TextView) holder.ballList.getChildAt(i);
                if (i < info.getCode().length()) {
                    ball.setVisibility(View.VISIBLE);
                    ball.setText(String.valueOf(info.getCode().charAt(i) - 48));
                } else {
                    ball.setVisibility(View.GONE);
                }
            }
        }
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.issue)
        TextView issue;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.ball_list)
        LinearLayout ballList;
        @Bind(R.id.lottery_ico)
        ImageView lotteryIco;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
            convertView.setTag(this);
        }
    }
}
