package com.shrinktool.record;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.base.net.GsonHelper;
import com.shrinktool.data.PlanDetail;
import com.shrinktool.rule.Path;
import com.shrinktool.rule.ResultRecord;
import com.shrinktool.rule.RuleRecord;
import com.shrinktool.rule.ssc.SpecialNumberRuleItem;
import com.shrinktool.rule.ssc.SpecialNumberRuleSet;
import com.shrinktool.rule.ssq.SsqAssistInfo;
import com.shrinktool.view.NumberGroupView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static com.shrinktool.record.RuleNode.MISMATCH;

/**
 * 双色球
 * Created by Alashi on 2017/1/18.
 */

public class RecordAnalyzeSSQ extends RecordAnalyze {
    private static final String TAG = "RecordAnalyzeSSQ";

    private NumberGroupView red;
    private NumberGroupView blue;
    private TextView redText;
    private TextView blueText;

    public RecordAnalyzeSSQ(View topView, PlanDetail planDetail, ResultRecord resultRecord) {
        super(topView, planDetail, resultRecord);
        numberCount = 7;
    }

    @Override
    public String onCopyString() {
        String src = planDetail.getFilterResult();
        src = src.substring(src.indexOf(":") + 1);
        return src.replaceAll("\\|", "\n").replaceAll("_", ",");
    }

    @Override
    protected Object onTransformAssist() {
        String assistString = resultRecord.getRuleRecord().getAssist();
        if (TextUtils.isEmpty(assistString)) {
            return null;
        } else {
            return GsonHelper.fromJson(assistString, SsqAssistInfo.class);
        }
    }

    @Override
    protected int[] onTransformWinCodes() {
        String codeString = planDetail.code;
        if (TextUtils.isEmpty(codeString)) {
            return new int[0];
        }
        int[] openCodes = new int[7];
        int i = 0;
        for (String tmp: codeString.split(" ")) {
            openCodes[i++] = Integer.parseInt(tmp);
        }
        return openCodes;
    }

    @Override
    protected void onApplyPickCode() {
        String src = planDetail.getFilterResult();
        src = src.substring(src.indexOf(":") + 1);
        String[] codes = src.split("\\|");
        for (int i = 0; i < codes.length && i < MAX_CODE_COUNT; i++) {
            //01_02_03_04_05_06,12
            addPickCode(i, codes[i]);
        }

        if (planDetail.filterPlan.getFilterTotal() == 0) {
            planDetail.filterPlan.setFilterTotal(resultRecord.getTicket().getChooseNotes());
            planDetail.filterPlan.setFilterCount(codes.length);
            planDetail.filterPlan.setPickCount(codes.length);
        }
    }

    private void addPickCode(int index, String code) {
        View view = layoutInflater.inflate(R.layout.record_pick_code_ssq, pickNumberLayout, false);
        ((TextView)view.findViewById(R.id.index)).setText(String.valueOf(index + 1));
        LinearLayout numberLayout = (LinearLayout) view.findViewById(R.id.openCodeLayout);
        for (int i = 0, count = numberLayout.getChildCount(); i < count; i++) {
            ((TextView)numberLayout.getChildAt(i)).setText(code.substring(i*3, i*3+2));
        }
        pickNumberLayout.addView(view);
    }

    @Override
    protected void onApplyOriginalCode() {
        View view = layoutInflater.inflate(R.layout.record_pick_column_ssq, pickOriginalLayout, false);
        red = (NumberGroupView) view.findViewById(R.id.pick_column_NumberGroupView_red);
        blue = (NumberGroupView) view.findViewById(R.id.pick_column_NumberGroupView_blue);
        redText = (TextView) view.findViewById(R.id.redText);
        blueText = (TextView) view.findViewById(R.id.blueText);
        String srcCode = resultRecord.getTicket().getCodes();
        ArrayList<Integer> numRed = new ArrayList<>();
        int indexOfComma = srcCode.indexOf(",");
        for (String code: srcCode.substring(0, indexOfComma).split("_")) {
            numRed.add(Integer.parseInt(code));
        }
        red.setCheckNumber(numRed);
        red.setReadOnly(true);

        ArrayList<Integer> numBlue = new ArrayList<>();
        for (String code: srcCode.substring(indexOfComma + 1, srcCode.length()).split("_")) {
            numBlue.add(Integer.parseInt(code));
        }
        blue.setCheckNumber(numBlue);
        blue.setReadOnly(true);

        pickOriginalLayout.addView(view);
    }

    @Override
    protected void onReplayOriginalCode() {
        ArrayList<Integer> redNum = red.getCheckedNumber();
        boolean redMath = true;
        for (int i = 0; i < 6; i++) {
            if (Arrays.binarySearch(redNum.toArray(), winCodes[i]) < 0) {
                redMath = false;
                break;
            }
        }
        setTextViewMathDrawable(redText, redMath);

        ArrayList<Integer> blueNum = blue.getCheckedNumber();
        boolean math = Arrays.binarySearch(blueNum.toArray(), winCodes[6]) >= 0;
        setTextViewMathDrawable(blueText, math);
    }

    @Override
    protected void onApplySpecial(boolean replay) {
        Path path = Path.fromString("/ssq/specialNumber");
        SpecialNumberRuleSet ruleSet = (SpecialNumberRuleSet) manager.getRuleSet(path);
        ArrayList<RuleRecord.Special> specials = resultRecord.getRuleRecord().getSpecial();
        for (RuleRecord.Special special: specials) {
            ArrayList<Integer> specialCount = special.getCount();
            ArrayList<Integer> numbers = special.getNumbers();
            if (numbers.size() == 0) {
                continue;
            }

            RuleNode specialNumber = new RuleNode();
            specialNumber.name = "红球胆码";
            specialNumber.item = new LinkedHashMap<>();
            for (int number: numbers) {
                if (replay) {
                    int math = MISMATCH;
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
                    specialNumberOutCount.item.put(name, ruleObject.apply(winCodes, assist)? RuleNode.MATCH :
                            RuleNode.MISMATCH);
                } else {
                    specialNumberOutCount.item.put(name, 0);
                }
            }
            applyRuleNode(specialNumberOutCount, replay);
        }
    }
}
