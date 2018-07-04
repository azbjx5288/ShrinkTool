package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;

/**
 * 双色球的走势数据
 * Created by Alashi on 2016/8/8.
 */
public class TendencySsq {

    public int td_id;
    public String issue_id;
    public int lottery_id;
    public int method_id;
    public String issue;
    public String belong_date;
    public String code;
    public GeneralTendency generalTendency;
    public RedThreeArea redThreeArea;
    public RedOddEven redOddEven;
    public RedBigSmall redBigSmall;
    public RedPrimComposite redPrimComposite;
    public RedSumValue redSumValue;
    public RedDifferenceValue redDifferenceValue;
    public RedEndValue redEndValue;
    public BlueGeneral blueGeneral;
    public String ts;

    public static class RedBigSmall{

        @SerializedName("0")
        public RedBigSmallItem red1;
        @SerializedName("1")
        public RedBigSmallItem red2;
        @SerializedName("2")
        public RedBigSmallItem red3;
        @SerializedName("3")
        public RedBigSmallItem red4;
        @SerializedName("4")
        public RedBigSmallItem red5;
        @SerializedName("5")
        public RedBigSmallItem red6;
        public int[] redBall;
        public String position;
        public String retio;

        public LinkedHashMap<String, String> retioMiss;

        public static class RedBigSmallItem {
            public String small;
            public String big;
        }
    }

    public static class RedPrimComposite{

        @SerializedName("0")
        public RedPrimCompositeItem red1;
        @SerializedName("1")
        public RedPrimCompositeItem red2;
        @SerializedName("2")
        public RedPrimCompositeItem red3;
        @SerializedName("3")
        public RedPrimCompositeItem red4;
        @SerializedName("4")
        public RedPrimCompositeItem red5;
        @SerializedName("5")
        public RedPrimCompositeItem red6;
        public int[] redBall;
        public String position;
        public String retio;

        public LinkedHashMap<String, String> retioMiss;

        public static class RedPrimCompositeItem {
            public String prim;
            public String composite;
        }
    }

    public static class RedOddEven{

        @SerializedName("0")
        public RedObbEvenItem red1;
        @SerializedName("1")
        public RedObbEvenItem red2;
        @SerializedName("2")
        public RedObbEvenItem red3;
        @SerializedName("3")
        public RedObbEvenItem red4;
        @SerializedName("4")
        public RedObbEvenItem red5;
        @SerializedName("5")
        public RedObbEvenItem red6;
        public int[] redBall;
        public String position;
        public String retio;

        public LinkedHashMap<String, String> retioMiss;

        public static class RedObbEvenItem {
            public String odd;
            public String even;
        }
    }

    public static class RedThreeArea{
        public String area1;
        public String area2;
        public String area3;
        public String area1_left;
        public String area1_right;
        public String area2_left;
        public String area2_right;
        public String area3_left;
        public String area3_right;
        public String ratio;
        public LinkedHashMap<String, String> area;
    }

    public static class GeneralTendency {
        public LinkedHashMap<String, String> redCodeMiss;
        public LinkedHashMap<String, String> blueCodeMiss;
    }

    public static class RedSumValue {
        public String sum;
        public LinkedHashMap<String, String> sumMiss;
        public String sumEnd;
        public int[] redBall;
        public String[] sumEndMiss;
    }

    public static class RedDifferenceValue {
        public LinkedHashMap<String, String> differenceMiss;
        public int odd;
        public int even;
        public int small;
        public int big;
        public int mod0;
        public int mod1;
        public int mod2;
        public int[] redBall;
    }

    public static class RedEndValue {
        public int[] redBall;
        public LinkedHashMap<String, String> endValueGroup;
        public String[] endValue;
    }

    public static class BlueGeneral {
        public int blueBall;

        public LinkedHashMap<String, String> ballMiss;
        public int odd;
        public int even;
        public int small;
        public int big;
        public int mod0;
        public int mod1;
        public int mod2;
    }
}
