package com.shrinktool.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.TrendActivity;
import com.shrinktool.base.net.GsonHelper;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.Lottery;
import com.shrinktool.data.LotteryListCommand;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnItemClick;

import static com.shrinktool.R.id.gridView;

public class TrendTabFragment extends BaseFragment {

    private static final int LOTTERY_TRACE_ID = 1;

    @Bind(gridView) GridView gridview;

    private MyAdapter adapter = new MyAdapter();
    private ArrayList<Lottery> lotteryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflateView(inflater, container, false, "图表走势", R.layout.fragment_trend_tab);
        return inflater.inflate(R.layout.fragment_trend_tab, container, false);
   }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //loadDataFromFile();
        loadLotteryListOnline();
        gridview.setAdapter(adapter);
    }

    @OnItemClick(gridView)
    public void onItemClick(int position) {
        TrendActivity.launch(getActivity(), lotteryList.get(position).getLotteryId());
    }

    private void loadDataFromFile() {
        String json = loadAssets("json/lotteryList.json");
        if (json == null) {
            loadLotteryListOnline();
            return;
        }
        TypeToken typeToken = new TypeToken<ArrayList<Lottery>>() {};
        lotteryList = GsonHelper.fromJson(json, typeToken.getType());
        adapter.notifyDataSetChanged();
    }

    private void loadLotteryListOnline() {
        LotteryListCommand lotteryListCommand = new LotteryListCommand();
        lotteryListCommand.setLotteryID(0);
        TypeToken typeToken = new TypeToken<RestResponse<ArrayList<Lottery>>>() {
        };
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), lotteryListCommand, typeToken, restCallback, LOTTERY_TRACE_ID, this);
        RestResponse restResponse = restRequest.getCache();
        if (restResponse != null && restResponse.getData() instanceof ArrayList) {
            updateLotteryUI((ArrayList<Lottery>) restResponse.getData());
        }
        restRequest.execute();
    }

    private void updateLotteryUI(ArrayList<Lottery> lotteries){
        lotteryList = lotteries;
        adapter.notifyDataSetChanged();
        gridview.setFocusable(false);
        int widthMS = View.MeasureSpec.makeMeasureSpec(getView().getWidth(), View.MeasureSpec.EXACTLY);
        int heightMS = View.MeasureSpec.makeMeasureSpec(1073741823, View.MeasureSpec.AT_MOST);
        gridview.measure(widthMS, heightMS);
        ViewGroup.LayoutParams params = gridview.getLayoutParams();
        params.height = gridview.getMeasuredHeight();
        gridview.setLayoutParams(params);
    }

    private RestCallback restCallback = new RestCallback<ArrayList<?>>() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            if (LOTTERY_TRACE_ID == request.getId()){
                updateLotteryUI((ArrayList<Lottery>) response.getData());
            }
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

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return lotteryList == null? 0 : lotteryList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            GirdHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.fragment_trend_tab_item, viewGroup, false);
                holder = new GirdHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.msg = (TextView) convertView.findViewById(R.id.msg);
                convertView.setTag(holder);
            } else {
                holder = (GirdHolder) convertView.getTag();
            }

            Lottery info = lotteryList.get(i);
            holder.name.setText(info.getCname());
            holder.msg.setText("");

            return convertView;
        }

        private class GirdHolder {
            TextView name;
            TextView msg;
        }
    }
}