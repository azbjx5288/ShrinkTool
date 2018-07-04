package com.shrinktool.material;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleManager;
import com.shrinktool.rule.RuleRecord;
import com.shrinktool.rule.ssc.SpecialNumberRuleSet;
import com.shrinktool.view.NumberGroupView;

import java.util.ArrayList;
import java.util.List;

/**
 * 双色球的胆码处理
 * Created by Alashi on 2016/8/10.
 */
public class SsqSpecialHelper implements SpecialHelper {
    private BaseFragment fragment;
    private LayoutInflater inflater;

    private PopupWindow popupWindow;
    private LinearLayout layout;
    private Button addButton;
    private View titleView;
    private ItemHelper[] itemHelpers = new ItemHelper[5];

    private NumberGroupView numberGroupView;
    private Button[] specialButtons;

    private ArrayList<RuleRecord.Special> specialList;

    public SsqSpecialHelper(BaseFragment fragment) {
        this.fragment = fragment;
        inflater = LayoutInflater.from(fragment.getContext());
        specialList = new ArrayList<>();
    }

    @Override
    public View getHeaderView() {
        LinearLayout headerView = (LinearLayout) inflater.inflate(
                R.layout.rule_setting_listview_top_item_ssq, null, false);
        titleView = headerView.findViewById(R.id.ruleSettingSsqTitle);
        addButton = (Button) headerView.findViewById(R.id.ruleSettingSsqAdd);
        addButton.setOnClickListener(this::showDialog);
        layout = (LinearLayout) headerView.findViewById(R.id.ruleSettingSsqLayout);
        return headerView;
    }

    private void showDialog(View view) {
        if (popupWindow == null) {
            LinearLayout pickView = (LinearLayout) inflater.inflate(
                    R.layout.rule_setting_ssq, null, false);
            pickView.findViewById(R.id.ruleSettingSsqCancel).setOnClickListener(view1 -> popupWindow.dismiss());
            pickView.findViewById(R.id.ruleSettingSsqOk).setOnClickListener(this::onDialogAddItem);
            numberGroupView = (NumberGroupView) pickView.findViewById(R.id
                    .pick_column_NumberGroupView);
            specialButtons = new Button[5];
            LinearLayout special = (LinearLayout) pickView.findViewById(R.id.special0);
            for (int i = 0; i < 5; i++) {
                specialButtons[i] = (Button) special.getChildAt(i);
            }
            for (Button specialButton : specialButtons) {
                specialButton.setOnClickListener(this::onSpecialClick);
            }

            popupWindow = new PopupWindow(fragment.getActivity());
            popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(pickView);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
        }

        popupWindow.showAsDropDown(titleView);
    }

    private void onDialogAddItem(View view) {
        RuleRecord.Special special = new RuleRecord.Special();
        ArrayList<Integer> specialCount = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < specialButtons.length; i++) {
            if (specialButtons[i].isSelected()) {
                specialCount.add(i + 1);
                min = min > i + 1 ? i + 1 : min;
            }
        }

        ArrayList<Integer> numbers = numberGroupView.getCheckedNumber();
        if (!(min == Integer.MAX_VALUE || numbers.size() >= min)) {
            fragment.showToast("胆码数量不符合要求，请选择更多的胆码，或改变“胆码出现个数”");
            return;
        }

        special.setNumbers(numbers);
        special.setCount(specialCount);

        specialList.add(special);

        popupWindow.dismiss();
        updateUi();
    }

    private void updateUi() {
        int size = specialList.size();
        for (int i = 0; i < 5; i++) {
            if (i < size) {
                if (itemHelpers[i] == null) {
                    itemHelpers[i] = new ItemHelper();
                }
                itemHelpers[i].setData(i, specialList.get(i));
                itemHelpers[i].itemLayout.setVisibility(View.VISIBLE);
            } else {
                if (itemHelpers[i] != null) {
                    itemHelpers[i].itemLayout.setVisibility(View.GONE);
                }
            }
        }

        addButton.setVisibility(size == 5? View.GONE: View.VISIBLE);
    }

    private class ItemHelper implements View.OnClickListener {
        View del;
        TextView indexView;
        TextView red;
        TextView blue;
        LinearLayout itemLayout;
        int index;

        public ItemHelper() {
            itemLayout = (LinearLayout) inflater.inflate(R.layout.rule_setting_ssq_special_item, layout,
                    false);
            indexView = (TextView) itemLayout.findViewById(R.id.ruleSettingSsqIndex);
            red = (TextView) itemLayout.findViewById(R.id.ruleSettingSsqRedList);
            blue = (TextView) itemLayout.findViewById(R.id.ruleSettingSsqBlueList);
            del = itemLayout.findViewById(R.id.ruleSettingSsqDel);
            del.setOnClickListener(this);
            layout.addView(itemLayout);
        }

        private void setData(int index, RuleRecord.Special special) {
            this.index = index;
            indexView.setText(String.valueOf(index + 1));

            String text = "";
            for (int i = 0, size = special.getNumbers().size(); i < size; i++) {
                if (i == size - 1) {
                    text += String.format("%02d", special.getNumbers().get(i));
                } else {
                    text += String.format("%02d,", special.getNumbers().get(i));
                }
            }
            red.setText(text);

            text = "";
            for (int i = 0, size = special.getCount().size(); i < size; i++) {
                if (i == size - 1) {
                    text += String.format("%d", special.getCount().get(i));
                } else {
                    text += String.format("%d,", special.getCount().get(i));
                }
            }
            blue.setText(text);
        }

        @Override
        public void onClick(View view) {
            specialList.remove(index);
            updateUi();
        }
    }

    public void onSpecialClick(View view) {
        view.setSelected(!view.isSelected());
    }

    @Override
    public void useRuleRecord(ArrayList<RuleRecord.Special> list) {
        if (list != null) {
            specialList = list;
            updateUi();
        }
    }

    @Override
    public ArrayList<RuleRecord.Special> saveOut() {
        return specialList;
    }

    @Override
    public boolean getRule(List<ArrayList<String>> rules) {
        if (specialList.size() == 0) {
            return true;
        }
        Path path = Path.fromString("/ssq/specialNumber");
        SpecialNumberRuleSet ruleSet = (SpecialNumberRuleSet) RuleManager.getInstance().getRuleSet(path);
        ArrayList<String> ruleList = new ArrayList<>();
        for (RuleRecord.Special special: specialList) {
            for (int count: special.getCount()) {
                ruleList.add(ruleSet.createPathByNumber(count, special.getNumbers()));
            }
        }
        if (ruleList.size() > 0) {
            rules.add(ruleList);
        }
        return true;
    }


    @Override
    public void reset() {
        specialList.clear();
        updateUi();
    }
}
