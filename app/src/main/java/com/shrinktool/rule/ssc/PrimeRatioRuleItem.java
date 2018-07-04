package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

import java.util.Arrays;

/**
 * 质合比, 数字范围0到11
 * Created by Alashi on 2016/5/30.
 */
public class PrimeRatioRuleItem extends RuleObject {

    private static final int[] PRIME = new int[]{1,2,3,5,7,11};
    private static final int[] COMPOSITE = new int[]{0,4,6,8,9,10};

    public static final int ALL_PRIME = 2;
    public static final int ALL_COMPOSITE = 3;
    public static final int OTHER = 4;

    private int type;
    private int primeCount;
    private int compositeCount;

    public PrimeRatioRuleItem(Path path, int type) {
        super(path);
        this.type = type;
    }

    public PrimeRatioRuleItem(Path path, int type, int primeCount, int compositeCount) {
        super(path);
        this.type = type;
        this.primeCount = primeCount;
        this.compositeCount = compositeCount;
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        switch (type) {
            case ALL_PRIME:{
                for (int num : numbers) {
                    if (Arrays.binarySearch(PRIME, num) < 0) {
                        return false;
                    }
                }
                return true;
            }

            case ALL_COMPOSITE:{
                for (int num : numbers) {
                    if (Arrays.binarySearch(COMPOSITE, num) < 0) {
                        return false;
                    }
                }
                return true;
            }

            case OTHER:{
                int prime = 0;
                int composite = 0;
                for (int num : numbers) {
                    if (Arrays.binarySearch(PRIME, num) > -1) {
                        prime++;
                    } else {
                        composite++;
                    }

                    if (prime > primeCount || composite > compositeCount) {
                        return false;
                    }
                }
                return true;
            }

            default:
                throw new UnsupportedOperationException("不支持的计算规则：" + type);
        }
    }
}
