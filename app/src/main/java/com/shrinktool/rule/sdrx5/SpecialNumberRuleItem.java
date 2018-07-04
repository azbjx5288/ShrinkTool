package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

import java.util.Arrays;

/**
 * 胆码，//0_1_2_A;第一位表示“胆码出现个数”，后面的数字是选中的胆码，10用A，11用B
 * Created by Alashi on 2016/5/30.
 */
public class SpecialNumberRuleItem extends RuleObject {
    private static final String TAG = "SpecialNumberRuleItem";

    private int numberCount;
    private int[] specialNumbers;

    public SpecialNumberRuleItem(Path path) {
        super(path);
        String key = path.getSuffix();
        numberCount = key.charAt(0) - 48;
        specialNumbers = new int[key.length() / 2];
        for (int i = 2, j = 0; i < key.length(); i += 2, j++) {
            char k = key.charAt(i);
            if (k == 'A') {
                specialNumbers[j] = 10;
            } else if (k == 'B') {
                specialNumbers[j] = 11;
            } else {
                specialNumbers[j] = k - 48;
            }
        }
    }

    public int getNumberCount() {
        return numberCount;
    }

    public int[] getSpecialNumbers() {
        return specialNumbers;
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        //Log.d(TAG, "apply: " + path.toString() + " -> " +Arrays.toString(numbers));
        int find = 0;

        for (int number : numbers) {
            if (Arrays.binarySearch(specialNumbers, number) >= 0) {
                find++;
            }
        }

        return find == numberCount;
    }
}
