package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 质合形态
 * Created by Alashi on 2016/5/30.
 */
public class PrimeShapeRuleSet extends RuleSet {
    public PrimeShapeRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        return buildRuleList(this, numberCount, 'p', "质", 'c', "合");
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new PrimeShapeRuleItem(path);
    }

    @Override
    public String getName() {
        return "质合形态";
    }
}
