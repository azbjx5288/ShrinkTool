package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

/**
 * 和值尾数
 * Created by Alashi on 2016/5/30.
 */
public class SumMantissaRuleItem extends RuleObject {
    private int key;

    public SumMantissaRuleItem(Path path) {
        super(path);
        key = Integer.valueOf(path.getSuffix());
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum % 10 == key;
    }
}
