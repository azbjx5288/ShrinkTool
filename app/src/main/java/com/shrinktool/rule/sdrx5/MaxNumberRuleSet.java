package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 最大号码
 * Created by Alashi on 2016/10/24.
 */

public class MaxNumberRuleSet extends RuleSet{
    public MaxNumberRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});
        for (int i = 5; i <= 11; i++) {
            list.add(new String[]{ top + i, String.format("%02d", i)});
        }
        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return "最大号码";
    }

    public static class RuleItem extends RuleObject {
        private int number;

        public RuleItem(Path path) {
            super(path);
            number = Integer.parseInt(path.getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            int max = Integer.MIN_VALUE;
            for (int number1 : numbers) {
                max = Math.max(max, number1);
            }
            return max == number;
        }
    }
}
