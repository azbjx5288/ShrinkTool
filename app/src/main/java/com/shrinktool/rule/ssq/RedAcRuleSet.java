package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 红球AC值
 * 不限，0,1,2,3,4,5,6,7,8,9,10
 * 解释：AC值:也称数字复杂度，是由世界著名彩票专家诺伯特•海齐和数学家汉斯•里德威尔率先提出来的。
 * 它指的是在一组号码组合中，任意两个数字之间不相同的正差值总个数减去号码数量加1的值。
 * 『例如:开奖号码10、11、16、18、19，他的AC值计算方法分为以下两步，
 * 第一步计算差值个数 11-10=1，16-10=6，18-10=8，19-10=9，16-11=5，18-11=7，19-11=8，18-16=2，19-16=3，19-18=1，
 * 其中相同的1和8只保留1个，其结果为8个差值。第二步彩票的组成号码个数为5，8-5+1=4，计算结果AC值是4』。
 * Created by Alashi on 2016/8/9.
 */
public class RedAcRuleSet extends RuleSet {
    private static final String TAG = "RedAcRuleSet";
    public RedAcRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});
        for (int i = 0; i <= 10; i++) {
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
        return "红球AC值";
    }

    public static class RuleItem extends RuleObject{
        private int ac;
        public RuleItem(Path path) {
            super(path);
            ac = Integer.parseInt(path.getSuffix());
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            boolean[] match = new boolean[33];
            Arrays.fill(match, false);
            for (int i = 0; i < 6; i++) {
                for (int j = 5; j >= i; j--) {
                    if (j == i) {
                        continue;
                    }
                    int index = Math.abs(numbers[j] - numbers[i]);
                    //Log.d(TAG, "apply: j,i=" + j +"," + i + " -> " + numbers[j] + "-" + numbers[i] + " = " + index);
                    match[index] = true;
                }
            }

            int matchCount = 0;
            for (boolean m: match) {
                if (m) {
                    matchCount++;
                }
            }
            //Log.d(TAG, "apply: matchCount = " + matchCount);
            return matchCount - 5 == ac;
        }
    }
}
