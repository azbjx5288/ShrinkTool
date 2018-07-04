package com.shrinktool.data;

import com.shrinktool.base.net.RequestConfig;

/**
 * 彩票种类信息查询
 * Created by Alashi on 2016/1/5.
 */
@RequestConfig(api = "?c=game&a=lotteryList")
public class LotteryListCommand {
    /**Int	可为空	彩种ID，空表示查所有彩种*/
    private int lotteryID;

    public int getLotteryID() {
        return lotteryID;
    }

    public void setLotteryID(int lotteryID) {
        this.lotteryID = lotteryID;
    }
}
