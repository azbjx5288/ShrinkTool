package com.shrinktool.rule.wxzx;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;
import com.shrinktool.rule.ssc.ModularRationRuleSet;
import com.shrinktool.rule.ssc.SpanRuleSet;
import com.shrinktool.rule.ssc.SpecialNumberRuleSet;
import com.shrinktool.rule.ssc.SumMantissaRuleSet;
import com.shrinktool.rule.ssc.SumRuleSet;

import java.util.ArrayList;

/**
 * 排列5“五星直选”的规则顶级Set
 * Created by Alashi on 2016/10/25.
 */

public class WxzxSourceSet extends RuleSet{
    public static final String PATH_TAG = "/wxzx";

    public WxzxSourceSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        return null;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return createSubSet(path.split()[1]);
    }

    @Override
    public String getName() {
        return null;
    }

    private RuleSet createSubSet(String pathTag) {
        switch (pathTag){
            case "specialNumber":
                return new SpecialNumberRuleSet(path.getChild(pathTag), RuleSet.TYPE_0_9_SXZX);
            case "oddEvenRatio":
                return new OddEvenRationRuleSet(path.getChild(pathTag));
            case "sizeRatio":
                return new SizeRatioRuleSet(path.getChild(pathTag), RuleSet.TYPE_0_9_SXZX);
            case "primeRatio":
                return new PrimeRatioRuleSet(path.getChild(pathTag));
            case "modularRatio":
                return new ModularRationRuleSet(path.getChild(pathTag));
            case "sum":
                return new SumRuleSet(path.getChild(pathTag), RuleSet.TYPE_0_9_SXZX);
            case "sumMantissa":
                return new SumMantissaRuleSet(path.getChild(pathTag));
            case "span":
                return new SpanRuleSet(path.getChild(pathTag), RuleSet.TYPE_0_9_SXZX);
        }
        return null;
    }
}
