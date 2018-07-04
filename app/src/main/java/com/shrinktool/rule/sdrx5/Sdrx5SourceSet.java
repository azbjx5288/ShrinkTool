package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 山东11选5的“任选五中五” 的规则顶级Set
 * Created by Alashi on 2016/10/25.
 */

public class Sdrx5SourceSet extends RuleSet {
    public static final String PATH_TAG = "/sdrx5";
    public Sdrx5SourceSet(Path path) {
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

    private RuleSet createSubSet(String pathTag) {
        switch (pathTag) {
            case "specialNumber":
                return new SpecialNumberRuleSet(path.getChild(pathTag));
            case "oddEvenRatio":
                return new OddEvenRationRuleSet(path.getChild(pathTag));
            case "sizeRatio":
                return new SizeRatioRuleSet(path.getChild(pathTag));
            case "primeRatio":
                return new PrimeRatioRuleSet(path.getChild(pathTag));
            case "sum":
                return new SumRuleSet(path.getChild(pathTag));
            case "sumMantissa":
                return new SumMantissaRuleSet(path.getChild(pathTag));
            case "span":
                return new SpanRuleSet(path.getChild(pathTag));
            case "min":
                return new MinNumberRuleSet(path.getChild(pathTag));
            case "max":
                return new MaxNumberRuleSet(path.getChild(pathTag));
            case "modular_0":
                return new ModularRuleSet(path.getChild(pathTag));
            case "modular_1":
                return new ModularRuleSet(path.getChild(pathTag));
            case "modular_2":
                return new ModularRuleSet(path.getChild(pathTag));
            case "ac":
                return new AcRuleSet(path.getChild(pathTag));
        }
        return null;
    }
    
    @Override
    public String getName() {
        return null;
    }
}
