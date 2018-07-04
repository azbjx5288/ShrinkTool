package com.shrinktool.rule.ssq;

import android.support.annotation.Keep;

/**
 * 双色球过滤需要的辅助信息
 * Created by Alashi on 2016/8/10.
 */
@Keep
public class SsqAssistInfo {
    private int[] lastIssueCode;

    public int[] getLastIssueCode() {
        return lastIssueCode;
    }

    public void setLastIssueCode(int[] lastIssueCode) {
        this.lastIssueCode = lastIssueCode;
    }
}
