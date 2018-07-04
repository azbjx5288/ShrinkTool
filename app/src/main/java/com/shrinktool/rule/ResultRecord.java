package com.shrinktool.rule;

import android.support.annotation.Keep;

import com.shrinktool.data.Lottery;
import com.shrinktool.material.Ticket;

/**
 * 保存过滤的结果
 * Created by Alashi on 2016/6/3.
 */
@Keep
public class ResultRecord {
    public static final int VERSION = 1;

    /** 彩种 */
    private Lottery lottery;
    /** 选号情况 */
    private Ticket ticket;
    /** 过滤规则 */
    private RuleRecord ruleRecord;
    /** 过滤结果文件，过滤后结果可能太多，写在文件 */
    private String resultFile;
    /** 本期开奖号码 */
    private int[] winCode;
    /** 是否中奖 */
    private boolean isWin;
    /** 方案金额 */
    private int money;
    /** 中奖金额 */
    private int winMoney;
    /** 是否离线 */
    private boolean isOnline;

    public Lottery getLottery() {
        return lottery;
    }

    public void setLottery(Lottery lottery) {
        this.lottery = lottery;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public RuleRecord getRuleRecord() {
        return ruleRecord;
    }

    public void setRuleRecord(RuleRecord ruleRecord) {
        this.ruleRecord = ruleRecord;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    public int[] getWinCode() {
        return winCode;
    }

    public void setWinCode(int[] winCode) {
        this.winCode = winCode;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getWinMoney() {
        return winMoney;
    }

    public void setWinMoney(int winMoney) {
        this.winMoney = winMoney;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
