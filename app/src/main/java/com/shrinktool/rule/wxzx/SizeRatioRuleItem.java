package com.shrinktool.rule.wxzx;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

/**
 * 大小比具体规则
 * Created by Alashi on 2016/5/26.
 */
public class SizeRatioRuleItem extends RuleObject {
    public static final int ALL_BIG = 2;
    public static final int ALL_SMALL = 3;
    public static final int OTHER = 4;

    private int type;
    private int bigCount;
    private int smallCount;
    /** 大小的中间值，如0到9的是5 */
    private int divideNum;

    public SizeRatioRuleItem(Path path, int type, int divideNum, int bigCount, int smallCount) {
        super(path);
        this.type = type;
        this.divideNum = divideNum;
        this.bigCount = bigCount;
        this.smallCount = smallCount;
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        switch (type) {
            case ALL_BIG:{
                boolean out = true;
                for (int number : numbers) {
                    out &= number >= divideNum;
                }
                return out;
            }
            case ALL_SMALL:{
                boolean out = true;
                for (int number : numbers) {
                    out &= number < divideNum;
                }
                return out;
            }

            case OTHER: {
                int big = 0, small = 0;
                for (int number : numbers) {
                    if (number >= divideNum) {
                        big++;
                    } else {
                        small++;
                    }
                }
                return big == bigCount && small == smallCount;
            }

            default:
                throw new UnsupportedOperationException("不支持的计算规则：" + type);
        }
    }
}
