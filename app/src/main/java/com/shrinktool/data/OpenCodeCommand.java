package com.shrinktool.data;

import com.android.volley.Request;
import com.shrinktool.base.net.RequestConfig;

/**
 * 彩种的历史开奖号码
 * Created by Alashi on 2016/8/5.
 */
@RequestConfig(api = "?c=game&a=openCodeHistory", method = Request.Method.GET,
        response = OpenCodeIssue.class)
public class OpenCodeCommand {
    private int lotteryId;
    private int curPage;
    private int perPage;

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }
}
