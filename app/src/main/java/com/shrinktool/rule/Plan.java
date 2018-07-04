package com.shrinktool.rule;

import android.support.annotation.Keep;

/**
 * 过滤方案，区别于{@link ResultRecord}，数据被提交到服务器，重新获取的时候可能因版本变化改变
 * {@link Plan#record}的结构
 * Created by Alashi on 2016/8/10.
 */
@Keep
public class Plan {
    /** 客户端的record对接数据的版本号，重点，
     * 若ResultRecord改变，需要保留旧版本，添加新版本后需要对服务器数据 */
    private int version;
    /** 具体的记录 */
    private String record;
    private int lotteryId;
    private int methodId;
    private String issue;
    private int filterTotal;
    private int filterCount;
    private int pickCount;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public int getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }

    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public int getFilterTotal() {
        return filterTotal;
    }

    public void setFilterTotal(int filterTotal) {
        this.filterTotal = filterTotal;
    }

    public int getFilterCount() {
        return filterCount;
    }

    public void setFilterCount(int filterCount) {
        this.filterCount = filterCount;
    }

    public int getPickCount() {
        return pickCount;
    }

    public void setPickCount(int pickCount) {
        this.pickCount = pickCount;
    }
}
