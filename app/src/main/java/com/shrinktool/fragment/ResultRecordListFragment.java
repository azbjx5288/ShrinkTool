package com.shrinktool.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.GameActivity;
import com.shrinktool.base.Preferences;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.PlanListCommand;
import com.shrinktool.data.PlanListItem;
import com.shrinktool.game.GameConfig;
import com.shrinktool.material.ConstantInformation;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemLongClick;

/**
 * 过滤记录列表页面
 * Created by Alashi on 2016/6/3.
 */
public class ResultRecordListFragment extends BaseFragment {

    /**
     * 服务器分页从1开始
     */
    private static final int FIRST_PAGE = 1;

    private static final String[] NO_ITEM_TEXT = {
            "暂无记录",
            "暂无待开奖记录",
            "暂无中奖记录",
            "暂无未中奖记录"};

    @Bind(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;
    @Bind(R.id.list) ListView listView;
    @Bind(R.id.listNoItemLayout) View listNoItemLayout;
    @Bind(R.id.listNoItemLayoutText) TextView listNoItemLayoutText;

    private ArrayList<PlanListItem> items = new ArrayList();

    private MyAdapter myAdapter;

    //类型：我的方案，待开奖，中奖，未开奖
    private int type;

    public void setType(int type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflateView(inflater, container, "过滤记录", R.layout.refreshable_list_fragment);
        return inflater.inflate(R.layout.result_record_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"), Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
        refreshLayout.setOnRefreshListener(() -> loadDataOnline(false));
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        loadDataOnline(false);
    }

    private void loadDataOnline(boolean cache) {
        PlanListCommand command = new PlanListCommand();
        String[] typeNames = new String[]{null, "-1", "1", "0"};
        command.setIsPrize(typeNames[type]);
        command.setCurPage(1);
        command.setPerPage(100);

        /*TypeToken typeToken = new TypeToken<RestResponse<ArrayList<PlanListItem>>>() {};
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), command,
                typeToken, restCallback, 0, this);
        restRequest.execute();*/
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), command, restCallback, 0, this);
        if (cache) {
            RestResponse restResponse = restRequest.getCache();
            if (restResponse != null && restResponse.getData() instanceof PlanListCommand.PlanListData) {
                applyData((PlanListCommand.PlanListData) restResponse.getData());
            }
        }
        restRequest.execute();
    }

    private void applyData(PlanListCommand.PlanListData planListData) {
        items.clear();
        if (planListData != null) {
            for (Map.Entry<String, PlanListItem> stringPlanListItemEntry : planListData.plans.entrySet()) {
                items.add(stringPlanListItemEntry.getValue());
            }
        }

        if (items.size() == 0) {
            listNoItemLayoutText.setText(NO_ITEM_TEXT[type]);
            listNoItemLayout.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        } else {
            refreshLayout.setVisibility(View.VISIBLE);
            listNoItemLayout.setVisibility(View.GONE);
            myAdapter.notifyDataSetChanged();
        }
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            applyData((PlanListCommand.PlanListData) response.getData());
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

    @OnItemLongClick(R.id.list)
    public boolean onItemLongClick(int position){
        new AlertDialog.Builder(getContext())
                .setTitle("删除过滤记录")
                .setMessage("确定删除此过滤记录？")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", (d, w) -> {
                    //TODO:删除过滤方案
                }).show();
        return true;
    }

    /*@OnItemClick(R.id.list)
    public void onItemClick(int position) {
        ResultRecordFragment.launch(this, items.get(position).filterId);
    }*/

    @OnClick(R.id.go)
    public void onButton(View view) {
        int lotteryId = Preferences.getInt(getActivity(), "GameActivity_last_lottery", 1);
        GameActivity.launch(getActivity(), lotteryId, 0);
    }

    private View.OnClickListener go2gameListener = v ->
            GameActivity.launch(getActivity(), (Integer) v.getTag(), 0);

    private View.OnClickListener convertViewListener = v ->
            ResultRecordFragment.launch(this, ((Holder) v.getTag()).filterId);


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return items == null? 0 : items.size();
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
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.result_record_list_item, parent, false);
                holder = new Holder(convertView);
                holder.go2game.setOnClickListener(go2gameListener);
                convertView.setOnClickListener(convertViewListener);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            PlanListItem item = items.get(position);
            holder.filterId = item.filterId;
            holder.filterNo.setText("方案编号：" + item.filterId);
            holder.go2game.setTag(item.getLotteryId());
            String time = item.getInputTime();
            if (time != null && time.length() > 16){
                holder.time.setText(time.substring(0, 16));
            } else {
                holder.time.setText(time);
            }
            holder.icon.setImageResource(ConstantInformation.getLotteryLogo(item.getLotteryId()));
            holder.name.setText(GameConfig.getLotteryName(item.getLotteryId()));
            holder.issue.setText("第" + item.getIssue() + "期");
            holder.money.setText(Html.fromHtml(String.format("共<font color=#FFA423>%d</font>注<font color=#FFA423>%d</font>元",
                    item.amount / 2, item.amount)));
            //inputTime;
            switch (item.isPrize) {
                case -1:
                    //holder.state.setTextColor(Color.GRAY);
                    holder.state.setText("等待开奖");
                    break;
                case 0:
                    //holder.state.setTextColor(Color.GRAY);
                    holder.state.setText("已开奖");//未中奖
                    break;
                default:
                    //holder.state.setTextColor(Color.RED);
                    //holder.state.setText("中奖"+ item.isPrize + "元");
                    holder.state.setText("已开奖");
            }
            return convertView;
        }
    }

    static class Holder {
        @Bind(R.id.filterNo) TextView filterNo;
        @Bind(R.id.lotteryName) TextView name;
        @Bind(R.id.lotteryIco) ImageView icon;
        @Bind(R.id.issue) TextView issue;
        @Bind(R.id.money) TextView money;
        @Bind(R.id.state) TextView state;
        @Bind(R.id.time) TextView time;
        @Bind(R.id.go2game) View go2game;

        int filterId;

        public Holder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
