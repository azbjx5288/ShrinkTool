package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 蓝球大小
 * 不限，大数，小数
 * Created by Alashi on 2016/8/9.
 */
public class BlueSizeRuleSet extends RuleSet {

    public BlueSizeRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        ArrayList<String[]> map = new ArrayList<>();
        map.add(new String[]{createChild(UNLIMITED), "不限"});
        map.add(new String[]{createChild("big"), "大数"});
        map.add(new String[]{createChild("small"), "小数"});
        return map;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return "蓝球大小";
    }

    public static class RuleItem extends RuleObject{
        private boolean big;

        public RuleItem(Path path) {
            super(path);
            big = "big".equals(path.getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            return big == numbers[6] >= 9;
        }
    }
}
