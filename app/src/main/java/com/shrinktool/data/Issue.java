package com.shrinktool.data;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;

/**
 * 开奖数据
 * Created by Alashi on 2016/8/5.
 */
public class Issue {

    /**
     * issue_id : 1655348
     * issue : 20160803-089
     * code : 11948
     */

    @SerializedName("issue_id")
    private String issueId;
    private String issue;
    private String code;
    private Detail detail;
    //input_time:"2017-01-04 13:32:44"
    @SerializedName("input_time")
    private String inputTime;
    private LinkedHashMap<String, int[]> missCode;

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LinkedHashMap<String, int[]> getMissCode() {
        return missCode;
    }

    public Detail getDetail() {
        return detail;
    }

    public String getInputTime() {
        return inputTime;
    }

    @Keep
    public static class Detail {
        private LinkedHashMap<String, String> header;
        private LinkedHashMap<String, String[]> body;

        public LinkedHashMap<String, String> getHeader() {
            return header;
        }

        public LinkedHashMap<String, String[]> getBody() {
            return body;
        }
    }
}
