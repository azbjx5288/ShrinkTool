package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;

/**
 * 彩种，山东11选5-任选5 的走势数据
 * Created by Alashi on 2016/8/8.
 */
public class TendencySDRX5 {

    //public String td_id;
    //public String issue_id;
    public String lottery_id;
    public String issue;
    public String method_id;
    //public String belong_date;
    public String code;
    /** 综合走势 */
    public GeneralTendency generalTendency;
    /** 奇偶走势 */
    public OddEven oddEven;
    /** 大小走势 */
    public BigSmall bigSmall;
    /** 质合走势 */
    public PrimComposite primComposite;
    /** 和值走势 */
    public SumValue sumValue;
    /** 跨度走势 */
    public DifferenceValueEntity differenceValue;
    //public String ts;

    public static class PrimComposite{
        @SerializedName("primCompositeNumScaleMiss")
        public ScaleMiss scaleMiss;
        public String primCompositeFormText;
        public PrimCompositeForm[] primCompositeForm;
        public String primCompositeNumScale;
        public String[] primNum;
        public String[] compositeNum;
    }

    public static class OddEven {
        @SerializedName("oddEvenScaleMiss")
        public ScaleMiss scaleMiss;
        public String oddEvenFormText;
        public OddEvenForm[] oddEvenForm;
        public String oddEvenNumScale;
        public String[] oddNum;
        public String[] evenNum;
    }

    public static class BigSmall {
        @SerializedName("bigSmallNumScaleMiss")
        public ScaleMiss scaleMiss;
        public String bigSmallFormText;
        public BigSmallForm[] bigSmallForm;
        public String bigSmallNumScale;
        public String[] smallNum;
        public String[] bigNum;
    }

    public static class GeneralTendency {
        public String hezhi;
        public String[] codeMiss;
    }

    public static class DifferenceValueEntity {
        public LinkedHashMap<String, String> codeMiss;
        public String differenceValue;
    }

    public static class SumValue {
        public String sumValue;
        public LinkedHashMap<String, String> sumValueMiss;
        public String sumEndValue;
        public String[] sumEndValueMiss;
    }

    public static class ScaleMiss {
        public String scale_50;
        public String scale_41;
        public String scale_32;
        public String scale_23;
        public String scale_14;
        public String scale_05;
    }

    public static class OddEvenForm{
        public String even;
        public String odd;
    }

    public static class BigSmallForm{
        public String big;
        public String small;
    }

    public static class PrimCompositeForm{
        public String prim;
        public String composite;
    }
}
