package com.shrinktool.material;

import android.view.View;

import com.shrinktool.rule.RuleRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤设置时的胆码的处理
 * Created by Alashi on 2016/8/10.
 */
public interface SpecialHelper {
    View getHeaderView();
    void useRuleRecord(ArrayList<RuleRecord.Special> list);
    ArrayList<RuleRecord.Special> saveOut();
    boolean getRule(List<ArrayList<String>> rules);
    void reset();
}
