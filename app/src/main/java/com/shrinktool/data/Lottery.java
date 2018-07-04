package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Alashi on 2016/1/5.
 */
public class Lottery implements Serializable{


    /**
     * lottery_id : 1
     * name : CQSSC
     * cname : 重庆时时彩
     * lottery_type : 1
     * property_id : 1
     * yearly_start_closed : 2016-02-07
     * yearly_end_closed : 2016-02-13
     * status : 8
     * is_available : 0
     * stop_reason : 年度休市时间为2016-02-07——2016-02-13
     */

    @SerializedName("lottery_id")
    private int lotteryId;
    @SerializedName("name")
    private String name;
    @SerializedName("cname")
    private String cname;
    @SerializedName("lottery_type")
    private int lotteryType;
    @SerializedName("property_id")
    private int propertyId;
    @SerializedName("yearly_start_closed")
    private String yearlyStartClosed;
    @SerializedName("yearly_end_closed")
    private String yearlyEndClosed;
    @SerializedName("status")
    private int status;
    @SerializedName("is_available")
    private boolean isAvailable = true;
    @SerializedName("stop_reason")
    private String stopReason;

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

    public int getLotteryType() {
        return lotteryType;
    }

    public void setLotteryType(int lotteryType) {
        this.lotteryType = lotteryType;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public String getYearlyStartClosed() {
        return yearlyStartClosed;
    }

    public void setYearlyStartClosed(String yearlyStartClosed) {
        this.yearlyStartClosed = yearlyStartClosed;
    }

    public String getYearlyEndClosed() {
        return yearlyEndClosed;
    }

    public void setYearlyEndClosed(String yearlyEndClosed) {
        this.yearlyEndClosed = yearlyEndClosed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getStopReason() {
        return stopReason;
    }

    public void setStopReason(String stopReason) {
        this.stopReason = stopReason;
    }
}
