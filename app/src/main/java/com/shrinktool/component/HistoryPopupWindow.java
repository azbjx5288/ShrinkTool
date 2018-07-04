package com.shrinktool.component;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.Issue;
import com.shrinktool.data.OpenCodeCommand;
import com.shrinktool.data.OpenCodeIssue;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * “选号页”用，显示历史开奖号码
 * Created by Alashi on 2016/7/28.
 */
public class HistoryPopupWindow {
    private Activity activity;
    private PopupWindow popupWindow;
    private SwipeRefreshLayout refreshLayout;
    private int lotteryId;
    private List<Issue> issueList;

    public HistoryPopupWindow(Activity activity, int lotteryId) {
        this.activity = activity;
        this.lotteryId = lotteryId;
    }

    public void show(View view) {
        if (popupWindow == null) {
            View layout = LayoutInflater.from(activity).inflate(R.layout.game_history, null);
            popupWindow = new PopupWindow(activity);
            popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(layout);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            popupWindow.setAnimationStyle(R.style.pulldown);

            refreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.refreshLayout);
            ListView listView = (ListView) layout.findViewById(R.id.list);
            refreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"), Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
            refreshLayout.setOnRefreshListener(() -> loadData(false));
            listView.setAdapter(adapter);
            layout.findViewById(R.id.gameDismiss).setOnClickListener(view1 -> popupWindow.dismiss());
            loadData(true);
        } else {
            loadData(false);
        }

        popupWindow.showAsDropDown(view);
    }

    private void loadData(boolean useCache) {
        OpenCodeCommand command = new OpenCodeCommand();
        command.setLotteryId(lotteryId);
        command.setCurPage(1);
        command.setPerPage(50);
        RestRequest restRequest = RestRequestManager.createRequest(activity, command, restCallback,
                0, this);
        if (useCache) {
            RestResponse restResponse = restRequest.getCache();
            if (restResponse != null && restResponse.getData() instanceof ArrayList) {
                issueList = ((OpenCodeIssue) restResponse.getData()).getIssue();
                adapter.notifyDataSetChanged();
            }
        }
        restRequest.execute();
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            issueList = ((OpenCodeIssue) response.getData()).getIssue();
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

    private BaseAdapter adapter = new BaseAdapter() {
        private String issueFormat = "第%s期";

        @Override
        public int getCount() {
            return issueList == null? 0: issueList.size();
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.game_history_item, viewGroup, false);
                holder = new ViewHolder(convertView, lotteryId);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Issue historyIssue = issueList.get(i);
            holder.issue.setText(String.format(issueFormat, historyIssue.getIssue()));
            if (lotteryId == 100) {
                int index = 0;
                String red = "";
                for (String tmp: historyIssue.getCode().split(" ")) {
                    if (index < 6) {
                        if (tmp.length() < 2) {
                            red += "0" + tmp + " ";
                        } else {
                            red += tmp + " ";
                        }
                    } else {
                        holder.code.setText(red);
                        if (tmp.length() < 2) {
                            holder.codeBlue.setText("0" + tmp);
                        } else {
                            holder.codeBlue.setText(tmp);
                        }
                    }
                    index++;
                }
            } else {
                holder.code.setText(historyIssue.getCode());
            }
            holder.time.setText("");

            return convertView;
        }
    };

    static class ViewHolder {
        @Bind(R.id.issue)
        TextView issue;
        @Bind(R.id.code)
        TextView code;
        @Bind(R.id.codeBlue)
        TextView codeBlue;
        @Bind(R.id.time)
        TextView  time;

        public ViewHolder(View convertView, int lotteryId) {
            ButterKnife.bind(this, convertView);
            codeBlue.setVisibility(lotteryId == 100? View.VISIBLE : View.GONE);
            convertView.setTag(this);
        }
    }
}
