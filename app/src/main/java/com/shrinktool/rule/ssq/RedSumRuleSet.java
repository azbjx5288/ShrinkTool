package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 红球和值
 * 共12项
 * 不限,21-49,50-59,60-69,70-79,80-89,90-99,100-109,110-119,120-129,130-139,140-183
 * Created by Alashi on 2016/8/9.
 */
public class RedSumRuleSet extends RuleSet{
    public RedSumRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});
        String[] names = new String[]{"21-49","50-59","60-69","70-79","80-89","90-99",
                "100-109","110-119","120-129", "130-139","140-183"};
        for (String name : names) {
            list.add(new String[]{top + name.replace("-", "_"), name});
        }
        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return "红球和值";
    }

    public static class RuleItem extends RuleObject{
        private int min;
        private int max;
        public RuleItem(Path path) {
            super(path);
            String key = path.getSuffix();
            String[] codes = key.split("_");
            min = Integer.valueOf(codes[0]);
            max = Integer.valueOf(codes[1]);
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            int sum = 0;
            for (int i = 0; i < 6; i++) {
                sum += numbers[i];
            }
            return sum >= min && sum <= max;
        }
    }
}
