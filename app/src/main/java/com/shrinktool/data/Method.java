package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

/**
 * 玩法信息
 * Created by ACE-PC on 2016/1/22.
 */
public class Method {
    @SerializedName("method_id")
    private int methodId;
    @SerializedName("lottery_id")
    private int lotteryId;
    @SerializedName("name")
    private String name;
    @SerializedName("cname")
    private String cname;
    @SerializedName("mg_id")
    private int mgId;
    @SerializedName("mg_name")
    private String mgName;
    @SerializedName("can_input")
    private boolean canInput;
    @SerializedName("levels")
    private int levels;

    public Method(int methodId, int lotteryId, String name, String cname, int mgId, String mgName, boolean canInput, int levels) {
        this.methodId = methodId;
        this.lotteryId = lotteryId;
        this.name = name;
        this.cname = cname;
        this.mgId = mgId;
        this.mgName = mgName;
        this.canInput = canInput;
        this.levels = levels;
    }

    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public int getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public int getMgId() {
        return mgId;
    }

    public void setMgId(int mgId) {
        this.mgId = mgId;
    }

    public String getMgName() {
        return mgName;
    }

    public void setMgName(String mgName) {
        this.mgName = mgName;
    }

    public boolean isCanInput() {
        return canInput;
    }

    public void setCanInput(boolean canInput) {
        this.canInput = canInput;
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }
}
