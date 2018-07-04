package com.shrinktool.record;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.data.PlanDetail;
import com.shrinktool.rule.Path;
import com.shrinktool.rule.ResultRecord;
import com.shrinktool.rule.RuleRecord;
import com.shrinktool.rule.sdrx5.SpecialNumberRuleItem;
import com.shrinktool.rule.sdrx5.SpecialNumberRuleSet;
import com.shrinktool.view.NumberGroupView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 山东11选5，任选5
 * Created by User on 2017/1/18.
 */

public class RecordAnalyzeSDRX5 extends RecordAnalyze {
    private TextView name;
    public RecordAnalyzeSDRX5(View topView, PlanDetail planDetail, ResultRecord resultRecord) {
        super(topView, planDetail, resultRecord);
        numberCount = resultRecord.getTicket().getCodes().split(",").length;
    }

    @Override
    public String onCopyString() {
        String src = planDetail.getFilterResult();
        src = src.substring(src.indexOf(":") + 1);
        return src.replaceAll("\\|", "\n").replaceAll("_", ",");
    }

    @Override
    protected Object onTransformAssist() {
        return null;
    }

    @Override
    protected int[] onTransformWinCodes() {
        String codeString = planDetail.code;
        if (TextUtils.isEmpty(codeString)) {
            return null;
        }
        String[] codes = codeString.split(" ");
        int[] openCodes = new int[codes.length];
        for (int i = 0; i < openCodes.length; i++) {
            openCodes[i] = Integer.valueOf(codes[i]);
        }

        return openCodes;
    }

    @Override
    protected void onApplyPickCode() {
        String src = planDetail.getFilterResult();
        src = src.substring(src.indexOf(":") + 1);
        String[] codes = src.split("\\|");
        for (int i = 0; i < codes.length && i < MAX_CODE_COUNT; i++) {
            //01_02_03_06_08
            addPickCode(i, codes[i]);
        }

        if (planDetail.filterPlan.getFilterTotal() == 0) {
            planDetail.filterPlan.setFilterTotal(resultRecord.getTicket().getChooseNotes());
            planDetail.filterPlan.setFilterCount(codes.length);
            planDetail.filterPlan.setPickCount(codes.length);
        }
    }

    private void addPickCode(int index, String code) {
        View view = layoutInflater.inflate(R.layout.record_pick_code_sdrx5, pickNumberLayout, false);
        ((TextView)view.findViewById(R.id.index)).setText(String.valueOf(index + 1));
        LinearLayout numberLayout = (LinearLayout) view.findViewById(R.id.openCodeLayout);
        for (int i = 0, count = numberLayout.getChildCount(); i < count; i++) {
            ((TextView)numberLayout.getChildAt(i)).setText(code.substring(i*3, i*3+2));
        }
        pickNumberLayout.addView(view);
    }

    @Override
    protected void onApplyOriginalCode() {
        //01_02_03_05_06_07_08_09_10_11
        String srcCode = resultRecord.getTicket().getCodes();
        View view = layoutInflater.inflate(R.layout.record_pick_column_sdrx5, pickOriginalLayout, false);
        name = (TextView) view.findViewById(R.id.name);
        NumberGroupView numberGroupView = (NumberGroupView) view.findViewById(R.id.pick_column_NumberGroupView);
        numberGroupView.setReadOnly(true);
        ArrayList<Integer> num = new ArrayList<>();
        for (String code : srcCode.split("_")) {
            num.add(Integer.parseInt(code));
        }
        numberGroupView.setCheckNumber(num);
        pickOriginalLayout.addView(view);
    }

    @Override
    protected void onReplayOriginalCode() {
        setTextViewMathDrawable(name, planDetail.isPrize > 0);
    }

    @Override
    protected void onApplySpecial(boolean replay) {
        Path path = Path.fromString("/sdrx5/specialNumber");
        SpecialNumberRuleSet ruleSet = (SpecialNumberRuleSet) manager.getRuleSet(path);
        ArrayList<RuleRecord.Special> specials = resultRecord.getRuleRecord().getSpecial();
        for (RuleRecord.Special special: specials) {
            ArrayList<Integer> specialCount = special.getCount();
            ArrayList<Integer> numbers = special.getNumbers();
            if (numbers.size() == 0) {
                continue;
            }

            RuleNode specialNumber = new RuleNode();
            specialNumber.name = "不定位胆码";
            specialNumber.item = new LinkedHashMap<>();
            for (int number: numbers) {
                if (replay) {
                    int math = RuleNode.MISMATCH;
                    for (int win: winCodes) {
                        if (win == number) {
                            math = RuleNode.MATCH;
                        }
                    }
                    specialNumber.item.put(String.valueOf(number), math);
                } else {
                    specialNumber.item.put(String.valueOf(number), 0);
                }
            }
            applyRuleNode(specialNumber, replay);

            RuleNode specialNumberOutCount = new RuleNode();
            specialNumberOutCount.name = "出现个数";
            specialNumberOutCount.item = new LinkedHashMap<>();
            for (int countKey : specialCount) {
                SpecialNumberRuleItem ruleObject = (SpecialNumberRuleItem) manager.getRuleObject(Path.fromString(
                        ruleSet.createPathByNumber(countKey, numbers)));
                String name = String.valueOf(ruleObject.getNumberCount());
                if (replay) {
                    specialNumberOutCount.item.put(name,
                            ruleObject.apply(winCodes, assist)? RuleNode.MATCH : RuleNode.MISMATCH);
                } else {
                    specialNumberOutCount.item.put(name, 0);
                }
            }
            applyRuleNode(specialNumberOutCount, replay);
        }
    }
}
