package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;

/**
 * 质合比
 * Created by Alashi on 2016/5/30.
 */
public class PrimeRatioRuleSet extends RuleSet {

    public PrimeRatioRuleSet(Path path) {
        super(path);
    }

    @Override
    public ArrayList<String[]> onCreateRuleList(int numberCount) {
        ArrayList<String[]> map = new ArrayList<>();
        map.add(new String[]{createChild(UNLIMITED), "不限"});
        map.add(new String[]{createChild("allPrime"), "全质"});
        map.add(new String[]{createChild("allComposite"), "全合"});
        for (int i = numberCount - 1; i > 0; i--) {
            map.add(new String[]{createChild(i + "_" + (numberCount - i)),
                    i + "质" + (numberCount - i) +"合"});
        }
        return map;
    }

    @Override
    public RuleObject createRuleObject(Path path) {
        switch (path.getSuffix()) {
            case "allPrime":
                return new PrimeRatioRuleItem(path, PrimeRatioRuleItem.ALL_PRIME);
            case "allComposite":
                return new PrimeRatioRuleItem(path, PrimeRatioRuleItem.ALL_COMPOSITE);
            default:
                String[] strings = path.getSuffix().split("_");
                return new PrimeRatioRuleItem(path, PrimeRatioRuleItem.OTHER,
                        Integer.valueOf (strings[0]), Integer.valueOf(strings[1]));
        }
    }

    @Override
    public String getName() {
        return "质合比";
    }
}
