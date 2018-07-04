package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 奇偶形态
 * Created by Alashi on 2016/5/30.
 */
public class OddEvenShapeRuleSet extends RuleSet {
    public OddEvenShapeRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        return buildRuleList(this, numberCount, 'o', "奇", 'e', "偶");
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new OddEvenShapeRuleItem(path);
    }

    @Override
    public String getName() {
        return "奇偶形态";
    }
}
