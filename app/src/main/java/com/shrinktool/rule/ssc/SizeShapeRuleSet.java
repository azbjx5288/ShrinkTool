package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 大小形态设置
 * Created by Alashi on 2016/5/27.
 */
public class SizeShapeRuleSet extends RuleSet {
    private int type;

    public SizeShapeRuleSet(Path path, int type) {
        super(path);
        this.type = type;
    }

    @Override
    public String getName() {
        return "大小形态";
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
        return buildRuleList(this, numberCount, 's', "小", 'b', "大");
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        return new SizeShapeRuleItem(path, getDivideNum());
    }
}
