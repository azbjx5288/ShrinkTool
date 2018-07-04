package com.shrinktool.rule;

/**
 * 任何一个规则集合里面的“不限”
 * Created by Alashi on 2016/8/9.
 */
public class UnlimitedRuleItem extends RuleObject {
    public UnlimitedRuleItem(Path path) {
        super(path);
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        return true;
    }
}
