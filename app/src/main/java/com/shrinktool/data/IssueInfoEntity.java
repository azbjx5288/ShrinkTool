package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

/**
 * 销售信息奖期信息
 * Created by ACE-PC on 2016/1/27.
 */
public class IssueInfoEntity {
    /**
     * issue_id : 4961609
     * issue : 20160122-0816
     * end_time : 2016-01-22 14:36:00
     * input_time : 2016-01-22 14:36:00
     */
    @SerializedName("issue_id")
    private String issueid;
    private String issue;
    @SerializedName("end_time")
    private String endtime;
    @SerializedName("input_time")
    private String inputtime;

    public String getIssueid() {
        return issueid;
    }

    public void setIssueid(String issueid) {
        this.issueid = issueid;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getInputtime() {
        return inputtime;
    }

    public void setInputtime(String inputtime) {
        this.inputtime = inputtime;
    }
}