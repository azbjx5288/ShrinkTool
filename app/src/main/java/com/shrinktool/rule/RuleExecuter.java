package com.shrinktool.rule;

import android.util.Log;

import com.shrinktool.BuildConfig;
import com.shrinktool.app.GoldenAsiaApp;
import com.shrinktool.game.GameConfig;
import com.shrinktool.material.RefiningCart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 对选号产生的原始号码运用“规则”进行过滤
 * Created by Alashi on 2016/5/31.
 */
public class RuleExecuter {
    private static final String TAG = "RuleExecuter";

    private List<ArrayList<String>> allRules;
    private OnResultLister lister;
    private int[][] srcCode;

    private boolean[] isValid;
    private int runningCount;
    private Object Lock = new Object();
    private RuleManager ruleManager;
    private int ruleSetIndex;
    private int validCount;
    private Object assist;

    public interface OnResultLister {
        void onResult(int[][] result);
    }

    public RuleExecuter(int[][] srcCode, RefiningCart refiningCart, OnResultLister lister) {
        this.allRules = refiningCart.getRuleList();
        this.lister = lister;
        this.srcCode = srcCode;
        ruleManager = RuleManager.getInstance();
        if (GameConfig.getNumberType(refiningCart.getLottery()) == RuleSet.TYPE_SSQ) {
            //双色球的“上期重复个数”规则需要上期开奖号码
             assist = refiningCart.getAssist();
        }
    }

    public void execute() {
        ruleSetIndex = 0;
        isValid = new boolean[srcCode.length];
        validCount = srcCode.length;

        executeRuleSet(allRules.get(ruleSetIndex));
    }

    private void executeRuleSet(ArrayList<String> list) {
        synchronized (Lock) {
            Arrays.fill(isValid, false);
            runningCount = list.size();
            for (int i = 0, size = list.size(); i < size; i++) {
                String rulePath = list.get(i);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "executeRuleSet: submit rule is: " + rulePath);
                }
                RuleObject ruleObject = ruleManager.getRuleObject(Path.fromString(rulePath));
                if (ruleObject.isUnlimited()) {
                    runningCount--;
                } else {
                    apply(ruleObject);
                }
            }
        }
    }

    private void notifyLister() {
        lister.onResult(Arrays.copyOf(srcCode, validCount));
    }

    private void apply(RuleObject rule) {
        GoldenAsiaApp.getThreadPool().submit((jc) -> {
                    for (int i = 0, length = validCount; i < length; i++) {
                        if (!isValid[i] && rule.apply(srcCode[i], assist)) {
                            isValid[i] = true;
                        }
                    }

                    synchronized (Lock) {
                        runningCount--;
                        if (runningCount == 0) {
                            buildOut();
                        }
                    }
                    return null;
                }, (future) -> {
                    if (runningCount == 0 && ruleSetIndex == allRules.size()) {
                        notifyLister();
                    }
                }, false
        );
    }

    private void buildOut() {
        int rest = 0;
        for (int i = 0; i < validCount; i++) {
            if (isValid[i]) {
                srcCode[rest++] = srcCode[i];
            }
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "buildOut: 过滤后: " + validCount + " -> " + rest);
        }
        validCount = rest;
        ruleSetIndex++;
        if (ruleSetIndex < allRules.size()) {
            executeRuleSet(allRules.get(ruleSetIndex));
        }
    }
}
