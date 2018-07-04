package com.shrinktool.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created on 2016/1/19.
 *
 * @author ACE
 * @功能描述: 历史开奖
 */
public class LotteryHistoryFragment extends BaseFragment {
    private static final String TAG = LotteryHistoryFragment.class.getSimpleName();

    private static final int LOTTERIES_HISTORY_ID = 1;


    @Bind(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.list)
    ListView listView;
    private List<List<LotteriesHistory>> historyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflateView(inflater, container, false, "开奖公告", R.layout.lotteries_history_fragment);
        return inflater.inflate(R.layout.lotteries_history_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"), Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
        refreshLayout.setOnRefreshListener(()->loadData(false));
        listView.setAdapter(adapter);
        loadData(true);
    }

    private void loadData(boolean useCache) {
        ALLOpenCodeCommand command = new ALLOpenCodeCommand();
        TypeToken typeToken = new TypeToken<RestResponse<List<List<LotteriesHistory>>>>() {
        };
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), command, typeToken, restCallback,
                0, this);
        if (useCache) {
            RestResponse restResponse = restRequest.getCache();
            if (restResponse != null && restResponse.getData() instanceof ArrayList) {
                UpdateUi((List<List<LotteriesHistory>>) restResponse.getData());
            }
        }
        restRequest.execute();
    }

    private void UpdateUi(List<List<LotteriesHistory>> lists){
        historyList = lists;
        adapter.notifyDataSetChanged();

        listView.setFocusable(false);
        int widthMS = View.MeasureSpec.makeMeasureSpec(getView().getWidth(), View.MeasureSpec.EXACTLY);
        int heightMS = View.MeasureSpec.makeMeasureSpec(1073741823, View.MeasureSpec.AT_MOST);
        listView.measure(widthMS, heightMS);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = listView.getMeasuredHeight();
        Log.i(TAG, "UpdateUi: " + params.height);
        listView.setLayoutParams(params);

        params = refreshLayout.getLayoutParams();
        params.height = listView.getMeasuredHeight();
        refreshLayout.setLayoutParams(params);
    }

    public void refresh() {
        if (refreshLayout != null) {
            loadData(false);
        }
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            UpdateUi((List<List<LotteriesHistory>>) response.getData());
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

    private BaseAdapter adapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return historyList == null ? 0 : historyList.size();
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
    };

    @OnItemClick(R.id.list)
    public void onItemClick(int position) {
        LotteriesHistory info = historyList.get(position).get(0);
        /*Bundle bundle = new Bundle();
        bundle.putInt("id", info.getLotteryId());
        launchFragment(OpenCodeHistoryFragment.class, bundle);*/

        GameActivity.launch(getActivity(), info.getLotteryId(), 1);
    }

    static class ViewHolder {
        @Bind(R.id.issue)
        TextView issue;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.ball_list)
        LinearLayout  ballList;
        @Bind(R.id.lottery_ico)
        ImageView lotteryIco;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
            convertView.setTag(this);
        }
    }
}