package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 跨度<br>
 *     跨度是指每注中最大与最小的差值。如：开奖号码“345”，跨度等于5-3=2.
 * Created by Alashi on 2016/5/30.
 */
public class SpanRuleSet extends RuleSet {
    private int type;

    public SpanRuleSet(Path path, int type) {
        super(path);
        this.type = type;
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        int maxNumber = type == TYPE_0_9_SXZX ? 10 : 11;
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});
        for (int i = 0; i < maxNumber; i++) {
            list.add(new String[]{ top + i, String.valueOf(i)});
        }
        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new SpanRuleItem(path);
    }

    @Override
    public String getName() {
        return "跨度";
    }
}
