package com.shrinktool.rule.wxzx;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 大小比
 * Created by Alashi on 2016/5/26.
 */
public class SizeRatioRuleSet extends RuleSet {

    private int type;

    public SizeRatioRuleSet(Path path, int type) {
        super(path);
        this.type = type;
    }

    @Override
    public String getName() {
        return "大小比";
    }

    private int getDivideNum() {
        switch (type) {
            case TYPE_0_9_SXZX:
                return 5;
            case TYPE_1_11_SDRX5:
                return 6;
            default:
                throw new UnsupportedOperationException("不支持的类型:" + type);
        }
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        ArrayList<String[]> map = new ArrayList<>();
        map.add(new String[]{createChild(UNLIMITED), "不限"});
        for (int i = 0; i <= numberCount; i++) {
            map.add(new String[]{createChild(i + "_" + (numberCount - i)),
                    i + ":" + (numberCount - i)});
        }
        return map;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        int divideNum = getDivideNum();
        switch (path.getSuffix()) {
            case "allBig":
                return new SizeRatioRuleItem(path, SizeRatioRuleItem.ALL_BIG, divideNum, 0, 0);
            case "allSmall":
                return new SizeRatioRuleItem(path, SizeRatioRuleItem.ALL_SMALL, divideNum, 0, 0);
            default:
                String[] strings = path.getSuffix().split("_");
                return new SizeRatioRuleItem(path, SizeRatioRuleItem.OTHER, divideNum,
                        Integer.valueOf (strings[0]), Integer.valueOf(strings[1]));
        }

    }
}
