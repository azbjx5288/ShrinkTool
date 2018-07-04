package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 红球质合比
 * 共有8种
 * 不限，0:6，1:5，2:4，3:3，4:2，5:1，6:0
 * Created by Alashi on 2016/8/9.
 */
public class RedPrimeRatioRuleSet extends RuleSet{
    public RedPrimeRatioRuleSet(Path path) {
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
        return "红球质合比";
    }

    public static class RuleItem extends RuleObject{
        private static final int[] PRIME = new int[]{1,2,3,5,7,11,13,17,19,23,29,31};

        private int primeCount;
        private int compositeCount;

        public RuleItem(Path path, int primeCount, int compositeCount) {
            super(path);
            this.primeCount = primeCount;
            this.compositeCount = compositeCount;
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            int prime = 0;
            int composite = 0;
            for (int i = 0; i < 6; i++) {
                if (Arrays.binarySearch(PRIME, numbers[i]) > -1) {
                    prime++;
                } else {
                    composite++;
                }

                if (prime > primeCount || composite > compositeCount) {
                    return false;
                }
            }
            return true;
        }
    }
}
