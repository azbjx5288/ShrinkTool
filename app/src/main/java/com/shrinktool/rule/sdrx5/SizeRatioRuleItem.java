package com.shrinktool.rule.sdrx5;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

/**
 * 大小比具体规则
 * Created by Alashi on 2016/5/26.
 */
public class SizeRatioRuleItem extends RuleObject {

    private int bigCount;
    private int smallCount;
    /** 大小的中间值，如0到9的是5 */
    private int divideNum;

    public SizeRatioRuleItem(Path path, int divideNum, int bigCount, int smallCount) {
        super(path);
        this.divideNum = divideNum;
        this.bigCount = bigCount;
        this.smallCount = smallCount;
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
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
}
