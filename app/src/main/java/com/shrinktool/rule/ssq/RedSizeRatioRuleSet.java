package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 红球大小比（17为大数）
 * 共有8种
 * 不限，0:6，1:5，2:4，3:3，4:2，5:1，6:0
 * Created by Alashi on 2016/8/9.
 */
public class RedSizeRatioRuleSet extends RuleSet {

    public RedSizeRatioRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        ArrayList<String[]> map = new ArrayList<>();
        map.add(new String[]{createChild(UNLIMITED), "不限"});
        int count = 6;
        for (int i = 0; i <= count; i++) {
            map.add(new String[]{createChild(i + "_" + (count - i)),
                    i + ":" + (count - i)});
        }
        return map;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        String[] strings = path.getSuffix().split("_");
        return new RuleItem(path, Integer.valueOf (strings[0]), Integer.valueOf(strings[1]));
    }

    @Override
    public String getName() {
        return "红球大小比";
    }

    public static class RuleItem extends RuleObject{
        private int bigCount;
        private int smallCount;

        public RuleItem(Path path, int bigCount, int smallCount) {
            super(path);
            this.bigCount = bigCount;
            this.smallCount = smallCount;
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            int big = 0, small = 0;
            for (int i = 0; i < 6; i++) {
                if (numbers[i] >= 17) {
                    big++;
                } else {
                    small++;
                }
            }
            return big == bigCount && small == smallCount;
        }
    }
}
