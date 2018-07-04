package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 和值<br>
 *     和值是指每注号码的3个数字之和。如：开奖号码“345”，和值等于3+4+5=12.
 和值共有28个数字，0—27
 * Created by Alashi on 2016/5/30.
 */
public class SumRuleSet extends RuleSet {
    private int type;
    public SumRuleSet(Path path, int type) {
        super(path);
        this.type = type;
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        int maxNumber = type == TYPE_0_9_SXZX ? 9 * numberCount: 11 * numberCount;
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});
        for (int i = 0; i <= maxNumber; i++) {
            list.add(new String[]{ top + i, String.valueOf(i)});
        }
        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new SumRuleItem(path);
    }

    @Override
    public String getName() {
        return "和值";
    }
}
