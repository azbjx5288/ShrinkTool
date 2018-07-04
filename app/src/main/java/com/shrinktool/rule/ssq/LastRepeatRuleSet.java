package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 上期重复个数(红球)
 * 不限，无，1,2,3,4,5,6
 * Created by Alashi on 2016/8/10.
 */
public class LastRepeatRuleSet extends RuleSet {
    public LastRepeatRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ createChild(UNLIMITED), "不限"});
        list.add(new String[]{ createChild("0"), "无"});
        for (int i = 1; i < 7; i++) {
            String key = String.valueOf(i);
            list.add(new String[]{ createChild(key), key});
        }
        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return "上期重复个数";
    }

    public static class RuleItem extends RuleObject {
        private int key;

        public RuleItem(Path path) {
            super(path);
            key = Integer.parseInt(path.getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            if (assist == null) {
                return true;
            }

            int[] lastCode = ((SsqAssistInfo) assist).getLastIssueCode();
            int sameCount = 0;
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (numbers[i] == lastCode[j]) {
                        sameCount++;
                    }
                }
            }
            return sameCount == key;
        }
    }
}
