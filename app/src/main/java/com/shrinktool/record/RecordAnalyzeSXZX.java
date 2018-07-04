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
import com.shrinktool.rule.ssc.SpecialNumberRuleItem;
import com.shrinktool.rule.ssc.SpecialNumberRuleSet;
import com.shrinktool.view.NumberGroupView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * 重庆时时彩，三星直选
 * Created by Alashi on 2017/1/18.
 */

public class RecordAnalyzeSXZX extends RecordAnalyze {

    private NumberGroupView[] numberGroupViews;
    private TextView[] names;

    public RecordAnalyzeSXZX(View topView, PlanDetail planDetail, ResultRecord resultRecord) {
        super(topView, planDetail, resultRecord);
        numberCount = resultRecord.getTicket().getCodes().split(",").length;
    }

    @Override
    public String onCopyString() {
        String src = planDetail.getFilterResult();
        src = src.substring(src.indexOf(":") + 1);
        return src.replaceAll("\\|", "\n");
    }

    @Override
    protected Object onTransformAssist() {
        return null;
    }

    @Override
    protected int[] onTransformWinCodes() {
        String codeString = planDetail.code;
        if (TextUtils.isEmpty(codeString)) {
            return new int[0];
        }
        int[] openCodes = new int[codeString.length()];
        for (int i = 0; i < openCodes.length; i++) {
            openCodes[i] = codeString.charAt(i) - 48;
        }

        int methodId=  resultRecord.getTicket().getChooseMethod().getMethodId();
        int lotteryId = resultRecord.getTicket().getChooseMethod().getLotteryId();
        switch (lotteryId) {
            case 1: //重庆时时彩
                if (methodId == 1) {
                    return Arrays.copyOfRange(openCodes, 2, 5);
                }
                break;
            case 4: //新疆时时彩
                if (methodId == 103) {
                    return Arrays.copyOfRange(openCodes, 2, 5);
                }
                break;
            case 8: //天津时时彩
                if (methodId == 190) {
                    return Arrays.copyOfRange(openCodes, 2, 5);
                }
                break;
        }
        return openCodes;
    }

    @Override
    protected void onApplyPickCode() {
        String src = planDetail.getFilterResult();
        src = src.substring(src.indexOf(":") + 1);
        String[] codes = src.split("\\|");
        for (int i = 0; i < codes.length && i < MAX_CODE_COUNT; i++) {
            //9,9,4
            addPickCode(i, codes[i]);
        }

        if (planDetail.filterPlan.getFilterTotal() == 0) {
            planDetail.filterPlan.setFilterTotal(resultRecord.getTicket().getChooseNotes());
            planDetail.filterPlan.setFilterCount(codes.length);
            planDetail.filterPlan.setPickCount(codes.length);
        }
    }

    private void addPickCode(int index, String code) {
        View view = layoutInflater.inflate(R.layout.record_pick_code_sxzx, pickNumberLayout, false);
        ((TextView)view.findViewById(R.id.index)).setText(String.valueOf(index + 1));
        LinearLayout numberLayout = (LinearLayout) view.findViewById(R.id.openCodeLayout);
        for (int i = 0, count = numberLayout.getChildCount(); i < count; i++) {
            ((TextView)numberLayout.getChildAt(i)).setText(code.substring(i*2, i*2+1));
        }
        pickNumberLayout.addView(view);
    }

    @Override
    protected void onApplyOriginalCode() {
        //0123456789,0123456789,0123456789
        numberGroupViews = new NumberGroupView[3];
        names = new TextView[3];
        String srcCode = resultRecord.getTicket().getCodes();
        String[] name = new String[]{"百位", "十位", "个位"};
        String[] codes = srcCode.split(",");
        for (int i = 0; i < name.length; i++) {
            View view = layoutInflater.inflate(R.layout.record_pick_column_sxzx, pickOriginalLayout, false);
            view.findViewById(R.id.divide).setVisibility(i == name.length - 1? View.GONE : View.VISIBLE);
            names[i] = ((TextView)view.findViewById(R.id.name));
            names[i].setText(name[i]);
            NumberGroupView numberGroupView = (NumberGroupView) view.findViewById(R.id.pick_column_NumberGroupView);
            numberGroupViews[i] = numberGroupView;
            numberGroupView.setReadOnly(true);
            ArrayList<Integer> num = new ArrayList<>();
            for (int j = 0, length = codes[i].length(); j < length; j++) {
                num.add(Integer.parseInt(codes[i].substring(j, j + 1)));
            }
            numberGroupView.setCheckNumber(num);
            pickOriginalLayout.addView(view);
        }
    }

    @Override
    protected void onReplayOriginalCode() {
        for (int i = 0; i < numberGroupViews.length; i++) {
            boolean math = Arrays.binarySearch(numberGroupViews[i].getCheckedNumber().toArray(), winCodes[i]) >= 0;
            setTextViewMathDrawable(names[i], math);
        }
    }

    @Override
    protected void onApplySpecial(boolean replay) {
        Path path = Path.fromString("/sxzx/specialNumber");
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
