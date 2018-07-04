package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 红球尾数个数
 * 不限，3尾，4尾，5尾，6尾
 * Created by Alashi on 2016/8/9.
 */
public class RedMantissaRuleSet extends RuleSet {
    public RedMantissaRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});
        for (int i = 3; i <= 6; i++) {
            list.add(new String[]{ top + i, i + "尾"});
        }
        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return "红球尾数个数";
    }

    public static class RuleItem extends RuleObject {
        private int count;
        public RuleItem(Path path) {
            super(path);
            count = Integer.parseInt(path.getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            boolean[] match = new boolean[10];
            Arrays.fill(match, false);
            for (int i = 0; i < 6; i++) {
                if (numbers[i] < 10) {
                    match[numbers[i]] = true;
                } else if (numbers[i] < 20) {
                    match[numbers[i] - 10] = true;
                } else if (numbers[i] < 30) {
                    match[numbers[i] - 20] = true;
                } else {
                    match[numbers[i] - 30] = true;
                }
            }

            int matchCount = 0;
            for (boolean m: match) {
                if (m) {
                    matchCount++;
                }
            }

            return matchCount == count;
        }
    }
}
