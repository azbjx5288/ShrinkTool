package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 奇偶比
 * Created by Alashi on 2016/5/30.
 */
public class OddEvenRationRuleSet extends RuleSet {
    public OddEvenRationRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        ArrayList<String[]> map = new ArrayList<>();
        map.add(new String[]{createChild(UNLIMITED), "不限"});
        for (int i = 0; i < 6; i++) {
            map.add(new String[]{createChild(i + "_" + (5 - i)),
                    i + ":" + (5 - i)});
        }
        return map;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        switch (path.getSuffix()) {
            case "allOdd":
                return new OddEvenRationRuleItem(path, OddEvenRationRuleItem.ALL_ODD);
            case "allEven":
                return new OddEvenRationRuleItem(path, OddEvenRationRuleItem.ALL_EVEN);
            default:
                String[] strings = path.getSuffix().split("_");
                return new OddEvenRationRuleItem(path, OddEvenRationRuleItem.OTHER,
                        Integer.valueOf (strings[0]), Integer.valueOf(strings[1]));
        }
    }

    @Override
    public String getName() {
        return "奇偶比";
    }
}
