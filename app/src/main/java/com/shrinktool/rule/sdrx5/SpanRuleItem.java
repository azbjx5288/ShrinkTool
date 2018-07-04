package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

/**
 * 跨度
 * Created by Alashi on 2016/5/30.
 */
public class SpanRuleItem extends RuleObject {
    private int key;
    public SpanRuleItem(Path path) {
        super(path);
        key = Integer.valueOf(path.getSuffix());
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int num : numbers) {
            max = num > max? num : max;
            min = num < min? num : min;
        }
        return max - min == key;
    }
}
