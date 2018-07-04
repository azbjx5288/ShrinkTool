package com.shrinktool.component;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.GameIssueInfo;
import com.shrinktool.data.Issue;
import com.shrinktool.data.OpenCodeCommand;
import com.shrinktool.data.OpenCodeIssue;
import com.shrinktool.view.IssueInfoDropDown;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 选号界面，最近10期开奖号码列表的处理
 * Created by Alashi on 2016/12/9.
 */
public class HistoryIssueHelper {
    private Activity activity;
    private IssueInfoDropDown issueInfoDropDown;
    private TextView infoTextView;
    private int lotteryId;
    private List<Issue> issueList;
    private GameIssueInfo gameIssueInfo;
    private OnChangedListener listener;

    public interface OnChangedListener{
        void onChanged();
    }

    public HistoryIssueHelper(Activity activity, int lotteryId, IssueInfoDropDown issueInfoDropDown,
                              OnChangedListener listener) {
        this.listener = listener;
        this.activity = activity;
        this.lotteryId = lotteryId;
        this.issueInfoDropDown = issueInfoDropDown;
        infoTextView = (TextView) issueInfoDropDown.findViewById(R.id.gameIssueInfoList);
        loadData(true);
    }

    private void loadData(boolean useCache) {
        OpenCodeCommand command = new OpenCodeCommand();
        command.setLotteryId(lotteryId);
        command.setCurPage(1);
        command.setPerPage(10);
        RestRequest restRequest = RestRequestManager.createRequest(activity,
                command, restCallback, 0, this);
        if (useCache) {
            RestResponse restResponse = restRequest.getCache();
            if (restResponse != null && restResponse.getData() instanceof ArrayList) {
                update(((OpenCodeIssue) restResponse.getData()).getIssue());
            }
        }
        restRequest.execute();
    }

    public void refresh() {
        loadData(false);
    }

    public void setGameIssueInfo(GameIssueInfo gameIssueInfo) {
        this.gameIssueInfo = gameIssueInfo;
    }

    private void update(List<Issue> issueList) {
        if (issueList == null) {
            return;
        }

        StringBuffer buffer = new StringBuffer();
        for (int i = issueList.size() - 1; i >= 0; i--) {
            Issue historyIssue = issueList.get(i);
            buffer.append(String.format("<font color=#FF6633>%s期&nbsp;&nbsp;</font>",
                    historyIssue.getIssue()));
            if (lotteryId == 100) {
                formatSSQ(buffer, historyIssue);
            } else {
                buffer.append(String.format("<font color=#FF3366>%s</font>",
                        historyIssue.getCode()));
            }

            if (i != 0) {
                buffer.append("<br>");
            }
        }

        infoTextView.setText(Html.fromHtml(buffer.toString()));
        issueInfoDropDown.onExpandableViewChanged();
        listener.onChanged();
    }

    //格式化双色球的开奖
    private void formatSSQ(StringBuffer buffer, Issue historyIssue) {
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
                buffer.append(String.format("<font color=#FF3366>%s</font><font color=#33b5e5>%s</font>",
                        red, tmp.length() < 2? "0" + tmp : tmp));
            }
            index++;
        }
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            update(((OpenCodeIssue) response.getData()).getIssue());
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
