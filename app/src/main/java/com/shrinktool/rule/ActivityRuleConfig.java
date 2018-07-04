package com.shrinktool.rule;

import com.shrinktool.material.RefiningCart;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置过滤条件设置里面显示的“RuleSet”
 * Created by Alashi on 2016/10/25.
 */

public final class ActivityRuleConfig {
    public static class Info {
        public String rule;
        public int buttonCount;

        public Info(String rule, int viewType) {
            this.rule = rule;
            this.buttonCount = viewType;
        }
    }

    public static List<Info> getRuleType(RefiningCart refiningCart, int numberType) {
        switch (numberType){
            case RuleSet.TYPE_1_11_SDRX5:
                return getRuleTypeSDRX5();
            case RuleSet.TYPE_0_9_SXZX:
                return getRuleType();
            case RuleSet.TYPE_SSQ:
                return getRuleTypeSSQ(refiningCart);
            case RuleSet.TYPE_WXZX:
                return getRuleTypeWXZX();
            default:
                return null;
        }
    }

    /** 双色球的过滤配置 */
    private static List<Info> getRuleTypeSSQ(RefiningCart refiningCart) {
        List<Info> ruleType = new ArrayList<>();
        ruleType.add(new Info("/ssq/oddEvenRatio", 4));
        ruleType.add(new Info("/ssq/sizeRatio", 4));
        ruleType.add(new Info("/ssq/primeRatio", 4));
        ruleType.add(new Info("/ssq/modularRatio", 5));
        ruleType.add(new Info("/ssq/rangeRatio", 5));
        ruleType.add(new Info("/ssq/sum", 4));
        ruleType.add(new Info("/ssq/ac", 6));
        ruleType.add(new Info("/ssq/mantissa", 5));
        ruleType.add(new Info("/ssq/span", 6));
        if (refiningCart.getAssist() != null) {
            ruleType.add(new Info("/ssq/lastRepeat", 4));
        }
        ruleType.add(new Info("/ssq/repeat", 4));
        ruleType.add(new Info("/ssq/blueOddEven", 4));
        ruleType.add(new Info("/ssq/blueSize", 4));

        return ruleType;
    }

    /** 重庆时时彩“三星直选”的规则配置 */
    private static List<Info> getRuleType() {
        List<Info> ruleType = new ArrayList<>();
        //大小
        ruleType.add(new Info("/sxzx/sizeRatio", 4));
        ruleType.add(new Info("/sxzx/sizeShape", 4));
        //奇偶
        ruleType.add(new Info("/sxzx/oddEvenRatio", 4));
        ruleType.add(new Info("/sxzx/oddEvenShape", 4));
        //质合
        ruleType.add(new Info("/sxzx/primeRatio", 4));
        ruleType.add(new Info("/sxzx/primeShape", 4));
        //012路
        ruleType.add(new Info("/sxzx/modularRatio", 5));
        ruleType.add(new Info("/sxzx/modularShape", 5));
        //和值
        ruleType.add(new Info("/sxzx/sum", 6));
        ruleType.add(new Info("/sxzx/sumMantissa", 6));
        //跨度
        ruleType.add(new Info("/sxzx/span", 6));

        return ruleType;
    }

    /** 山东11选5的“任选五中五” 的规则配置 */
    private static List<Info> getRuleTypeSDRX5() {
        List<Info> ruleType = new ArrayList<>();
        ruleType.add(new Info("/sdrx5/oddEvenRatio", 5));
        ruleType.add(new Info("/sdrx5/sizeRatio", 5));
        ruleType.add(new Info("/sdrx5/primeRatio", 5));
        ruleType.add(new Info("/sdrx5/modular_0", 6));
        ruleType.add(new Info("/sdrx5/modular_1", 6));
        ruleType.add(new Info("/sdrx5/modular_2", 6));
        ruleType.add(new Info("/sdrx5/sum", 6));
        ruleType.add(new Info("/sdrx5/sumMantissa", 6));
        ruleType.add(new Info("/sdrx5/span", 6));
        ruleType.add(new Info("/sdrx5/min", 6));
        ruleType.add(new Info("/sdrx5/max", 6));
        ruleType.add(new Info("/sdrx5/ac", 6));

        return ruleType;
    }

    public static List<Info> getRuleTypeWXZX() {
        List<Info> ruleType = new ArrayList<>();
        //奇偶
        ruleType.add(new Info("/wxzx/oddEvenRatio", 4));
        //ruleType.add("/wxzx/oddEvenShape");
        //大小
        ruleType.add(new Info("/wxzx/sizeRatio", 4));
        //ruleType.add("/wxzx/sizeShape");

        //质合
        ruleType.add(new Info("/wxzx/primeRatio", 4));
        //ruleType.add("/wxzx/primeShape");
        //012路
        ruleType.add(new Info("/wxzx/modularRatio", 5));
        //ruleType.add("/wxzx/modularShape");
        //和值
        ruleType.add(new Info("/wxzx/sum", 6));
        ruleType.add(new Info("/wxzx/sumMantissa", 6));
        //跨度
        ruleType.add(new Info("/wxzx/span", 6));
        return ruleType;
    }
}
