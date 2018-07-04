package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 胆码
 * Created by Alashi on 2016/5/30.
 */
public class SpecialNumberRuleSet extends RuleSet {
    public SpecialNumberRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        return null;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new SpecialNumberRuleItem(path);
    }

    @Override
    public String getName() {
        return "胆码";
    }

    //***0_1_2_A
    public String createPathByNumber(int count, ArrayList<Integer> numbers) {
        String key = String.valueOf(count);
        for (int num : numbers) {
            key += "_";
            if (num == 10) {
                key += "A";
            } else if (num == 11) {
                key += "B";
            } else {
                key += num;
            }
        }

        return getPath().getChild(key).toString();
    }
}
