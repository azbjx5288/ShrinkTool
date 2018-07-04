package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

/**
 * 012路比
 * Created by Alashi on 2016/5/30.
 */
public class ModularRationRuleItem extends RuleObject {
    private int[] keys;
    public ModularRationRuleItem(Path path) {
        super(path);
        String key = path.getSuffix();
        keys = new int[key.length()];
        for (int i = 0; i < 3; i++) {
            keys[i] = key.charAt(i) - 48;
        }
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        int[] newKeys = new int[3];
        for (int num: numbers) {
            newKeys[num % 3]++;
        }

        for (int i = 0; i < 3; i++) {
            if (newKeys[i] == keys[i]) {
                continue;
            }
            return false;
        }
        return true;
    }
}
