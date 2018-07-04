package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

/**
 * 大小形态, 如“大大大”，“大大小”
 * Created by Alashi on 2016/5/27.
 */
public class SizeShapeRuleItem extends RuleObject {

    private int divideNum;
    private boolean[] keys;

    public SizeShapeRuleItem(Path path, int divideNum) {
        super(path);
        this.divideNum = divideNum;
        String key = path.getSuffix();
        keys = new boolean[key.length()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = key.charAt(i) == 'b';
        }
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        for (int i = 0; i < numbers.length; i++) {
            if (keys[i] && numbers[i] >= divideNum) {
                continue;
            }
            if (!keys[i] && numbers[i] < divideNum) {
                continue;
            }
            return false;
        }
        return true;
    }
}
