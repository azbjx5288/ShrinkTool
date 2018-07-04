package com.shrinktool.material;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.game.GameConfig;
import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleManager;
import com.shrinktool.rule.RuleRecord;
import com.shrinktool.rule.sdrx5.SpecialNumberRuleSet;
import com.shrinktool.view.NumberGroupView;

import java.util.ArrayList;
import java.util.List;

/**
 * 山东11选5的(任选五中五)胆码处理
 * Created by Alashi on 2016/8/10.
 */
public class SscSpecialHelper2 implements SpecialHelper {

    private BaseFragment fragment;
    private LayoutInflater inflater;
    private RefiningCart refiningCart;
    private int numberType;
    private NumberGroupView numberGroupView;
    private Button[] specialButtons;

    public SscSpecialHelper2(BaseFragment fragment, RefiningCart refiningCart) {
        this.refiningCart = refiningCart;
        this.fragment = fragment;
        inflater = LayoutInflater.from(fragment.getContext());
        numberType = GameConfig.getNumberType(refiningCart.getLottery());
    }

    @Override
    public View getHeaderView() {
        LinearLayout headerView = (LinearLayout) inflater.inflate(
                R.layout.rule_setting_listview_top_item_2, null, false);
        numberGroupView = (NumberGroupView) headerView.findViewById(R.id.pick_column_NumberGroupView);

        specialButtons = new Button[6];
        LinearLayout special = (LinearLayout) headerView.findViewById(R.id.special0);
        for (int i = 0; i < 6; i++) {
            specialButtons[i] = (Button) special.getChildAt(i);
            specialButtons[i].setOnClickListener(this::onSpecialClick);
        }

        return headerView;
    }

    public void onSpecialClick(View view) {
        view.setSelected(!view.isSelected());
    }

    @Override
    public void useRuleRecord(ArrayList<RuleRecord.Special> list) {
        RuleRecord.Special special = list.get(0);
        numberGroupView.setCheckNumber(special.getNumbers());
        for (Button specialButton : specialButtons) {
            specialButton.setSelected(false);
        }

        for (int num : special.getCount()) {
            specialButtons[num].setSelected(true);
        }
    }

    @Override
    public ArrayList<RuleRecord.Special> saveOut() {
        RuleRecord.Special special = new RuleRecord.Special();
        ArrayList<Integer> specialCount = new ArrayList<>();

        for (int i = 0; i < specialButtons.length; i++) {
            if (specialButtons[i].isSelected()) {
                specialCount.add(i);
            }
        }

        special.setNumbers(numberGroupView.getCheckedNumber());
        special.setCount(specialCount);

        ArrayList<RuleRecord.Special> list = new ArrayList<>();
        list.add(special);
        return list;
    }

    @Override
    public boolean getRule(List<ArrayList<String>> rules) {
        Path path = Path.fromString("/sdrx5/specialNumber");
        SpecialNumberRuleSet ruleSet = (SpecialNumberRuleSet) RuleManager.getInstance().getRuleSet(path);
        ArrayList<Integer> numbers = numberGroupView.getCheckedNumber();
        if (numbers.size() == 0) {
            return true;
        }

        ArrayList<String> ruleList = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < specialButtons.length; i++) {
            if (specialButtons[i].isSelected()) {
                ruleList.add(ruleSet.createPathByNumber(i, numbers));
                min = min > i ? i : min;
            }
        }

        if (ruleList.size() > 0) {
            rules.add(ruleList);
        }

        return min == Integer.MAX_VALUE || numbers.size() >= min;
    }

    @Override
    public void reset() {
        numberGroupView.setCheckNumber(null);
        for (Button button: specialButtons) {
            button.setSelected(false);
        }
    }
}
