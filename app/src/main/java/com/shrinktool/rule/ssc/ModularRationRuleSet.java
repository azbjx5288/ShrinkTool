package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 012路比，实际是取模<br>
 * 0路：0、3、6、9; 1路：1、4、7; 2路：2、5、8
 解释：012路（除3余数）是指将每注中各位置号码除以3后的余数组合。
 如：开奖号码“345”，其中0路号码1个（3），1路号码1个（4），2路号码1个（5），则012路比为1:1:1.
 012路比共有10种组合：
 3:0:0,  2:1:0，2:0:1,  1:2;0,  1:1:1,  1:0:2,  0:3:0  ,0:2:1,  0:1:2,  0:0:3
 * Created by Alashi on 2016/5/30.
 */
public class ModularRationRuleSet extends RuleSet {
    public ModularRationRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});

        int[] index = new int[3];
        Arrays.fill(index, numberCount);

        int outCodeLength = 1;
        for (int i = 0; i < 3; i++) {
            outCodeLength *= (numberCount + 1);
        }

        for (int i = 0; i < outCodeLength; i++) {
            int key = 0;
            for (int anIndex : index) {
                key += anIndex;
            }

            if (key == numberCount) {
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
                    index[xIndex] = numberCount;
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
        return new ModularRationRuleItem(path);
    }

    @Override
    public String getName() {
        return "012路比";
    }
}
