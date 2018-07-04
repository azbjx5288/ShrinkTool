package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 012个数，实际是取模<br>
 * 0路：0、3、6、9; 1路：1、4、7; 2路：2、5、8
 * 解释：012路（除3余数）是指将每注中各位置号码除以3后的余数个数。
 * Created by Alashi on 2016/5/30.
 */
public class ModularRuleSet extends RuleSet {
    public ModularRuleSet(Path path) {
        super(path);

    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});
        int max = getLastNumber(path.getSuffix()) == 0? 3:4;
        for (int i = 0; i <= max; i++) {
            list.add(new String[]{ top + i, i + "个"});
        }


        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return getLastNumber(path.getSuffix()) + "路";
    }

    private static int getLastNumber(String str) {
        return Integer.parseInt(str.substring(str.length() - 1));
    }

    public static class RuleItem extends RuleObject {

        private int number;
        private int modular;

        public RuleItem(Path path) {
            super(path);
            number = Integer.parseInt(path.getSuffix());
            modular = getLastNumber(path.getParent().getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            int count = 0;
            for (int number1 : numbers) {
                if (number1 % 3 == modular) {
                    count++;
                }
            }
            return number == count;
        }
    }
}
