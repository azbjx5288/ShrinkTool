package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 大小比
 * Created by Alashi on 2016/5/26.
 */
public class SizeRatioRuleSet extends RuleSet {

    public SizeRatioRuleSet(Path path) {
        super(path);
    }

    @Override
    public String getName() {
        return "大小比";
    }

    @Override
    public String getHint() {
        return "(01-05为小数，06-11为大数)";
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
        String[] strings = path.getSuffix().split("_");
        return new SizeRatioRuleItem(path, 6,
                Integer.valueOf (strings[0]), Integer.valueOf(strings[1]));

    }
}
