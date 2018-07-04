package com.shrinktool.Analyzer;

import android.graphics.Color;

import com.google.gson.reflect.TypeToken;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.TendencySDRX5;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 山东11选5-任选5
 * Created by Alashi on 2016/12/2.
 */
public class TrendProcessorSDRX5 extends TrendProcessor{

    private static final int _综合 = 1;

    private static final int _奇偶比 = 2;
    private static final int _奇偶个数 = 3;
    private static final int _奇偶形态 = 4;

    private static final int _大小比 = 5;
    private static final int _大小个数 = 6;
    private static final int _大小形态 = 7;

    private static final int _质合比 = 8;
    private static final int _质合个数 = 9;
    private static final int _质合形态 = 10;

    private static final int _和值 = 11;
    private static final int _和尾 = 12;

    private static final int _跨度 = 13;


    public TrendProcessorSDRX5(int lotteryId, int methodId) {
        super(lotteryId, methodId);
        addAnalyzerType(_综合, "综合走势");

        addAnalyzerType(_奇偶比, "奇偶比走势");
        addAnalyzerType(_奇偶个数, "奇偶个数走势");
        addAnalyzerType(_奇偶形态, "奇偶形态走势");

        addAnalyzerType(_大小比, "大小比走势");
        addAnalyzerType(_大小个数, "大小个数走势");
        addAnalyzerType(_大小形态, "大小形态走势");

        addAnalyzerType(_质合比, "质合比走势");
        addAnalyzerType(_质合个数, "质合个数走势");
        addAnalyzerType(_质合形态, "质合形态走势");


        addAnalyzerType(_和值, "和值走势");
        addAnalyzerType(_和尾, "和尾走势");

        addAnalyzerType(_跨度, "跨度走势");
    }

    @Override
    public TypeToken getTypeToken() {
        return new TypeToken<RestResponse<ArrayList<TendencySDRX5>>>() {};
    }

    @Override
    public void process(AnalyzerType type) {
        rowInfos = new ArrayList<>();
        titleInfos = new ArrayList<>();
        if (trendJson == null) {
            return;
        }

        addIssueRowInfo(2);
        addCodeRowInfo(2);

        switch (type.getId()) {
            case _综合:
                general();
                break;

            case _奇偶比:
                oddEvenRatio();
                break;
            case _奇偶个数:
                oddEvenNum();
                break;
            case _奇偶形态:
                oddEvenForm();
                break;

            case _大小比:
                bigSmallRatio();
                break;
            case _大小个数:
                bigSmallNum();
                break;
            case _大小形态:
                bigSmallForm();
                break;

            case _质合比:
                primCompositeRatio();
                break;
            case _质合个数:
                primCompositeNum();
                break;
            case _质合形态:
                primCompositeForm();
                break;

            case _和值:
                sumValue();
                break;
            case _和尾:
                sumEndValue();
                break;
            case _跨度:
                differenceValue();
                break;
        }
    }

    /** 跨度走势 */
    private void differenceValue() {
        String[] codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length - 1 - i)).differenceValue.differenceValue;
        }
        commonOneLineRow("跨度", 2, 2, codes);

        LinkedHashMap<String, String>[] linkedHashMaps = new LinkedHashMap[trendJson.size()];
        for (int i = 0; i < linkedHashMaps.length; i++) {
            linkedHashMaps[i] = ((TendencySDRX5) trendJson.get(linkedHashMaps.length - i - 1))
                    .differenceValue.codeMiss;
        }
        commonTwoLineIteratorTable("跨度走势", 1, linkedHashMaps);
    }

    /** 和尾 */
    private void sumEndValue(){
        //和值列
        String[] codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length - 1 - i)).sumValue.sumValue;
        }
        commonOneLineRow("和值", 2, 2, codes);

        //和尾列
        codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length - 1 - i)).sumValue.sumEndValue;
        }
        commonOneLineRow("和尾", 2, 2, codes);

        //和尾分布
        String[] subTitle = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[][] codeMiss = new String[trendJson.size()][];
        for (int i = 0; i < codeMiss.length; i++) {
            codeMiss[i] = ((TendencySDRX5) trendJson.get(i)).sumValue.sumEndValueMiss;
        }
        commonTwoLineRow("和尾", subTitle, 1, codeMiss);
    }

    /** 和值分布 */
    private void sumValue() {
        //和值列
        String[] codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length - 1 - i)).sumValue.sumValue;
        }
        commonOneLineRow("和值", 2, 2, codes);

        LinkedHashMap<String, String>[] linkedHashMaps = new LinkedHashMap[trendJson.size()];
        for (int i = 0; i < linkedHashMaps.length; i++) {
            linkedHashMaps[i] = ((TendencySDRX5) trendJson.get(linkedHashMaps.length - i - 1))
                    .sumValue.sumValueMiss;
        }
        commonTwoLineIteratorTable("和值分布", 1, linkedHashMaps);
    }

    /** 奇偶比 */
    private void oddEvenRatio() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("奇偶比", 0, y++, 12, 1, colorBg, colorTitle, sizeTitleText, false);
        String[] subTitle = {"5:0", "4:1", "3:2", "2:3", "1:4", "0:5"};
        for (int i = 0; i < subTitle.length; i++) {
            rowInfo.addUnitInfo(subTitle[i], i*2, y, 2, 1,
                    colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            int i = 0;
            TendencySDRX5.ScaleMiss miss = ((TendencySDRX5) trendJson.get(itemIndex))
                    .oddEven.scaleMiss;
            oddEvenRatioSub(rowInfo, i, y, subTitle[0], miss.scale_50);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[1], miss.scale_41);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[2], miss.scale_32);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[3], miss.scale_23);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[4], miss.scale_14);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[5], miss.scale_05);
            y++;
        }

        rowInfos.add(rowInfo);
    }

    /** 大小比 */
    private void bigSmallRatio() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("大小比", 0, y++, 12, 1, colorBg, colorTitle, sizeTitleText, false);
        String[] subTitle = {"5:0", "4:1", "3:2", "2:3", "1:4", "0:5"};
        for (int i = 0; i < subTitle.length; i++) {
            rowInfo.addUnitInfo(subTitle[i], i*2, y, 2, 1,
                    colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            int i = 0;
            TendencySDRX5.ScaleMiss miss = ((TendencySDRX5) trendJson.get(itemIndex))
                    .bigSmall.scaleMiss;
            oddEvenRatioSub(rowInfo, i, y, subTitle[0], miss.scale_50);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[1], miss.scale_41);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[2], miss.scale_32);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[3], miss.scale_23);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[4], miss.scale_14);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[5], miss.scale_05);
            y++;
        }

        rowInfos.add(rowInfo);
    }

    /** 质合比 */
    private void primCompositeRatio() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("质合比", 0, y++, 12, 1, colorBg, colorTitle, sizeTitleText, false);
        String[] subTitle = {"5:0", "4:1", "3:2", "2:3", "1:4", "0:5"};
        for (int i = 0; i < subTitle.length; i++) {
            rowInfo.addUnitInfo(subTitle[i], i*2, y, 2, 1,
                    colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            int i = 0;
            TendencySDRX5.ScaleMiss miss = ((TendencySDRX5) trendJson.get(itemIndex))
                    .primComposite.scaleMiss;
            oddEvenRatioSub(rowInfo, i, y, subTitle[0], miss.scale_50);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[1], miss.scale_41);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[2], miss.scale_32);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[3], miss.scale_23);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[4], miss.scale_14);
            i += 2;
            oddEvenRatioSub(rowInfo, i, y, subTitle[5], miss.scale_05);
            y++;
        }

        rowInfos.add(rowInfo);
    }

    private void oddEvenRatioSub(CodeFormView.RowInfo rowInfo, int i, int y, String h, String code) {
        if ("0".equals(code)) {
            rowInfo.addUnitInfo(h, i, y, 2, 1,
                    Color.WHITE, Color.RED, sizeCodeText, true);
        } else {
            rowInfo.addUnitInfo(code, i, y, 2, 1,
                    Color.WHITE, colorMiss, sizeCodeText, false);
        }
    }

    /** 大小个数 */
    private void bigSmallNum() {
        String[] subTitle = {"0", "1", "2", "3", "4", "5"};
        String[][] codeMiss = new String[trendJson.size()][];
        for (int i = 0; i < codeMiss.length; i++) {
            codeMiss[i] = ((TendencySDRX5) trendJson.get(i)).bigSmall.bigNum;
        }
        commonTwoLineRow("大数个数", subTitle, 1, codeMiss);

        codeMiss = new String[trendJson.size()][];
        for (int i = 0; i < codeMiss.length; i++) {
            codeMiss[i] = ((TendencySDRX5) trendJson.get(i)).bigSmall.smallNum;
        }
        commonTwoLineRow("小数个数", subTitle, 1, codeMiss);

        String[] codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length-1-i)).bigSmall.bigSmallNumScale;
        }
        commonOneLineRow("大小比", 2, 2, codes);
    }

    /** 质合个数 */
    private void primCompositeNum() {
        String[] subTitle = {"0", "1", "2", "3", "4", "5"};
        String[][] codeMiss = new String[trendJson.size()][];
        for (int i = 0; i < codeMiss.length; i++) {
            codeMiss[i] = ((TendencySDRX5) trendJson.get(i)).primComposite.primNum;
        }
        commonTwoLineRow("质数个数", subTitle, 1, codeMiss);

        codeMiss = new String[trendJson.size()][];
        for (int i = 0; i < codeMiss.length; i++) {
            codeMiss[i] = ((TendencySDRX5) trendJson.get(i)).primComposite.compositeNum;
        }
        commonTwoLineRow("合数个数", subTitle, 1, codeMiss);


        String[] codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length-1-i)).primComposite.primCompositeNumScale;
        }
        commonOneLineRow("质合比", 2, 2, codes);
    }

    /** 奇偶个数 */
    private void oddEvenNum() {
        String[] subTitle = {"0", "1", "2", "3", "4", "5"};
        String[][] codeMiss = new String[trendJson.size()][];
        for (int i = 0; i < codeMiss.length; i++) {
            codeMiss[i] = ((TendencySDRX5) trendJson.get(i)).oddEven.oddNum;
        }
        commonTwoLineRow("奇数个数", subTitle, 1, codeMiss);

        codeMiss = new String[trendJson.size()][];
        for (int i = 0; i < codeMiss.length; i++) {
            codeMiss[i] = ((TendencySDRX5) trendJson.get(i)).oddEven.evenNum;
        }
        commonTwoLineRow("偶数个数", subTitle, 1, codeMiss);


        String[] codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length-1-i)).oddEven.oddEvenNumScale;
        }
        commonOneLineRow("奇偶比", 2, 2, codes);
    }

    private void oddEvenForm(){
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] subTitle = {"1位", "2位", "3位", "4位", "5位"};
        rowInfo.addUnitInfo("奇偶形态", 0, y++, subTitle.length * 2, 1, colorBg, colorTitle, sizeTitleText, false);
        for (int i = 0; i < subTitle.length; i++) {
            rowInfo.addUnitInfo(subTitle[i], i*2, y, 2, 1,
                    colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;
        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySDRX5 tendency = (TendencySDRX5) trendJson.get(itemIndex);
            int i = 0;
            for (TendencySDRX5.OddEvenForm evenForm : tendency.oddEven.oddEvenForm) {
                if ("0".equals(evenForm.odd)) {
                    rowInfo.addUnitInfo("奇", i++, y, 1, 1,
                            Color.WHITE, Color.RED, sizeCodeText, true);
                    rowInfo.addUnitInfo(evenForm.even, i++, y, 1, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                } else {
                    rowInfo.addUnitInfo(evenForm.odd, i++, y, 1, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                    rowInfo.addUnitInfo("偶", i++, y, 1, 1,
                            Color.WHITE, Color.RED, sizeCodeText, true);
                }
            }
            y++;
        }
        rowInfos.add(rowInfo);

        String[] codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length-1-i)).oddEven.oddEvenFormText;
        }
        commonOneLineRow("形态", 2, 3, codes);
    }

    private void bigSmallForm(){
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] subTitle = {"1位", "2位", "3位", "4位", "5位"};
        rowInfo.addUnitInfo("大小形态", 0, y++, subTitle.length * 2, 1, colorBg, colorTitle, sizeTitleText, false);
        for (int i = 0; i < subTitle.length; i++) {
            rowInfo.addUnitInfo(subTitle[i], i*2, y, 2, 1,
                    colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;
        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySDRX5 tendency = (TendencySDRX5) trendJson.get(itemIndex);
            int i = 0;
            for (TendencySDRX5.BigSmallForm evenForm : tendency.bigSmall.bigSmallForm) {
                if ("0".equals(evenForm.big)) {
                    rowInfo.addUnitInfo("大", i++, y, 1, 1,
                            Color.WHITE, Color.RED, sizeCodeText, true);
                    rowInfo.addUnitInfo(evenForm.small, i++, y, 1, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                } else {
                    rowInfo.addUnitInfo(evenForm.big, i++, y, 1, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                    rowInfo.addUnitInfo("小", i++, y, 1, 1,
                            Color.WHITE, Color.RED, sizeCodeText, true);
                }
            }
            y++;
        }
        rowInfos.add(rowInfo);

        String[] codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length-1-i)).bigSmall.bigSmallFormText;
        }
        commonOneLineRow("形态", 2, 3, codes);
    }

    private void primCompositeForm(){
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] subTitle = {"1位", "2位", "3位", "4位", "5位"};
        rowInfo.addUnitInfo("质合形态", 0, y++, subTitle.length * 2, 1, colorBg, colorTitle, sizeTitleText, false);
        for (int i = 0; i < subTitle.length; i++) {
            rowInfo.addUnitInfo(subTitle[i], i*2, y, 2, 1,
                    colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;
        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySDRX5 tendency = (TendencySDRX5) trendJson.get(itemIndex);
            int i = 0;
            for (TendencySDRX5.PrimCompositeForm evenForm : tendency.primComposite.primCompositeForm) {
                if ("0".equals(evenForm.prim)) {
                    rowInfo.addUnitInfo("质", i++, y, 1, 1,
                            Color.WHITE, Color.RED, sizeCodeText, true);
                    rowInfo.addUnitInfo(evenForm.composite, i++, y, 1, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                } else {
                    rowInfo.addUnitInfo(evenForm.prim, i++, y, 1, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                    rowInfo.addUnitInfo("合", i++, y, 1, 1,
                            Color.WHITE, Color.RED, sizeCodeText, true);
                }
            }
            y++;
        }
        rowInfos.add(rowInfo);

        String[] codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length-1-i)).primComposite.primCompositeFormText;
        }
        commonOneLineRow("形态", 2, 3, codes);
    }

    /** 综合走势 */
    private void general() {
        String[] subTitle = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"};
        String[][] codeMiss = new String[trendJson.size()][];
        for (int i = 0; i < codeMiss.length; i++) {
            codeMiss[i] = ((TendencySDRX5) trendJson.get(codeMiss.length - i - 1))
                    .generalTendency.codeMiss;
        }
        commonTwoLineRow("综合走势", subTitle, 1, codeMiss);

        String[] codes = new String[trendJson.size()];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = ((TendencySDRX5) trendJson.get(codes.length - i - 1)).generalTendency.hezhi;
        }
        commonOneLineRow("和值", 2, 2, codes);
    }

    /** 期号列 */
    private void addIssueRowInfo(int high) {
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth * 2;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("期号", 0, 0, 1, high, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth * 2;
        rowInfo.unitH = unitHeight;
        int y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySDRX5 tendency = (TendencySDRX5) trendJson.get(itemIndex);
            int length = tendency.issue.length();
            if (length > 7) {
                rowInfo.addUnitInfo(tendency.issue.substring(length - 7), 0, y, 1, 1,
                        colorBg, Color.BLACK, sizeTitleText, false);
            } else {
                rowInfo.addUnitInfo(tendency.issue, 0, y, 1, 1, colorBg, Color.BLACK, sizeTitleText, false);
            }
            y++;
        }

        rowInfos.add(rowInfo);
    }

    /** 开奖号码列 */
    private void addCodeRowInfo(int high) {
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = (int) (unitWidth * 1.5);
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("号码", 0, 0, 3, high, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = (int) (unitWidth * 1.5);
        rowInfo.unitH = unitHeight;
        int y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySDRX5 tendency = (TendencySDRX5) trendJson.get(itemIndex);
            rowInfo.addUnitInfo(tendency.code, 0, y, 3, 1,
                    Color.WHITE, colorTitle, sizeTitleText, false);
            y++;
        }

        rowInfos.add(rowInfo);
    }
}