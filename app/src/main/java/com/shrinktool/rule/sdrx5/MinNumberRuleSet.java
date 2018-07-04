package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 最小号码
 * Created by Alashi on 2016/10/24.
 */

public class MinNumberRuleSet extends RuleSet{
    public MinNumberRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});
        for (int i = 1; i <= 7; i++) {
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
        return "最小号码";
    }

    public static class RuleItem extends RuleObject {
        private int number;

        public RuleItem(Path path) {
            super(path);
            number = Integer.parseInt(path.getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            int min = Integer.MAX_VALUE;
            for (int number1 : numbers) {
                min = Math.min(min, number1);
            }
            return min == number;
        }
    }
}
