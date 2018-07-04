package com.shrinktool.data;

import com.android.volley.Request;
import com.shrinktool.base.net.RequestConfig;

/**
 * 开奖信息获取
 * Created by ACE-PC on 2016/1/22.
 */

@RequestConfig(api = "?c=game&a=getIssueInfo", method = Request.Method.GET,
        response = GameIssueInfo.class)
public class GameIssueInfoCommand {
    private String op = "getCurIssue";

    private int lotteryId;

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public int getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }
}

