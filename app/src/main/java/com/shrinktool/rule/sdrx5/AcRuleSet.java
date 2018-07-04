package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * AC值
 * Created by Alashi on 2016/10/24.
 */

public class AcRuleSet extends RuleSet {
    private static final String TAG = "AcRuleSet";

    public AcRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{top + UNLIMITED, "不限"});
        for (int i = 0; i <= 5; i++) {
            list.add(new String[]{top + i, String.valueOf(i)});
        }
        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return "AC值";
    }

    public static class RuleItem extends RuleObject {
        private int ac;

        public RuleItem(Path path) {
            super(path);
            ac = Integer.parseInt(path.getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            boolean[] match = new boolean[11];
            Arrays.fill(match, false);
            for (int i = 0; i < numbers.length; i++) {
                for (int j = numbers.length - 1; j >= i; j--) {
                    if (j == i) {
                        continue;
                    }
                    int index = Math.abs(numbers[j] - numbers[i]);
                    //Log.d(TAG, "apply: j,i=" + j +"," + i + " -> " + numbers[j] + "-" + numbers[i] + " = " + index);
                    match[index] = true;
                }
            }

            int matchCount = 0;
            for (boolean m : match) {
                if (m) {
                    matchCount++;
                }
            }
            //Log.d(TAG, "apply: matchCount = " + matchCount);
            return matchCount - 4 == ac;
        }
    }
}
