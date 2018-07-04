package com.shrinktool.rule;

import java.util.HashMap;

/**
 * 老版本的Path映射到新版本
 * Created by Alashi on 2016/10/25.
 */

public class OddVersionMather {
    private static final String TAG = "OddVersionMather";

    private HashMap<String, String> math = new HashMap<>();

    public OddVersionMather() {
        /*//旧版本“三星直选”的
        math.put("specialNumber9", "/sxzx/specialNumber");
        math.put("sizeRatio9", "/sxzx/sizeRatio");
        math.put("sizeShape9", "/sxzx/sizeShape");
        math.put("oddEvenRatio", "/sxzx/oddEvenRatio");
        math.put("oddEvenShape", "/sxzx/oddEvenShape");
        math.put("primeRatio", "/sxzx/primeRatio");
        math.put("primeShape", "/sxzx/primeShape");
        math.put("modularRatio", "/sxzx/modularRatio");
        math.put("modularShape", "/sxzx/modularShape");
        math.put("sum9", "/sxzx/sum");
        math.put("sumMantissa", "/sxzx/sumMantissa");
        math.put("span9", "/sxzx/span");

        //老版本“双色球”的
        math.put("specialNumberSSQ", "/ssq/specialNumber");
        math.put("oddEvenRatioSSQ", "/ssq/oddEvenRatio");
        math.put("sizeRatioSSQ", "/ssq/sizeRatio");
        math.put("primeRatioSSQ", "/ssq/primeRatio");
        math.put("modularRatioSSQ", "/ssq/modularRatio");
        math.put("rangeRatioSSQ", "/ssq/rangeRatio");
        math.put("sumSSQ", "/ssq/sum");
        math.put("acSSQ", "/ssq/ac");
        math.put("mantissaSSQ", "/ssq/mantissa");
        math.put("spanSSQ", "/ssq/span");
        math.put("repeatSSQ", "/ssq/repeat");
        math.put("blueOddEvenSSQ", "/ssq/blueOddEven");
        math.put("blueSizeSSQ", "/ssq/blueSize");
        math.put("lastRepeatSSQ", "/ssq/lastRepeat");*/
    }

    /** 将旧版本的path转换成新版本 */
    public String transformPath(String path) {
        if (math.size() == 0) {
            return path;
        }
        String[] items = path.split("/");
        String replacePath = math.get(items[1]);
        if (replacePath == null) {
            return path;
        }
        //Log.i(TAG, "transformPath: " + path + " -> " + items[1] + " -> " + outPath);
        return replacePath + path.substring(items[0].length() + items[1].length() + 1);
    }
}
