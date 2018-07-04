package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 胆码
 * Created by Alashi on 2016/5/30.
 */
public class SpecialNumberRuleSet extends RuleSet {
    private int type = RuleSet.TYPE_0_9_SXZX;
    public SpecialNumberRuleSet(Path path, int type) {
        super(path);
        this.type = type;
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        return null;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new SpecialNumberRuleItem(path, type);
    }

    @Override
    public String getName() {
        return "胆码";
    }

    //***0_1_2_A
    public String createPathByNumber(int count, ArrayList<Integer> numbers) {
        String key = String.valueOf(count);
        if (type == RuleSet.TYPE_SSQ) {
            for (int num : numbers) {
                key += "_";
                key += num;
            }
        } else {
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
        }
        return getPath().getChild(key).toString();
    }
}
