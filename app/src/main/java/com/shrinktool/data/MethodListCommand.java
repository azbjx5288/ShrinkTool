package com.shrinktool.data;

import com.android.volley.Request;
import com.shrinktool.base.net.RequestConfig;

/**
 * 某彩种玩法信息查询
 * Created by ACE-PC on 2016/1/21.
 */
@RequestConfig(api = "?c=game&a=methodList", method = Request.Method.GET)
public class MethodListCommand {
    /**Int	不为空	lotteryID，空表示查所有彩种*/
    private int lotteryId;

    public int getLotteryID() {
        return lotteryId;
    }

    public void setLotteryID(int lotteryID) {
        this.lotteryId = lotteryID;
    }
}
