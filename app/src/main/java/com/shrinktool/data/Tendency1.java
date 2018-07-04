package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

/**
 * 彩种xx的走势数据
 * Created by Alashi on 2016/8/8.
 */
public class Tendency1 {

    public String td_id;
    public String issue_id;
    public String lottery_id;
    public String issue;
    public String method_id;
    public String belong_date;
    public String code;
    //public PrimComposite primComposite;
    /** 综合走势 */
    public GeneralTendency generalTendency;
    /** 直选定位走势 */
    public SelectFix selectFix;
    /** 奇偶走势 */
    public OddEven[] oddEven;
    /** 大小走势 */
    public BigSmall bigSmall;
    /** 质合走势 */
    public PrimComposite primComposite;
    /** 和值走势 */
    public SumValue sumValue;
    /** 跨度走势 */
    public DifferenceValueEntity differenceValue;
    public String ts;
    //public List<?> oddEven;

    public static class PrimComposite{
        @SerializedName("0")
        public PrimCompositeItem _0;
        @SerializedName("1")
        public PrimCompositeItem _1;
        @SerializedName("2")
        public PrimCompositeItem _2;
        public String specificOfPrimComposite;
    }

    public static class PrimCompositeItem{
        /** 质数 */
        public int prim;
        /** 合数 */
        public int composite;
        public String[] number;
    }

    public static class BigSmall{
        @SerializedName("0")
        public BigSmallItem _0;
        @SerializedName("1")
        public BigSmallItem _1;
        @SerializedName("2")
        public BigSmallItem _2;
        public String specificOfBigSamll;
    }

    public static class BigSmallItem{
        public int big;
        public int small;
        public String[] number;
    }

    public static class OddEven {
        public int odd;
        public int even;
        public String[] number;
    }

    public static class SelectFix{
        public int zusanMiss;
        public int zuliuMiss;
        public int hezhi;
        public int kuadu;
        public String[] codeMiss0;
        public String[] codeMiss1;
        public String[] codeMiss2;
    }

    public static class GeneralTendency {
        public int zusanMiss;
        public int zuliuMiss;
        public int hezhi;
        public int kuadu;
        public String[] codeMiss;
    }

    public static class DifferenceValueEntity {
        public String[] codeMiss;
    }

    public static class SumValue {
        public String bigSamll;
        public String oddEven;
        public String primComposite;
        public int divide3;
        public int area;
        public int sum;
        public String[] number;
    }
}
