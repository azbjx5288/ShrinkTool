package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

/**
 * 012路形态
 * Created by Alashi on 2016/5/30.
 */
public class ModularShapeRuleItem extends RuleObject {
    private int[] keys;

    public ModularShapeRuleItem(Path path) {
        super(path);
        String key = path.getSuffix();
        keys = new int[key.length()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = key.charAt(i) - 48;
        }
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        for (int i = 0; i < numbers.length; i++) {
            if (keys[i] == numbers[i] % 3) {
                continue;
            }
            return false;
        }
        return true;
    }
}
