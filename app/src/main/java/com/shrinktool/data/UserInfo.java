package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

/**
 * Created by Alashi on 2016/1/7.
 */
public class UserInfo {
    @SerializedName("user_id")
    private int userId;
    @SerializedName("username")
    private String userName;
    private int level;
    @SerializedName("parent_id")
    private int parentId;
    @SerializedName("top_id")
    private int topId;
    @SerializedName("real_name")
    private String realName;
    @SerializedName("nick_name")
    private String nickName;
    private Double balance;
    @SerializedName("is_test")
    private boolean isTest;
    @SerializedName("group_id")
    private int groupId;
    @SerializedName("last_ip")
    private String lastIp;
    @SerializedName("last_time")
    private Timestamp lastTime;
    private int status;
    private String mobile;
    private String headimg;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getTopId() {
        return topId;
    }

    public void setTopId(int topId) {
        this.topId = topId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public Timestamp getLastTime() {
        return lastTime;
    }

    public void setLastTime(Timestamp lastTime) {
        this.lastTime = lastTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }
}
