package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 012路形态
 * Created by Alashi on 2016/5/30.
 */
public class ModularShapeRuleSet extends RuleSet {
    public ModularShapeRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        String top = getPath().toString() + "/";
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{ top + UNLIMITED, "不限"});

        int[] index = new int[numberCount];
        int maxType = 3;
        Arrays.fill(index, maxType - 1);

        int outCodeLength = 1;
        for (int i = 0; i < numberCount; i++) {
            outCodeLength *= maxType;
        }

        for (int i = 0; i < outCodeLength; i++) {
            String outKey = "";
            for (int anIndex : index) {
                outKey += anIndex;
            }

            list.add(new String[]{ top + outKey, outKey });

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
        return new ModularShapeRuleItem(path);
    }

    @Override
    public String getName() {
        return "012路形态";
    }
}
