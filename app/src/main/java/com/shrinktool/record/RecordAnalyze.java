package com.shrinktool.record;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.data.PlanDetail;
import com.shrinktool.rule.Path;
import com.shrinktool.rule.ResultRecord;
import com.shrinktool.rule.RuleManager;
import com.shrinktool.rule.RuleObject;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 过滤记录按不同彩种进行处理
 * Created by Alashi on 2017/1/18.
 */

public abstract class RecordAnalyze {
    private static final String TAG = "RecordAnalyze";
    /** 过滤结果列表显示的最大条数，超出显示按钮“查看更多” */
    protected static final int MAX_CODE_COUNT = 5;
    protected RuleManager manager;
    protected int numberCount;
    protected int[] winCodes;
    protected Object assist;
    protected PlanDetail planDetail;
    protected ResultRecord resultRecord;
    private String copyCode;
    protected View topView;
    protected LinearLayout pickNumberLayout;
    protected LinearLayout pickOriginalLayout;
    protected LinearLayout pickRuleLayout;
    protected LayoutInflater layoutInflater;
    private View pickNumberMore;

    public RecordAnalyze(View topView, PlanDetail planDetail, ResultRecord resultRecord) {
        manager = RuleManager.getInstance();
        this.topView = topView;
        this.planDetail = planDetail;
        this.resultRecord = resultRecord;
        pickNumberLayout = (LinearLayout) topView.findViewById(R.id.pickNumberLayout);
        pickOriginalLayout = (LinearLayout) topView.findViewById(R.id.pickOriginalLayout);
        pickRuleLayout = (LinearLayout) topView.findViewById(R.id.pickRuleLayout);
        pickNumberMore = topView.findViewById(R.id.pickNumberMore);
        layoutInflater = LayoutInflater.from(topView.getContext());
    }

    public final void apply(){
        assist = onTransformAssist();
        winCodes = onTransformWinCodes();
        onApplyPickCode();

        ((TextView)topView.findViewById(R.id.filterTotal))
                .setText(planDetail.filterPlan.getFilterTotal() + "注");
        ((TextView)topView.findViewById(R.id.filterCount))
                .setText(planDetail.filterPlan.getFilterCount() + "注");
        ((TextView)topView.findViewById(R.id.filterRatio))
                .setText((100 * planDetail.filterPlan.getFilterCount() / planDetail.filterPlan.getFilterTotal())
                        + "%");
        ((TextView)topView.findViewById(R.id.pickCount))
                .setText(Html.fromHtml(String.format("共<font color=#FFA423>%d</font>注<font color=#FFA423>%d</font>元",
                        planDetail.filterPlan.getPickCount(), 2 * planDetail.filterPlan.getPickCount())));
        if (planDetail.filterPlan.getPickCount() > MAX_CODE_COUNT) {
            pickNumberMore.setVisibility(View.VISIBLE);
        } else {
            pickNumberMore.setVisibility(View.GONE);
        }
        onApplyOriginalCode();

        pickRuleLayout.removeAllViews();
        onApplySpecial(false);
        applyOtherRule(false);
    }

    private void applyOtherRule(boolean replay){
        ArrayList<String> ruleList = resultRecord.getRuleRecord().getRuleList();
        RuleSet cRuleSet = null;
        RuleNode cNode = null;
        for (String rule: ruleList) {
            //Log.i(TAG, "decodeRuleRecord: " + rule);
            RuleSet ruleSet = manager.getRuleSet(Path.fromString(rule).getParent());
            RuleObject ruleObject = manager.getRuleObject(Path.fromString(rule));
            if (cRuleSet != ruleSet) {
                if (cNode != null) {
                    if (replay && cNode.match == RuleNode.MISMATCH) {
                        findCorrectRule(cRuleSet, cNode);
                    }
                    applyRuleNode(cNode, replay);
                }
                cRuleSet = ruleSet;
                cNode = new RuleNode();
                cNode.name = ruleSet.getName();
                cNode.item = new LinkedHashMap<>();
            }
            String name = findName(ruleSet, rule);
            if (replay) {
                int math = ruleObject.apply(winCodes, assist)? RuleNode.MATCH : RuleNode.MISMATCH;
                cNode.item.put(name, math);
                if (math == RuleNode.MATCH) {
                    cNode.match = RuleNode.MATCH;
                }
            } else {
                cNode.item.put(name, 0);
            }
        }

        if (cNode != null) {
            if (replay && cNode.match == RuleNode.MISMATCH) {
                findCorrectRule(cRuleSet, cNode);
            }
            applyRuleNode(cNode, replay);
        }
    }

    private void findCorrectRule(RuleSet ruleSet, RuleNode cNode) {
        ArrayList<String[]> list = ruleSet.getRuleList(numberCount);
        for (String[] item : list) {
            RuleObject rule = manager.getRuleObject(Path.fromString(item[0]));
            if (rule != null && !rule.isUnlimited() && rule.apply(winCodes, assist)) {
                cNode.item.put(item[1], RuleNode.REVISE);
                return;
            }
        }
    }

    protected void applyRuleNode(RuleNode node, boolean replay) {
        View view = layoutInflater.inflate(R.layout.record_rule, pickRuleLayout, false);
        ((TextView)view.findViewById(R.id.ruleSetName)).setText(node.name + ":");
        TextView ruleItem = (TextView)view.findViewById(R.id.ruleItem);
        if (replay) {
            String text = "";
            int[][] spanIndex = new int[node.item.size()][3];
            int i = 0;
            for (Map.Entry<String, Integer> item : node.item.entrySet()) {
                int value = item.getValue();
                if (value == RuleNode.MATCH) {
                    text += item.getKey() + "　";
                } else {
                    text += item.getKey() + " [" + value + "]　";
                    int length = text.length();
                    spanIndex[i++] = new int[]{item.getValue(), length-4, length - 1};
                }
            }
            SpannableString spannableString = new SpannableString(text);
            for (int j = 0; j < i; j++) {
                int id = spanIndex[j][0] == RuleNode.MISMATCH ? R.drawable.faxq_fp_error : R.drawable.faxq_fp_correct;
                        setSpan(spannableString, id, spanIndex[j][1], spanIndex[j][2]);
            }
            ruleItem.setText(spannableString);
        } else {
            String text = "";
            for (Map.Entry<String, Integer> item : node.item.entrySet()) {
                text += item.getKey() + "　";
            }
            ruleItem.setText(text);
        }
        pickRuleLayout.addView(view);
    }

    private void setSpan(SpannableString spannableString, @DrawableRes int id, int start, int end) {
        ImageSpan span = new ImageSpan(layoutInflater.getContext(), id, ImageSpan.ALIGN_BASELINE);
        spannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    private String findName(RuleSet ruleSet, String rule) {
        rule = manager.transformPath(rule);
        for (String[] item : ruleSet.getRuleList(numberCount)) {
            if (rule.equals(item[0])) {
                return item[1];
            }
        }
        return null;
    }

    public void replay(){
        if (planDetail.isPrize != 0) {
            return;
        }

        pickRuleLayout.removeAllViews();
        onApplySpecial(true);
        applyOtherRule(true);
        onReplayOriginalCode();
    }

    protected void setTextViewMathDrawable(TextView textView, boolean math){
        Drawable drawable = textView.getContext().getResources().getDrawable(
                math? R.drawable.faxq_fp_correct : R.drawable.faxq_fp_error);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    public String getCopyCode() {
        if (copyCode == null) {
            copyCode = onCopyString();
        }
        return copyCode;
    }

    protected abstract String onCopyString();
    protected abstract Object onTransformAssist();
    protected abstract int[] onTransformWinCodes();
    protected abstract void onApplyPickCode();
    protected abstract void onApplyOriginalCode();
    protected abstract void onReplayOriginalCode();
    protected abstract void onApplySpecial(boolean replay);
}
