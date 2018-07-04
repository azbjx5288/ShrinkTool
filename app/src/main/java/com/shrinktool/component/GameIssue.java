package com.shrinktool.component;

import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.GameIssueInfo;
import com.shrinktool.data.GameIssueInfoCommand;
import com.shrinktool.data.IssueInfoEntity;
import com.shrinktool.fragment.GameFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * 选号界面的，负责加载并定时更新最新期号等信息的
 * Created by Alashi on 2016/8/5.
 */
public class GameIssue {
    private static final String TAG = "GameIssue";

    private TextView issueView;
    private TextView timeView;
    private int lotteryId;
    private BaseFragment fragment;
    private GameIssueInfo gameIssueInfo;

    private SimpleDateFormat endTimeFormat;
    private SimpleDateFormat serverTimeFormat;
    private boolean isResume;
    private String lastCode;
    private String issue;

    private LinkedHashMap<String, int[]> missCode;
    private OnIssueChangedListener listener;

    public interface OnIssueChangedListener{
        void onIssueChanged();
    }

    public GameIssue(int lotteryId, GameFragment gameFragment, LinearLayout gameIssueInfoLayout,
                     OnIssueChangedListener listener) {
        this.lotteryId = lotteryId;
        this.fragment = gameFragment;
        this.listener = listener;
        issueView = (TextView) gameIssueInfoLayout.findViewById(R.id.game_issue);
        timeView = (TextView) gameIssueInfoLayout.findViewById(R.id.game_time);

        serverTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//2016/08/05 17:54:31
        endTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//2016-08-05 18:30:30
    }

    public String getLastIssueCode() {
        return lastCode;
    }

    public int[] getMissCode(int methodId) {
        if (missCode != null) {
            return missCode.get(String.valueOf(methodId));
        }

        return null;
    }

    public void onResume() {
        Log.d(TAG, "onResume: ");
        isResume = true;
        loadIssueOnline();
    }

    public void onPause() {
        Log.d(TAG, "onPause: ");
        isResume = false;
        timeView.removeCallbacks(runnable);
    }

    private void loadIssueOnline() {
        GameIssueInfoCommand command = new GameIssueInfoCommand();
        command.setLotteryId(lotteryId);
        RestRequestManager.executeCommand(fragment.getContext(), command,
                restCallback, 0, fragment);
    }

    public GameIssueInfo getGameIssueInfo() {
        return gameIssueInfo;
    }

    private void updateUi(){
        IssueInfoEntity info = gameIssueInfo.getIssueInfo();
        issueView.setText(info.getIssue() + "期截止时间：");
        timeView.setText(info.getEndtime());//TODO:格式化

        try {
            Date endTime = endTimeFormat.parse(info.getEndtime());//截止日期
            Date serverTime = serverTimeFormat.parse(gameIssueInfo.getServerTime());//服务器时间
            //Date nowTime = new Date(System.currentTimeMillis());//手机当前手机

            timeView.removeCallbacks(runnable);
            Log.d(TAG, "updateUi: next -> " + (endTime.getTime() - serverTime.getTime()));
            timeView.postDelayed(runnable, Math.max(3000, endTime.getTime() - serverTime.getTime()) );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (listener != null) {
            listener.onIssueChanged();
        }
    }

    private Runnable runnable = () -> {
        if (isResume){
            loadIssueOnline();
        }
    };

    private RestCallback restCallback = new RestCallback() {

        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            gameIssueInfo = (GameIssueInfo) response.getData();
            if (gameIssueInfo.getLastIssueInfo() != null) {
                lastCode = gameIssueInfo.getLastIssueInfo().getCode();
                missCode = gameIssueInfo.getLastIssueInfo().getMissCode();
            } else {
                lastCode = null;
                missCode = null;
            }
            issue = gameIssueInfo.getIssueInfo().getIssue();
            updateUi();
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            timeView.postDelayed(runnable, 100000);//10秒后重试
            lastCode = null;
            issue = null;
            missCode = null;
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {

        }
    };

    public String getIssue() {
        return issue;
    }
}
