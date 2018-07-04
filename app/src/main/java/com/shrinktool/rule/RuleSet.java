package com.shrinktool.rule;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 规则集合
 * Created by Alashi on 2016/5/26.
 */
public abstract class RuleSet extends RuleObject {

    /** 重庆时时彩的三星直选，号码从0到9的 */
    public static final int TYPE_0_9_SXZX = 1;
    /** 山东11选5的“任选五中五”，号码从01到11的 */
    public static final int TYPE_1_11_SDRX5 = 2;
    /** 双色球 */
    public static final int TYPE_SSQ = 3;
    /** 排列5-五星直选 */
    public static final int TYPE_WXZX = 4;

    private SparseArray<ArrayList<String[]>> cache = new SparseArray<>();
    public RuleSet(Path path) {
        super(path);
    }

    public ArrayList<String[]> getRuleList(int numberCount){
        ArrayList<String[]> list = cache.get(numberCount);
        if (list == null) {
            list = onCreateRuleList(numberCount);
            cache.put(numberCount, list);
        }
        return list;
    }

    /***
     * 获取用于显示的“规则表”
     * @param numberCount 玩法需要选择的数字位数，比如“后三直选”是需要选择3个数字
     * @return
     */
    public abstract ArrayList<String[]> onCreateRuleList(int numberCount);

    public abstract RuleObject createRuleObject(Path path);

    protected String createChild(String segment) {
        return path.getChild(segment).toString();
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        throw new UnsupportedOperationException("RuleSet 不能直接应用规则");
    }

    public abstract String getName();

    public String getHint() {
        return "";
    }

    protected static final ArrayList<String[]> buildRuleList(RuleSet ruleSet, int numberCount,
                                                             char key1, String name1, char key2,
                                                             String name2) {
        String setPath = ruleSet.getPath().toString();
        ArrayList<String[]> map = new ArrayList<>();
        map.add(new String[]{ setPath + "/" + UNLIMITED, "不限"});

        char[][] srcCodes = new char[numberCount][];
        int outCodeLength = 1;
        for (int i = 0; i < numberCount; i++) {
            srcCodes[i] = new char[] {key1, key2};
            outCodeLength *= 2;
        }

        int[] index = new int[srcCodes.length];
        Arrays.fill(index, 1);
        for (int i = 0; i < outCodeLength; i++) {
            String key = "";
            for (int j = 0; j < index.length; j++) {
                key += srcCodes[j][index[j]];
            }

            map.add(new String[]{ setPath + "/" + key,
                    key.replace(String.valueOf(key1), name1).replace(String.valueOf(key2), name2)});

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
        }
        return map;
    }
}
