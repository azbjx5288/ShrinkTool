package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;
import com.shrinktool.base.net.RequestConfig;

import java.util.LinkedHashMap;

/**
 * 获取过滤方案列表
 * Created by Alashi on 2016/8/10.
 */
@RequestConfig(api = "?c=game&a=planList", response = PlanListCommand.PlanListData.class)
public class PlanListCommand {
    //lotteryId	Int      	1	0或空表示所有彩种,可不传
    //issue	string		可不传
    //is_prize	Int	0	是否中奖 ：0 未判奖，1中奖 ，2 未中奖 ; 可不传
    //end_time	datetime		结束时间段  可不传
    //code	string		CHECK_NULL 查没抓到号的; CHECK_DRAW 查有抓到号的；可不传

    /** curPage	int		当前页数  从 1 开始可不传 */
    private int curPage;
    /** start_time	datetime		开始时间段  必须传 */
    @SerializedName("start_time")
    private String startTime = "2016-07-02";
    /** perPage	int		每页显示条数默20 可不传 */
    private int perPage;
    @SerializedName("is_prize")
    private String isPrize;

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public void setIsPrize(String isPrize) {
        this.isPrize = isPrize;
    }

    public static class PlanListData{
        public LinkedHashMap<String, PlanListItem> plans;
        public int totalNum;
    }
}
