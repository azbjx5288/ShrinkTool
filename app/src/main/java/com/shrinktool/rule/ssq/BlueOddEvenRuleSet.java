package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 蓝球奇偶
 * 不限，奇数，偶数
 * Created by Alashi on 2016/8/9.
 */
public class BlueOddEvenRuleSet extends RuleSet {

    public BlueOddEvenRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        ArrayList<String[]> map = new ArrayList<>();
        map.add(new String[]{createChild(UNLIMITED), "不限"});
        map.add(new String[]{createChild("odd"), "奇数"});
        map.add(new String[]{createChild("even"), "偶数"});
        return map;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return "蓝球奇偶";
    }

    public static class RuleItem extends RuleObject{
        private boolean odd;

        public RuleItem(Path path) {
            super(path);
            odd = "odd".equals(path.getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            return odd == ((numbers[6] & 1) != 0);
        }
    }
}
