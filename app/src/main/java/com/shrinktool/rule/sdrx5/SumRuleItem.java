package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

/**
 * 和值
 * Created by Alashi on 2016/5/30.
 */
public class SumRuleItem extends RuleObject {
    private int sum;
    public SumRuleItem(Path path) {
        super(path);
        sum = Integer.valueOf(path.getSuffix());
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        int nowSum = 0;
        for (int num : numbers) {
            nowSum += num;
        }
        return nowSum == sum;
    }
}
