package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

/**
 * 开奖号码
 * Created by ACE-PC on 2016/1/26.
 */
public class LotteriesHistory {


    /**
     * cname : 天津时时彩
     * issue_id : 263759
     * issue : 20160206-083
     * code : 75119
     */

    @SerializedName("lottery_id")
    private int lotteryId;
    @SerializedName("cname")
    private String cname;
    @SerializedName("issue_id")
    private String issueId;
    @SerializedName("issue")
    private String issue;
    @SerializedName("code")
    private String code;

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCname() {
        return cname;
    }

    public String getIssueId() {
        return issueId;
    }

    public String getIssue() {
        return issue;
    }

    public String getCode() {
        return code;
    }

    public int getLotteryId() {
        return lotteryId;
    }
}
