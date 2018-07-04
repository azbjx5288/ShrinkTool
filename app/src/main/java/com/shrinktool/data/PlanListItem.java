package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

/**
 * 过滤方案列表的项
 * Created by Alashi on 2016/8/10.
 */
public class PlanListItem {
    @SerializedName("fp_id")
    public int filterId;
    @SerializedName("lottery_id")
    public int lotteryId;
    @SerializedName("method_id")
    public int methodId;
    public String issue;
    @SerializedName("is_prize")
    public int isPrize;
    @SerializedName("input_time")
    public String inputTime;
    public String code;
    public int amount;

    public int getFilterId() {
        return filterId;
    }

    public int getLotteryId() {
        return lotteryId;
    }

    public int getMethodId() {
        return methodId;
    }

    public String getIssue() {
        return issue;
    }

    public int isPrize() {
        return isPrize;
    }

    public String getCode() {
        return code;
    }

    public String getInputTime() {
        return inputTime;
    }
}
