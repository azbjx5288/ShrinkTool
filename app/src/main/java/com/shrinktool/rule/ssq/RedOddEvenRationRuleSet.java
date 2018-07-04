package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 红球奇偶比
 * 共有8种
 * 不限，0:6，1:5，2:4，3:3，4:2，5:1，6:0
 * Created by Alashi on 2016/8/9.
 */
public class RedOddEvenRationRuleSet extends RuleSet {
    private static final String TAG = "RedOddEvenRationRuleSet";
    public RedOddEvenRationRuleSet(Path path) {
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
        return "红球奇偶比";
    }

    public static class RuleItem extends RuleObject{

        private int oddCount;
        private int evenCount;

        public RuleItem(Path path, int oddCount, int evenCount) {
            super(path);
            this.oddCount = oddCount;
            this.evenCount = evenCount;
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            int even = 0, odd = 0;
            for (int i = 0; i < 6; i++) {
                if ((numbers[i] & 1) != 0) {
                    odd++;
                } else {
                    even++;
                }
            }
            //Log.d(TAG, "apply: " + path.getSuffix() + " " + Arrays.toString(numbers)
            //        + ", " + (even == evenCount && odd == oddCount));
            return even == evenCount && odd == oddCount;
        }
    }
}
