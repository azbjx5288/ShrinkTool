package com.shrinktool.rule.ssc;

import com.shrinktool.rule.Path;
import com.shrinktool.rule.RuleObject;

/**
 * 奇偶比
 * Created by Alashi on 2016/5/30.
 */
public class OddEvenRationRuleItem extends RuleObject {
    public static final int ALL_ODD = 2;
    public static final int ALL_EVEN = 3;
    public static final int OTHER = 4;

    private int type;
    private int oddCount;
    private int evenCount;

    public OddEvenRationRuleItem(Path path, int type) {
        super(path);
        this.type = type;
    }

    public OddEvenRationRuleItem(Path path, int type, int oddCount, int evenCount) {
        super(path);
        this.type = type;
        this.oddCount = oddCount;
        this.evenCount = evenCount;
    }

    @Override
    public boolean apply(int[] numbers, Object assist) {
        switch (type) {
            case ALL_EVEN: {
                for (int num : numbers) {
                    if ((num & 1) != 0) {
                        return false;
                    }
                }
                return true;
            }

            case ALL_ODD: {
                for (int num : numbers) {
                    if ((num & 1) == 0) {
                        return false;
                    }
                }
                return true;
            }

            case OTHER:{
                int even = 0, odd = 0;
                for (int num : numbers) {
                    if ((num & 1) != 0) {
                        odd++;
                    } else {
                        even++;
                    }
                }
                return even == evenCount && odd == oddCount;
            }

            default:
                throw new UnsupportedOperationException("不支持的计算规则：" + type);
        }
    }
}
