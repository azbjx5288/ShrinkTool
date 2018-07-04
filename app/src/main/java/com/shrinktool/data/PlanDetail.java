package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;
import com.shrinktool.rule.Plan;

/**
 * 过滤方案详情，网络返回的数据
 * Created by Alashi on 2016/8/10.
 */
public class PlanDetail {
    @SerializedName("fp_id")
    public String filterId;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("lottery_id")
    public String lotteryId;
    @SerializedName("method_id")
    public String methodId;
    public String issue;
    @SerializedName("package_id")
    public String packageId;
    @SerializedName("filter_plan")
    public Plan filterPlan;
    @SerializedName("is_prize")
    public int isPrize;
    public String code;
    @SerializedName("create_time")
    public String createTime;
    @SerializedName("input_time")
    public String inputTime;
    public String ts;
    @SerializedName("filter_result")
    public String filterResult;

    public String getFilterId() {
        return filterId;
    }

    public String getUserId() {
        return userId;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public String getMethodId() {
        return methodId;
    }

    public String getIssue() {
        return issue;
    }

    public String getPackageId() {
        return packageId;
    }

    public Plan getFilterPlan() {
        return filterPlan;
    }

    public int getIsPrize() {
        return isPrize;
    }

    public String getCode() {
        return code;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getTs() {
        return ts;
    }

    public String getFilterResult() {
        return filterResult;
    }

    public String getInputTime() {
        return inputTime;
    }
}
