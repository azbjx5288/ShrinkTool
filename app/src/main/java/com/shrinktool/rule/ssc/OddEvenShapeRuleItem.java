package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

/**
 * 奇偶形态
 * Created by Alashi on 2016/5/30.
 */
public class OddEvenShapeRuleItem extends RuleObject {

    private boolean[] keys;

    public OddEvenShapeRuleItem(Path path) {
        super(path);
        String key = path.getSuffix();
        keys = new boolean[key.length()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = key.charAt(i) == 'o';
        }
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        for (int i = 0; i < numbers.length; i++) {
            if (keys[i] && (numbers[i] & 1) != 0) {
                continue;
            }
            if (!keys[i] && (numbers[i] & 1) == 0) {
                continue;
            }
            return false;
        }
        return true;
    }
}
