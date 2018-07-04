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
import com.shrinktool.rule.RuleSet;
import com.shrinktool.rule.ssc.SpecialNumberRuleSet;
import com.shrinktool.view.NumberGroupView;

import java.util.ArrayList;
import java.util.List;

/**
 * 一般彩种的胆码处理（时时彩）
 * Created by Alashi on 2016/8/10.
 */
public class SscSpecialHelper implements SpecialHelper {

    private BaseFragment fragment;
    private LayoutInflater inflater;
    private RefiningCart refiningCart;
    private int numberCount;
    private int numberType;
    private NumberGroupView numberGroupView;
    private Button[] specialButtons;

    public SscSpecialHelper(BaseFragment fragment, RefiningCart refiningCart, int numberCount) {
        this.refiningCart = refiningCart;
        this.numberCount = numberCount;
        this.fragment = fragment;
        inflater = LayoutInflater.from(fragment.getContext());
        numberType = GameConfig.getNumberType(refiningCart.getLottery());
    }

    @Override
    public View getHeaderView() {
        LinearLayout headerView = (LinearLayout) inflater.inflate(
                R.layout.rule_setting_listview_top_item, null, false);
        numberGroupView = (NumberGroupView) headerView.findViewById(R.id.pick_column_NumberGroupView);
        switch (numberType) {
            case RuleSet.TYPE_1_11_SDRX5:
                numberGroupView.setNumber(1, 11);
                numberGroupView.setNumberStyle(false);
                numberGroupView.setColumn(6);
                break;
            case RuleSet.TYPE_WXZX:
                numberGroupView.setNumber(0, 9);
                numberGroupView.setNumberStyle(true);
                break;
            default:
                numberGroupView.setNumber(0, 9);
                numberGroupView.setNumberStyle(true);
                break;
        }

        specialButtons = new Button[6];
        LinearLayout special = (LinearLayout) headerView.findViewById(R.id.special0);
        for (int i = 0; i < 4; i++) {
            specialButtons[i] = (Button) special.getChildAt(i);
        }
        special = (LinearLayout) headerView.findViewById(R.id.special1);
        for (int i = 0; i < 2; i++) {
            specialButtons[i + 4] = (Button) special.getChildAt(i);
        }
        if (numberCount <= 3) {
            special.setVisibility(View.GONE);
        }

        for (int i = 0; i < specialButtons.length; i++) {
            specialButtons[i].setVisibility(i <= numberCount? View.VISIBLE: View.INVISIBLE);
            specialButtons[i].setEnabled(i <= numberCount);
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
        Path path;
        switch (numberType) {
            case RuleSet.TYPE_0_9_SXZX:
                path = Path.fromString("/sxzx/specialNumber");
                break;
            case RuleSet.TYPE_1_11_SDRX5:
                path = Path.fromString("/sdrx5/specialNumber");
                break;
            case RuleSet.TYPE_WXZX:
                path = Path.fromString("/wxzx/specialNumber");
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作：" + numberType);
        }
        SpecialNumberRuleSet ruleSet = (SpecialNumberRuleSet) RuleManager.getInstance().getRuleSet(path);
        ArrayList<Integer> numbers = numberGroupView.getCheckedNumber();
        if (numbers.size() == 0) {
            return true;
        }

        ArrayList<String> ruleList = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        if (numberType == RuleSet.TYPE_SSQ) {
            for (int i = 0; i < specialButtons.length; i++) {
                if (specialButtons[i].isSelected()) {
                    ruleList.add(ruleSet.createPathByNumber(i + 1, numbers));
                    min = min > i ? i : min;
                }
            }
        } else {
            for (int i = 0; i < specialButtons.length; i++) {
                if (specialButtons[i].isSelected()) {
                    ruleList.add(ruleSet.createPathByNumber(i, numbers));
                    min = min > i ? i : min;
                }
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
