package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 红蓝是否重复
 * 不限，重复，不重复
 * Created by Alashi on 2016/8/9.
 */
public class RepeatRuleSet extends RuleSet{
    public RepeatRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});
        list.add(new String[]{ top + "yes", "重复"});
        list.add(new String[]{ top + "no", "不重复"});
        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return "红蓝是否重复";
    }

    public static class RuleItem extends RuleObject {
        private boolean key;

        public RuleItem(Path path) {
            super(path);
            key = "yes".equals(path.getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            if (key) {
                for (int i = 0; i < 6; i++) {
                    if (numbers[6] == numbers[i]) {
                        return true;
                    }
                }
                return false;
            } else {
                for (int i = 0; i < 6; i++) {
                    if (numbers[6] == numbers[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
    }
}
