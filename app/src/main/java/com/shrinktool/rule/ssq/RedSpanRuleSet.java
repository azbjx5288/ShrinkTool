package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 红球首尾跨度
 * 不限，5，6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32
 * Created by Alashi on 2016/8/9.
 */
public class RedSpanRuleSet extends RuleSet {
    public RedSpanRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});
        for (int i = 5; i < 33; i++) {
            list.add(new String[]{ top + i, String.valueOf(i)});
        }
        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return "红球首尾跨度";
    }

    public static class RuleItem extends RuleObject {
        private int key;

        public RuleItem(Path path) {
            super(path);
            key = Integer.parseInt(path.getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            return key == numbers[5] - numbers[0];
        }
    }
}
