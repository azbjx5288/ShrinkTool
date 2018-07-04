package com.shrinktool.record;

import android.util.Log;

import java.util.ArrayList;

/**
 * 将过滤结果，格式化成投注的字符串
 * Created by Alashi on 2016/12/8.
 */

public class ResultFormater {
    private static final String TAG = "ResultFormater";

    private int[][] result;
    private int lotteryId;
    private int methodId;
    private String submitString;

    public ResultFormater(int[][] result, int lotteryId, int methodId) {
        this.result = result;
        this.lotteryId = lotteryId;
        this.methodId = methodId;
    }

    public String getSubmitString() {
        if (submitString == null) {
            submitString = buildSubmitString();
        }
        Log.d(TAG, "getSubmitString: " + submitString);
        return submitString;
    }

    private String buildSubmitString() {
        switch (lotteryId) {
            case 1://重庆时时彩
            case 4://新疆时时彩
            case 8://天津时时彩
            case 9://福彩3D
            case 10://P3P5(排列3)
                return buildSubmitStringSXZX();
            case 2://山东11选5
            case 6://江西11选5
            case 7://广东11选5
            case 20://北京11选5
            case 21://上海11选5
                return buildSubmitStringSDRX5();
            case 100://双色球
                return buildSubmitStringSSQ();
        }
        return null;
    }

    //13579,02468,01234
    private String buildSubmitStringSXZX() {
        StringBuilder builder = new StringBuilder();
        builder.append(methodId);
        builder.append(":");

        for (int i = 0; i < result.length; i++) {
            int[] codes = result[i];
            for (int j = 0; j < codes.length; j++) {
                builder.append(String.format("%d", codes[j]));
                if (j != codes.length - 1) {
                    builder.append(",");
                }
            }

            if (i != result.length -1) {
                builder.append("|");
            }
        }
        return builder.toString();
    }

    //01_02_03_05_06|01_02_03_05_07
    private String buildSubmitStringSDRX5() {
        StringBuilder builder = new StringBuilder();
        builder.append(methodId);
        builder.append(":");

        for (int i = 0; i < result.length; i++) {
            int[] codes = result[i];
            for (int j = 0; j < codes.length; j++) {
                builder.append(String.format("%02d", codes[j]));
                if (j != codes.length - 1) {
                    builder.append("_");
                }
            }

            if (i != result.length -1) {
                builder.append("|");
            }
        }
        return builder.toString();
    }

    //01_02_03_04_05_06_07,12_13
    private String buildSubmitStringSSQ() {
        long t = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder(result.length * 21 + 100);
        builder.append(methodId);
        builder.append(":");

        for (int i = 0; i < result.length; i++) {
            int[] codes = result[i];
            for (int j = 0; j < 6; j++) {
                builder.append(String.format("%02d", codes[j]));
                if (j != 5) {
                    builder.append("_");
                }
            }
            builder.append(",");
            builder.append(String.format("%02d", codes[6]));
            if (i != result.length -1) {
                builder.append("|");
            }
        }
        Log.i(TAG, "buildSubmitStringSSQ: " + (System.currentTimeMillis() - t) + ", " + builder.length());
        return builder.toString();
    }

    /** 将Int的list转换成字符串，如list[06, 07] 转成string[06_07]
     * @param list int型数组
     * @param numberStyle 数字显示风格，true: 6, false: 06
     * @param emptyStyle 数组空时的显示风格， true: ""，false: "-"*/
    protected static String transform(ArrayList<Integer> list, boolean numberStyle, boolean emptyStyle) {
        StringBuilder builder = new StringBuilder();
        if (list.size() > 0) {
            for (int i = 0, size = list.size(); i < size; i++) {
                builder.append(String.format(numberStyle ? "%d" : "%02d", list.get(i)));
                if (!numberStyle && i != size - 1) {
                    builder.append("_");
                }
            }
        } else {
            builder.append(emptyStyle? "": "-");
        }
        return builder.toString();
    }
}
