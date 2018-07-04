package com.shrinktool.fragment;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.component.ExpandableViewHelper;
import com.shrinktool.data.Issue;
import com.shrinktool.data.OpenCodeCommand;
import com.shrinktool.data.OpenCodeIssue;
import com.shrinktool.view.OpenCodeDetailsView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 单个彩种开奖历史
 * Created by Alashi on 2016/8/5.
 */
public class OpenCodeHistoryFragment extends BaseFragment {
    private static final String TAG = "OpenCodeHistoryFragment";

    @Bind(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.list)
    ListView listView;

    private List<Issue> issueList;
    private int lotteryId;

    private ViewHolder lastOpen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflateView(inflater, container, true, "开奖历史", R.layout.open_code_history_list_fragment);
        return inflater.inflate(R.layout.open_code_history_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"), Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
        refreshLayout.setOnRefreshListener(()->loadData(false));
        listView.setAdapter(adapter);
        lotteryId = getArguments().getInt("id");
        //addMenuItem(R.drawable.muan, this::showMenu);
        loadData(true);
    }

    private void loadData(boolean useCache) {
        OpenCodeCommand command = new OpenCodeCommand();
        command.setLotteryId(lotteryId);
        command.setCurPage(1);
        command.setPerPage(50);
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), command, restCallback,
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

        @Override
        public int getCount() {
            return issueList == null ? 0 : issueList.size();
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_code_histiry_list_item, parent, false);
                convertView.setOnClickListener(clickListener);
                holder = new ViewHolder(convertView);
                if (lotteryId == 100) {
                    holder.detailsView1.setColorProvider(colorProvider);
                }
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ViewGroup.LayoutParams params = holder.details.getLayoutParams();
            params.height = 0;
            holder.details.setLayoutParams(params);

            Issue info = issueList.get(position);
            holder.issue.setText(info.getIssue());
            if (info.getInputTime() != null && info.getInputTime().length() > 16) {
                holder.inputTime.setText(info.getInputTime().substring(0, 16));
            } else {
                holder.inputTime.setText(info.getInputTime());
            }
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

            bindDetail(info.getDetail(), holder);

            return convertView;
        }
    };

    private void bindDetail(Issue.Detail detail, ViewHolder holder) {
        LinkedHashMap<String, String[]> header = new LinkedHashMap<>();
        LinkedHashMap<String, String> headerSrc = detail.getHeader();
        for (Map.Entry<String, String> entry : headerSrc.entrySet()) {
            header.put(entry.getKey(), new String[] { entry.getValue() });
        }
        if (header.size() == 0) {
            holder.detailsView1.setVisibility(View.GONE);
        } else {
            holder.detailsView1.setVisibility(View.VISIBLE);
            holder.detailsView1.setDate(header);
        }

        holder.detailsView2.setDate(detail.getBody());
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (lastOpen != null && lastOpen != holder) {
                if (lastOpen.details.getHeight() != 0) {
                    lastOpen.expandableViewHelper.startCloseAnimation();
                }
            }

            if (holder.details.getHeight() == 0) {
                Rect rect = new Rect();
                convertView.getGlobalVisibleRect(rect);
                int vBottom = rect.bottom;
                listView.getGlobalVisibleRect(rect);
                int listBottom = rect.bottom;
                int expandHeight = holder.expandableViewHelper.startOpenAnimation();
                int offY = vBottom + expandHeight - listBottom;
                if (offY > 0) {
                    //需要滑动到展开界面
                    listView.postDelayed(() -> listView.startAnimation(new ScrollAnimation(offY)), 100);
                }
            } else {
                holder.expandableViewHelper.startCloseAnimation();
            }
            lastOpen = holder;
        }
    };

    private class ScrollAnimation extends Animation {
        int totalHeight;
        int lastOffset;

        ScrollAnimation(int totalHeight) {
            this.totalHeight = totalHeight;
            setDuration(300);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int offset = (int) (totalHeight * interpolatedTime) - lastOffset;
            lastOffset += offset;
            listView.smoothScrollBy(offset, 0);
        }
    }

    private OpenCodeDetailsView.TextColorProvider colorProvider = new OpenCodeDetailsView.TextColorProvider() {
        int redColor = Color.parseColor("#E04B62");
        int greyColor = Color.parseColor("#979797");
        @Override
        public int getColor(int xIndex, int yIndex) {
            if (xIndex == 1 && yIndex == 1) {
                //‘奖池奖金’的数字颜色
                return redColor;
            }
            return greyColor;
        }
    };

    static class ViewHolder {
        @Bind(R.id.issue)
        TextView issue;
        @Bind(R.id.inputTime)
        TextView inputTime;
        @Bind(R.id.ball_list)
        LinearLayout ballList;
        @Bind(R.id.details)
        LinearLayout details;
        @Bind(R.id.detailsView1)
        OpenCodeDetailsView detailsView1;
        @Bind(R.id.detailsView2)
        OpenCodeDetailsView detailsView2;

        ExpandableViewHelper expandableViewHelper;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
            convertView.setTag(this);
            expandableViewHelper = new ExpandableViewHelper(details);
        }
    }
}
