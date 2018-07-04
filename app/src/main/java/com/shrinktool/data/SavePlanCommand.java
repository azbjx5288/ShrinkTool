package com.shrinktool.data;

import com.shrinktool.base.net.RequestConfig;
import com.shrinktool.rule.Plan;

/**
 * 过滤方案保存
 * Created by Alashi on 2016/8/10.
 */
@RequestConfig(api = "?c=game&a=savePlan", response = Integer.class)
public class SavePlanCommand {
    private int lotteryId;
    private int methodId;
    private String issue;
    private Plan plan;
    private String result;
    /** 方案总金额,按照int传值 */
    private int amount;

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
