package com.shrinktool.rule.ssq;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 红球三区比
 * 红球33个号码, 01-11为第一区, 12-22为二区, 23-33位三区, 共19项
 * 不限，6:0:0,5:1:0,5:0:1,4:2:0,4:1:1,4:0:2,3:3:0,3:2:1,3:1:2,3:0:3,2:4:0,2:3:1,2:2:2,2:1:3,2:0:4,1:5:0,1:4:1,1:3:2,
 * 1:2:3,1:1:4,1:0:5,0:6:0,0:5:1,0:4:2,0:3:3,0:2:4,0:1:5,0:0:6
 * Created by Alashi on 2016/8/9.
 */
public class RedRangeRationRuleSet extends RuleSet {
    public RedRangeRationRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        int count = 6;
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});

        int[] index = new int[3];
        Arrays.fill(index, count);

        int outCodeLength = 1;
        for (int i = 0; i < 3; i++) {
            outCodeLength *= (count + 1);
        }

        for (int i = 0; i < outCodeLength; i++) {
            int key = 0;
            for (int anIndex : index) {
                key += anIndex;
            }

            if (key == count) {
                String outKey = "";
                String outDisplay = "";
                for (int j = 0; j < index.length - 1; j++) {
                    outKey += index[j];
                    outDisplay += index[j] + ":";
                }
                outKey += index[index.length - 1];
                outDisplay += index[index.length - 1];
                list.add(new String[]{ top + outKey, outDisplay });
            }

            int xIndex = index.length - 1;
            while (xIndex >= 0) {
                index[xIndex]--;
                if (index[xIndex] < 0) {
                    index[xIndex] = count;
                    xIndex--;
                    continue;
                }
                break;
            }
        }

        return list;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new RuleItem(path);
    }

    @Override
    public String getName() {
        return "红球三区比";
    }

    public static class RuleItem extends RuleObject{
        private int[] keys;
        public RuleItem(Path path) {
            super(path);
            String key = path.getSuffix();
            keys = new int[key.length()];
            for (int i = 0; i < 3; i++) {
                keys[i] = key.charAt(i) - 48;
            }
        }

        @Override
        public boolean apply(int[] numbers, Object assist) {
            int[] newKeys = new int[3];
            for (int i = 0; i < 6; i++) {
                if (numbers[i] <= 11) {
                    newKeys[0]++;
                } else if (numbers[i] >= 23) {
                    newKeys[2]++;
                } else {
                    newKeys[1]++;
                }
            }

            for (int i = 0; i < 3; i++) {
                if (newKeys[i] == keys[i]) {
                    continue;
                }
                return false;
            }
            return true;
        }
    }
}
