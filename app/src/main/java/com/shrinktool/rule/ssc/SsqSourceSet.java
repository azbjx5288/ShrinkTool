package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;
import com.shrinktool.rule.ssq.BlueOddEvenRuleSet;
import com.shrinktool.rule.ssq.BlueSizeRuleSet;
import com.shrinktool.rule.ssq.LastRepeatRuleSet;
import com.shrinktool.rule.ssq.RedAcRuleSet;
import com.shrinktool.rule.ssq.RedMantissaRuleSet;
import com.shrinktool.rule.ssq.RedModularRationRuleSet;
import com.shrinktool.rule.ssq.RedOddEvenRationRuleSet;
import com.shrinktool.rule.ssq.RedPrimeRatioRuleSet;
import com.shrinktool.rule.ssq.RedRangeRationRuleSet;
import com.shrinktool.rule.ssq.RedSizeRatioRuleSet;
import com.shrinktool.rule.ssq.RedSpanRuleSet;
import com.shrinktool.rule.ssq.RedSumRuleSet;
import com.shrinktool.rule.ssq.RepeatRuleSet;

import java.util.ArrayList;

/**
 * 双色球的规则顶级Set
 * Created by Alashi on 2016/10/25.
 */

public class SsqSourceSet extends RuleSet {
    public static final String PATH_TAG = "/ssq";
    public SsqSourceSet(Path path) {
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
        switch (pathTag) {
            case "specialNumber":
                return new SpecialNumberRuleSet(path.getChild(pathTag), RuleSet.TYPE_SSQ);
            case "oddEvenRatio":
                return new RedOddEvenRationRuleSet(path.getChild(pathTag));
            case "sizeRatio":
                return new RedSizeRatioRuleSet(path.getChild(pathTag));
            case "primeRatio":
                return new RedPrimeRatioRuleSet(path.getChild(pathTag));
            case "modularRatio":
                return new RedModularRationRuleSet(path.getChild(pathTag));
            case "rangeRatio":
                return new RedRangeRationRuleSet(path.getChild(pathTag));
            case "sum":
                return new RedSumRuleSet(path.getChild(pathTag));
            case "ac":
                return new RedAcRuleSet(path.getChild(pathTag));
            case "mantissa":
                return new RedMantissaRuleSet(path.getChild(pathTag));
            case "span":
                return new RedSpanRuleSet(path.getChild(pathTag));
            case "repeat":
                return new RepeatRuleSet(path.getChild(pathTag));
            case "blueOddEven":
                return new BlueOddEvenRuleSet(path.getChild(pathTag));
            case "blueSize":
                return new BlueSizeRuleSet(path.getChild(pathTag));
            case "lastRepeat":
                return new LastRepeatRuleSet(path.getChild(pathTag));
        }
        return null;
    }
}
