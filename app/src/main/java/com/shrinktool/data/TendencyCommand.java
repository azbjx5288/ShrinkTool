package com.shrinktool.data;

import com.android.volley.Request;
import com.shrinktool.base.net.RequestConfig;

/**
 * 获取彩种走势图数据
 * Created by Alashi on 2016/8/8.
 */
@RequestConfig(api = "?c=tendency&a=getTendency", method = Request.Method.GET)
public class TendencyCommand {
    private int lotteryId;
    private int methodId;
    private int issueNums;
    private String type = "ALL";

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public void setIssueNums(int issueNums) {
        this.issueNums = issueNums;
    }
}
