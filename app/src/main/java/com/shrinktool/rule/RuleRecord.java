package com.shrinktool.rule;

import android.support.annotation.Keep;

import java.util.ArrayList;

/**
 * 过滤方案，用于保存或恢复选中的“规则”
 * Created by Alashi on 2016/6/1.
 */
@Keep
public class RuleRecord {
    /** 记录ID */
    private int id;
    /** 记录名词 */
    private String name;
    /** 彩种ID */
    private int lotteryId;
    /** 玩法ID */
    private int methodId;
    /** 期号 */
    private String issue;
    /** 胆码 */
    private ArrayList<Special> special;
    /** 已选中的“规则” */
    private ArrayList<String> ruleList;
    /** 辅助信息，不同的记录可能需要不同的信息，使用时再格式化成具体对象 */
    private String assist;

    /** 胆码 */
    @Keep
    public static class Special{
        /** 胆码的数字 */
        private ArrayList<Integer> numbers;
        /** 胆码出现个数 */
        private ArrayList<Integer> count;

        public ArrayList<Integer> getNumbers() {
            return numbers;
        }

        public void setNumbers(ArrayList<Integer> numbers) {
            this.numbers = numbers;
        }

        public ArrayList<Integer> getCount() {
            return count;
        }

        public void setCount(ArrayList<Integer> count) {
            this.count = count;
        }
    }

    public ArrayList<Special> getSpecial() {
        return special;
    }

    public void setSpecial(ArrayList<Special> special) {
        this.special = special;
    }

    public ArrayList<String> getRuleList() {
        return ruleList;
    }

    public void setRuleList(ArrayList<String> ruleList) {
        this.ruleList = ruleList;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssist() {
        return assist;
    }

    public void setAssist(String assist) {
        this.assist = assist;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
