package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

import java.util.Arrays;

/**
 * 质合形态
 * Created by Alashi on 2016/5/30.
 */
public class PrimeShapeRuleItem extends RuleObject {

    private static final int[] PRIME = new int[]{1,2,3,5,7,11};
    private static final int[] COMPOSITE = new int[]{0,4,6,8,9,10};

    private boolean[] keys;

    public PrimeShapeRuleItem(Path path) {
        super(path);
        String key = path.getSuffix();
        keys = new boolean[key.length()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = key.charAt(i) == 'p';
        }
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        for (int i = 0; i < numbers.length; i++) {
            if (keys[i] == Arrays.binarySearch(PRIME, numbers[i]) > -1) {
                continue;
            }
            return false;
        }
        return true;
    }
}
