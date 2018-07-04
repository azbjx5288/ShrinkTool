package com.shrinktool.data;

/**
 * 选号界面的期号相关
 * Created by Alashi on 2016/8/5.
 */
public class GameIssueInfo {
    private Issue lastIssueInfo;
    private IssueInfoEntity issueInfo;
    private String serverTime;

    public Issue getLastIssueInfo() {
        return lastIssueInfo;
    }

    public IssueInfoEntity getIssueInfo() {
        return issueInfo;
    }

    public String getServerTime() {
        return serverTime;
    }
}