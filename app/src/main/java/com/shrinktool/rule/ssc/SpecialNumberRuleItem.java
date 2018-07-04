package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.Arrays;

/**
 * 胆码，//0_1_2_A;第一位表示“胆码出现个数”，后面的数字是选中的胆码，10用A，11用B
 * Created by Alashi on 2016/5/30.
 */
public class SpecialNumberRuleItem extends RuleObject {
    private static final String TAG = "SpecialNumberRuleItem";

    private int numberCount;
    private int[] specialNumbers;
    private int type;

    public SpecialNumberRuleItem(Path path, int type) {
        super(path);
        this.type = type;
        String key = path.getSuffix();
        numberCount = key.charAt(0) - 48;
        if (type == RuleSet.TYPE_SSQ) {
            String[] code = key.split("_");
            specialNumbers = new int[code.length - 1];
            for (int i = 1; i < code.length; i++) {
                specialNumbers[i - 1] = Integer.parseInt(code[i]);
            }
        } else {
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
    }

    public int getNumberCount() {
        return numberCount;
    }

    public int[] getSpecialNumbers() {
        return specialNumbers;
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        int find = 0;

        if (type == RuleSet.TYPE_SSQ) {
            for (int i = 0; i < 6; i++) {
                if (Arrays.binarySearch(specialNumbers, numbers[i]) >= 0) {
                    find++;
                }
            }
        } else {
            //需要将原数组去重
            boolean[] x = new boolean[12];
            Arrays.fill(x, false);
            for (int num : numbers) {
                x[num] = true;
            }

            for (int i = 0; i < x.length; i++) {
                if (x[i] && Arrays.binarySearch(specialNumbers, i) >= 0) {
                    find++;
                }
            }
        }
        return find == numberCount;
    }
}
