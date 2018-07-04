package com.shrinktool.material;

import android.util.Log;

import com.shrinktool.data.Lottery;
import com.shrinktool.game.GameConfig;
import com.shrinktool.rule.RuleRecord;
import com.shrinktool.rule.RuleSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ACE-PC on 2016/1/26.
 */
public class RefiningCart {
    private static final String TAG = "RefiningCart";
    private static RefiningCart instance = new RefiningCart();

    private List<ArrayList<String>> ruleList;
    private Ticket ticket;
    private Lottery lottery;
    private RuleRecord ruleRecord;
    /**
     * 辅助信息，不同的记录可能需要不同的信息，使用时再格式化成具体对象，注意混淆配置
     */
    private Object assist;
    private String issue;

    private RefiningCart() {
    }

    public static RefiningCart getInstance() {
        return instance;
    }

    public boolean isEmpty() {
        return ticket == null;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Object getAssist() {
        return assist;
    }

    /** 辅助数据，需要混淆配置 */
    public void setAssist(Object assist) {
        this.assist = assist;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void clear() {
        ruleList = null;
        lottery = null;
        ticket = null;
        ruleRecord = null;
    }

    public List<ArrayList<String>> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<ArrayList<String>> ruleList) {
        this.ruleList = ruleList;
    }

    public void setLottery(Lottery lottery) {
        this.lottery = lottery;
    }

    public Lottery getLottery() {
        return this.lottery;
    }

    public int getLotteryId() {
        return lottery.getLotteryId();
    }

    /***
     * 将“0123456789,13579,02468”拆解成单个注单的集合
     * 11,06_07_08_09_10_11,06_07_08_09_10_11
     *
     * @return
     */
    public int[][] buildNumbers() {
        int numberType = GameConfig.getNumberType(lottery);
        if (numberType == RuleSet.TYPE_1_11_SDRX5) {
            return buildNumbersSDRX5();
        }

        String[] codes = ticket.getCodes().split(",");
        Log.i(TAG, "buildNumbers: " + ticket.getCodes());
        int[][] srcCodes = new int[codes.length][];
        int[] index = new int[srcCodes.length];//每个位数的下标
        int outLength = 1;
        for (int i = 0; i < srcCodes.length; i++) {
            if (numberType == RuleSet.TYPE_0_9_SXZX || numberType == RuleSet.TYPE_WXZX) {
                int length = codes[i].length();
                outLength *= length;
                index[i] = length - 1;
                srcCodes[i] = new int[length];
                for (int j = 0; j < length; j++) {
                    int num = codes[i].charAt(j) - 48;
                    srcCodes[i][j] = num;
                }
            } else {
                String[] codeNumbers = codes[i].split("_");
                int length = codeNumbers.length;
                outLength *= length;
                index[i] = length - 1;
                srcCodes[i] = new int[length];
                for (int j = 0; j < length; j++) {
                    srcCodes[i][j] = Integer.valueOf(codeNumbers[j]);
                }
            }
        }
        //Log.i(TAG, "buildNumbers: " + Arrays.deepToString(srcCodes));

        int[][] outCode = null;
        if (numberType == RuleSet.TYPE_0_9_SXZX || numberType == RuleSet.TYPE_WXZX) {
            outCode = new int[outLength][srcCodes.length];
            for (int i = 0; i < outCode.length; i++) {
                for (int j = 0; j < index.length; j++) {
                    outCode[i][j] = srcCodes[j][index[j]];
                }
                //Log.d(TAG, "buildNumbers: out " + Arrays.toString(outCode[i]));

                int xIndex = index.length - 1;
                while (xIndex >= 0) {
                    index[xIndex]--;
                    if (index[xIndex] < 0) {
                        index[xIndex] = srcCodes[xIndex].length - 1;
                        xIndex--;
                        continue;
                    }
                    break;
                }
                //Log.i(TAG, "buildNumbers: index " + Arrays.toString(index));//[10, 5, 5]
            }
        } else {
            //双色球
            OriginalCode ssqCode = new OriginalCode(srcCodes[0], srcCodes[1]);
            outCode = ssqCode.getOutCode();
        }
        return outCode;
    }

    /**
     * 山东11选5的“任选五中五”
     */
    private int[][] buildNumbersSDRX5() {
        Log.i(TAG, "buildNumbersSDRX5: " + ticket.getCodes());
        String[] codes = ticket.getCodes().split("_");
        int[] index = new int[codes.length];//每个位数的下标
        for (int j = 0; j < codes.length; j++) {
            index[j] = Integer.valueOf(codes[j]);
        }

        Log.i(TAG, "buildNumbersSDRX5: " + Arrays.toString(index));

        int[] outCodeLength = new int[]{0, 0, 0, 0, 0, 1, 6, 21, 56, 126, 252, 462};
        //int[][] outCode = new int[outCodeLength[index.length]][5];
        return new Combine(outCodeLength[index.length], 5, index).getOutCode();
    }

    public void setRuleRecord(RuleRecord ruleRecord) {
        this.ruleRecord = ruleRecord;
    }

    public RuleRecord getRuleRecord() {
        return ruleRecord;
    }
}
