package com.shrinktool.Analyzer;

import android.graphics.Color;

import com.google.gson.reflect.TypeToken;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.TendencySsq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 双色球的走势处理
 * Created by Alashi on 2016/8/11.
 */
public class TrendProcessorSsq extends TrendProcessor{

    private static final int _综合 = 1;
    private static final int _红球三分区 = 2;
    private static final int _红球大小 = 3;
    private static final int _红球奇偶 = 4;
    private static final int _红球质合 = 5;
    private static final int _红球和值和尾 = 6;
    private static final int _红球首尾跨度 = 7;
    private static final int _红球尾数 = 8;
    private static final int _蓝球 = 9;
    
    private String[] redText = new String[]{
            "01","02","03","04","05","06","07","08","09","10","11","12","13","14","15",
            "16","17","18","19","20","21","22","23","24","25","26","27","28","29","30",
            "31","32","33"};
    private String[] blueText = new String[]{
            "01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16"};

    public TrendProcessorSsq(int lotteryId, int methodId) {
        super(lotteryId, methodId);
        addAnalyzerType(_综合, "综合走势");
        addAnalyzerType(_红球三分区, "红球三分区走势");
        addAnalyzerType(_红球大小, "红球大小走势");
        addAnalyzerType(_红球奇偶, "红球奇偶走势");
        addAnalyzerType(_红球质合, "红球质合走势");
        addAnalyzerType(_红球和值和尾, "红球和值和尾走势");
        addAnalyzerType(_红球首尾跨度, "红球首尾跨度走势");
        addAnalyzerType(_红球尾数, "红球尾数走势");
        addAnalyzerType(_蓝球, "蓝球走势");
    }

    @Override
    public TypeToken getTypeToken() {
        return new TypeToken<RestResponse<ArrayList<TendencySsq>>>() {};
    }

    @Override
    public void process(AnalyzerType type) {
        rowInfos = new ArrayList<>();
        titleInfos = new ArrayList<>();
        if (trendJson == null) {
            return;
        }

        processTitleInfo(type);

        switch (type.getId()) {
            case _综合:
                generalRed();
                generalBlue();
                break;
            case _红球三分区:
                redThreeArea1();
                redThreeArea2();
                redThreeArea3();
                break;
            case _红球奇偶:
            case _红球大小:
            case _红球质合:
                redBallList(type, 1);
                redOddEven2(type);
                redOddEven3(type);
                redOddEven4(type);
                break;
            case _红球和值和尾:
                redBallList(type, 1);
                redSumValue1(true);
                redSumValue2();
                redSumValue1(false);
                redSumValue3();
                break;
            case _红球首尾跨度:
                redBallList(type, 2);
                redDifferenceValue1();
                redDifferenceValue2();
                break;
            case _红球尾数:
                redBallList(type, 2);
                redEndValue1();
                redEndValue2();
                break;
            case _蓝球:
                blueGeneral1();
                blueGeneral2();
                blueGeneral3();
                break;
        }
    }

    private void processTitleInfo(AnalyzerType type) {
        switch (type.getId()) {
            case _综合:
            case _红球首尾跨度:
            case _红球尾数:
            case _蓝球:
                addIssueRowInfo(2);
                break;
            case _红球三分区:
                addIssueRowInfo(3);
                break;
            case _红球大小:
            case _红球奇偶:
            case _红球质合:
            case _红球和值和尾:
                addIssueRowInfo(1);
                break;
        }
    }

    protected void addIssueRowInfo(int high) {
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
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
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

    private void generalRed() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("红球号码分布", 0, y++, 33, 1, colorBg, colorTitle, sizeTitleText, false);

        for (int i = 0; i < 33; i++) {
            rowInfo.addUnitInfo(redText[i], i, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;

        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            Iterator<Map.Entry<String, String>> iterator = tendency.generalTendency.redCodeMiss.entrySet().iterator();
            int i = 0;
            while(iterator.hasNext()){
                String code = iterator.next().getValue();
                if ("0".equals(code)) {
                    rowInfo.addUnitInfo(redText[i], i, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                } else if ("DTF".contains(code)) {
                    CodeFormView.UnitInfo unitInfo = rowInfo.addUnitInfo(redText[i], i, y, 1, 1,
                            Color.WHITE, Color.RED,sizeCodeText, true);
                    if ("D".equals(code)) {
                        unitInfo.smallText = "②";
                    } else if ("T".equals(code)) {
                        unitInfo.smallText = "③";
                    } else {
                        unitInfo.smallText = "④";
                    }
                } else {
                    rowInfo.addUnitInfo(code, i, y, 1, 1, Color.WHITE, colorMiss, sizeCodeText, false);
                }
                i++;
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void generalBlue() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("蓝球号码分布", 0, y++, 16, 1, colorBg, colorTitle, sizeTitleText, false);

        for (int i = 0; i < 16; i++) {
            rowInfo.addUnitInfo(blueText[i], i, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;

        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            Iterator<Map.Entry<String, String>> iterator = tendency.generalTendency.blueCodeMiss.entrySet().iterator();
            int i = 0;
            while(iterator.hasNext()){
                String code = iterator.next().getValue();
                if ("0".equals(code)) {
                    rowInfo.addUnitInfo(blueText[i], i, y, 1, 1, Color.WHITE, Color.BLUE, sizeCodeText, true);
                } else if ("DTF".contains(code)) {
                    CodeFormView.UnitInfo unitInfo = rowInfo.addUnitInfo(blueText[i], i, y, 1, 1,
                            Color.WHITE, Color.BLUE, sizeCodeText, true);
                    if ("D".equals(code)) {
                        unitInfo.smallText = "②";
                    } else if ("T".equals(code)) {
                        unitInfo.smallText = "③";
                    } else {
                        unitInfo.smallText = "④";
                    }
                } else {
                    rowInfo.addUnitInfo(code, i, y, 1, 1, Color.WHITE, colorMiss, sizeCodeText, false);
                }
                i++;
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void redThreeArea1(){
        int y = 0;
        int x = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] keys = new String[]{"一区","二区","三区"};
        for (String key : keys) {
            rowInfo.addUnitInfo(key, x, y, 11, 1, colorBg, colorTitle, sizeTitleText, false);
            x += 11;
        }
        y++;
        x = 0;
        keys = new String[]{"一区左","一区右","二区左","二区右","三区左","三区右"};
        for (int i = 0; i < keys.length; i+=2) {
            rowInfo.addUnitInfo(keys[i], x, y, 5, 1, colorBg, colorTitle, sizeTitleText, false);
            rowInfo.addUnitInfo(keys[i+1], x + 5, y, 6, 1, colorBg, colorTitle, sizeTitleText, false);
            x += 11;
        }

        y++;
        for (int i = 0; i < 33; i++) {
            rowInfo.addUnitInfo(redText[i], i, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            Iterator<Map.Entry<String, String>> iterator = tendency.redThreeArea.area.entrySet().iterator();
            int i = 0;
            while(iterator.hasNext()){
                String code = iterator.next().getValue();
                if ("0".equals(code)) {
                    rowInfo.addUnitInfo(redText[i], i, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                } else if ("DTF".contains(code)) {
                    CodeFormView.UnitInfo unitInfo = rowInfo.addUnitInfo(redText[i], i, y, 1, 1,
                            Color.WHITE, Color.RED, sizeCodeText, true);
                    if ("D".equals(code)) {
                        unitInfo.smallText = "②";
                    } else if ("T".equals(code)) {
                        unitInfo.smallText = "③";
                    } else {
                        unitInfo.smallText = "④";
                    }
                } else {
                    rowInfo.addUnitInfo(code, i, y, 1, 1, Color.WHITE, colorMiss, sizeCodeText, false);
                }
                i++;
            }
            y++;
        }

        rowInfos.add(rowInfo);
    }

    private void redThreeArea2(){
        int y = 0;
        int x = 0;
        int w1 = 2;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] keys = new String[]{"一区","二区","三区","一区左","一区右","二区左","二区右","三区左","三区右"};
        rowInfo.addUnitInfo("区间出现个数", 0, y, keys.length * w1, 1, colorBg, colorTitle, sizeTitleText, false);
        y++;
        for (String key : keys) {
            rowInfo.addUnitInfo(key, x, y, w1, 2, colorBg, colorTitle, sizeTitleText, false);
            x += w1;
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            x = 0;
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            rowInfo.addUnitInfo(tendency.redThreeArea.area1, x, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            x += w1;
            rowInfo.addUnitInfo(tendency.redThreeArea.area2, x, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            x += w1;
            rowInfo.addUnitInfo(tendency.redThreeArea.area3, x, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            x += w1;
            rowInfo.addUnitInfo(tendency.redThreeArea.area1_left, x, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            x += w1;
            rowInfo.addUnitInfo(tendency.redThreeArea.area1_right, x, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            x += w1;
            rowInfo.addUnitInfo(tendency.redThreeArea.area2_left, x, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            x += w1;
            rowInfo.addUnitInfo(tendency.redThreeArea.area2_right, x, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            x += w1;
            rowInfo.addUnitInfo(tendency.redThreeArea.area3_left, x, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            x += w1;
            rowInfo.addUnitInfo(tendency.redThreeArea.area3_right, x, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            y++;
        }

        rowInfos.add(rowInfo);
    }

    private void redThreeArea3() {
        int y = 0;
        int w1 = 2;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("三区比", 0, y, w1, 3, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            rowInfo.addUnitInfo(tendency.redThreeArea.ratio, 0, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void redBallList(AnalyzerType type, int titleHeight){
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("红球", 0, y, 6, titleHeight, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            int[] redBall = new int[0];
            switch (type.getId()) {
                case _红球大小:
                    redBall = tendency.redBigSmall.redBall;
                    break;
                case _红球奇偶:
                    redBall = tendency.redOddEven.redBall;
                    break;
                case _红球质合:
                    redBall = tendency.redPrimComposite.redBall;
                    break;
                case _红球和值和尾:
                    redBall = tendency.redSumValue.redBall;
                    break;
                case _红球首尾跨度:
                    redBall = tendency.redDifferenceValue.redBall;
                    break;
                case _红球尾数:
                    redBall = tendency.redEndValue.redBall;
                    break;
                default:
                    throw new UnsupportedOperationException("不支持的类型: " + type);
            }
            if (redBall != null) {
                for (int i = 0; i < 6; i++) {
                    rowInfo.addUnitInfo(redText[redBall[i] - 1], i, y, 1, 1,
                            colorBg, Color.BLACK, sizeTitleText, false);
                }
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void redOddEven2(AnalyzerType type){
        int x = 0;
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] keys = new String[]{"红一","红二","红三","红四","红五","红六"};
        for (String key : keys) {
            rowInfo.addUnitInfo(key, x, y, 2, 1, colorBg, colorTitle, sizeTitleText, false);
            x += 2;
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            x = 0;
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            switch (type.getId()) {
                case _红球奇偶: {
                    applyRedObbEvenItem(x, y, "奇", "偶",
                            tendency.redOddEven.red1.odd, tendency.redOddEven.red1.even, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "奇", "偶",
                            tendency.redOddEven.red2.odd, tendency.redOddEven.red2.even, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "奇", "偶",
                            tendency.redOddEven.red3.odd, tendency.redOddEven.red3.even, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "奇", "偶",
                            tendency.redOddEven.red4.odd, tendency.redOddEven.red4.even, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "奇", "偶",
                            tendency.redOddEven.red5.odd, tendency.redOddEven.red5.even, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "奇", "偶",
                            tendency.redOddEven.red6.odd, tendency.redOddEven.red6.even, rowInfo);
                    break;
                }
                case _红球大小: {
                    applyRedObbEvenItem(x, y, "小", "大",
                            tendency.redBigSmall.red1.small, tendency.redBigSmall.red1.big, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "小", "大",
                            tendency.redBigSmall.red2.small, tendency.redBigSmall.red2.big, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "小", "大",
                            tendency.redBigSmall.red3.small, tendency.redBigSmall.red3.big, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "小", "大",
                            tendency.redBigSmall.red4.small, tendency.redBigSmall.red4.big, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "小", "大",
                            tendency.redBigSmall.red5.small, tendency.redBigSmall.red5.big, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "小", "大",
                            tendency.redBigSmall.red6.small, tendency.redBigSmall.red6.big, rowInfo);
                    break;
                }
                case _红球质合: {
                    applyRedObbEvenItem(x, y, "质", "合",
                            tendency.redPrimComposite.red1.prim, tendency.redPrimComposite.red1.composite, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "质", "合",
                            tendency.redPrimComposite.red2.prim, tendency.redPrimComposite.red2.composite, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "质", "合",
                            tendency.redPrimComposite.red3.prim, tendency.redPrimComposite.red3.composite, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "质", "合",
                            tendency.redPrimComposite.red4.prim, tendency.redPrimComposite.red4.composite, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "质", "合",
                            tendency.redPrimComposite.red5.prim, tendency.redPrimComposite.red5.composite, rowInfo);
                    x += 2;
                    applyRedObbEvenItem(x, y, "质", "合",
                            tendency.redPrimComposite.red6.prim, tendency.redPrimComposite.red6.composite, rowInfo);
                    break;
                }
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void applyRedObbEvenItem(int x, int y, String text1, String text2, String key1, String key2,
                                     CodeFormView.RowInfo rowInfo) {
        if ("0".equals(key1)) {
            rowInfo.addUnitInfo(text1, x, y, 1, 1,
                    colorBg, Color.RED, sizeTitleText, false);
        } else {
            rowInfo.addUnitInfo(key1, x, y, 1, 1,
                    colorBg, colorMiss, sizeTitleText, false);
        }

        if ("0".equals(key2)) {
            rowInfo.addUnitInfo(text2, x + 1, y, 1, 1,
                    colorBg, Color.BLUE, sizeTitleText, false);
        } else {
            rowInfo.addUnitInfo(key2, x + 1, y, 1, 1,
                    colorBg, colorMiss, sizeTitleText, false);
        }
    }

    private void redOddEven3(AnalyzerType type){
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] keys = null;
        switch (type.getId()) {
            case _红球大小:
                keys = new String[]{"奇偶大小", "大小比"};
                break;
            case _红球奇偶:
                keys = new String[]{"奇偶排位", "奇偶比"};
                break;
            case _红球质合:
                keys = new String[]{"质合排位", "质合比"};
                break;
        }
        rowInfo.addUnitInfo(keys[0], 0, y, 3, 1, colorBg, colorTitle, sizeTitleText, false);
        rowInfo.addUnitInfo(keys[1], 3, y, 2, 1, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            String position = "";
            String retio = "";
            switch (type.getId()) {
                case _红球大小:
                    position = tendency.redBigSmall.position;
                    retio = tendency.redBigSmall.retio;
                    break;
                case _红球奇偶:
                    position = tendency.redOddEven.position;
                    retio = tendency.redOddEven.retio;
                    break;
                case _红球质合:
                    position = tendency.redPrimComposite.position;
                    retio = tendency.redPrimComposite.retio;
                    break;
            }
            rowInfo.addUnitInfo(position, 0, y, 3, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            rowInfo.addUnitInfo(retio, 3, y, 2, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void redOddEven4(AnalyzerType type){
        int y = 0;
        int x = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] keys = new String[]{"0:6","1:5","2:4","3:3","4:2","5:1","6:0"};
        for (String key : keys) {
            rowInfo.addUnitInfo(key, x, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
            x += 1;
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            LinkedHashMap<String, String> retioMiss = null;
            switch (type.getId()) {
                case _红球大小:
                    retioMiss = tendency.redBigSmall.retioMiss;
                    break;
                case _红球奇偶:
                    retioMiss = tendency.redOddEven.retioMiss;
                    break;
                case _红球质合:
                    retioMiss = tendency.redPrimComposite.retioMiss;
                    break;
                default:
                    break;
            }
            Iterator<Map.Entry<String, String>> iterator = retioMiss.entrySet().iterator();
            int i = 0;
            while(iterator.hasNext()){
                String code = iterator.next().getValue();
                if ("0".equals(code)) {
                    rowInfo.addUnitInfo(keys[i], i, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                } else {
                    rowInfo.addUnitInfo(code, i, y, 1, 1, Color.WHITE, colorMiss, sizeCodeText, false);
                }
                i++;
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void redSumValue1(boolean b) {
        int y = 0;
        int w1 = 2;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo(b? "和值": "和尾", 0, y, w1, 1, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            String value = b? tendency.redSumValue.sum : tendency.redSumValue.sumEnd;
            rowInfo.addUnitInfo(value, 0, y, w1, 1,
                    colorBg, Color.BLACK, sizeTitleText, false);
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void redSumValue2() {
        int y = 0;
        int x = 0;
        int w1 = 2;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] keys = new String[]{"21-49","50-59","60-69","70-79","80-89","90-99",
                "100-109","110-119","120-129","130-139","140-183"};
        for (String key : keys) {
            rowInfo.addUnitInfo(key, x, y, w1, 1, colorBg, colorTitle, sizeTitleText, false);
            x += w1;
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            Iterator<Map.Entry<String, String>> iterator = tendency.redSumValue.sumMiss.entrySet().iterator();
            int i = 0;
            while(iterator.hasNext()){
                String code = iterator.next().getValue();
                if ("0".equals(code)) {
                    int sum = 0;
                    for (int tmp: tendency.redSumValue.redBall) {
                        sum += tmp;
                    }
                    rowInfo.addUnitInfo(String.valueOf(sum), i, y, w1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                } else {
                    rowInfo.addUnitInfo(code, i, y, w1, 1, Color.WHITE, colorMiss, sizeCodeText, false);
                }
                i += w1;
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void redSumValue3() {
        int y = 0;
        int x = 0;
        int w1 = 1;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        for (int i = 0; i < 10; i++) {
            rowInfo.addUnitInfo(String.valueOf(i), x, y, w1, 1, colorBg, colorTitle, sizeTitleText, false);
            x += w1;
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            for (int i = 0; i < 10; i++) {
                if ("0".equals(tendency.redSumValue.sumEndMiss[i])) {
                    rowInfo.addUnitInfo(String.valueOf(i), i, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                } else {
                    rowInfo.addUnitInfo(tendency.redSumValue.sumEndMiss[i], i, y, 1, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                }
            }
            y++;
        }

        rowInfos.add(rowInfo);
    }

    private void redDifferenceValue1(){
        int y = 0;
        int x = 0;
        int w1 = 1;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("首尾跨度", x, y, 28 * w1, 1, colorBg, colorTitle, sizeTitleText, false);
        y++;
        for (int i = 5; i < 33; i++) {
            rowInfo.addUnitInfo(String.valueOf(i), x, y, w1, 1, colorBg, colorTitle, sizeTitleText, false);
            x += w1;
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            Iterator<Map.Entry<String, String>> iterator = tendency.redDifferenceValue.differenceMiss.entrySet().iterator();
            int i = 0;
            while(iterator.hasNext()){
                String code = iterator.next().getValue();
                if ("0".equals(code)) {
                    rowInfo.addUnitInfo(String.valueOf(i + 5), i, y, w1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                } else {
                    rowInfo.addUnitInfo(code, i, y, w1, 1, Color.WHITE, colorMiss, sizeCodeText, false);
                }
                i += w1;
            }
            y++;
        }

        rowInfos.add(rowInfo);
    }

    private void redDifferenceValue2() {
        int y = 0;
        int x = 0;
        int w1 = 2;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] keys = new String[]{"奇偶","大小","201路"};
        for (int i = 0; i < keys.length; i++) {
            rowInfo.addUnitInfo(keys[i], x, y, i==2 ? 3 : 2, 1, colorBg, colorTitle, sizeTitleText, false);
            x += w1;
        }
        y++;
        keys = new String[]{"奇","偶","大","小","0路","1路","2路"};
        x = 0;
        for (String key : keys) {
            rowInfo.addUnitInfo(key, x, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
            x += 1;
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            int i = 0;
            if (tendency.redDifferenceValue.odd == 0) {
                rowInfo.addUnitInfo("奇", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                rowInfo.addUnitInfo(String.valueOf(tendency.redDifferenceValue.even), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
            } else {
                rowInfo.addUnitInfo(String.valueOf(tendency.redDifferenceValue.odd), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo("偶", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
            }

            if (tendency.redDifferenceValue.big == 0) {
                rowInfo.addUnitInfo("大", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                rowInfo.addUnitInfo(String.valueOf(tendency.redDifferenceValue.small), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
            } else {
                rowInfo.addUnitInfo(String.valueOf(tendency.redDifferenceValue.big), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo("小", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
            }

            if (tendency.redDifferenceValue.mod0 == 0) {
                rowInfo.addUnitInfo("0路", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                rowInfo.addUnitInfo(String.valueOf(tendency.redDifferenceValue.mod1), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo(String.valueOf(tendency.redDifferenceValue.mod2), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
            } else if (tendency.redDifferenceValue.mod1 == 0){
                rowInfo.addUnitInfo(String.valueOf(tendency.redDifferenceValue.mod0), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo("1路", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                rowInfo.addUnitInfo(String.valueOf(tendency.redDifferenceValue.mod2), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
            } else {
                rowInfo.addUnitInfo(String.valueOf(tendency.redDifferenceValue.mod0), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo(String.valueOf(tendency.redDifferenceValue.mod1), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo("2路", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
            }

            y++;
        }

        rowInfos.add(rowInfo);
    }

    private void redEndValue1() {
        int y = 0;
        int x = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] keys = new String[]{"尾0","尾1","尾2","尾3","尾4","尾5","尾6","尾7","尾8","尾9"};
        int[] keyW = new int[]{3,4,4,4,3,3,3,3,3,3};
        for (int i = 0; i < keys.length; i++) {
            rowInfo.addUnitInfo(keys[i], x, y, keyW[i], 1, colorBg, colorTitle, sizeTitleText, false);
            x += keyW[i];
        }
        y++;

        /*int[] usbTitleText = new int[]{10,20,30,1,11,21,31,2,12,22,32,
                3,13,23,33,4,14,24,5,15,25,6,16,26,7,17,27,8,18,28,9,19,29};*/
        int[] usbIndex = new int[]{3, 7, 11, 15, 18, 21, 24, 27, 30, 0, 4, 8, 12, 16, 19, 22, 25, 28, 31,
                1, 5, 9, 13, 17, 20, 23, 26, 29, 32, 2, 6, 10, 14};
        for (int i = 0; i < redText.length; i++) {
            rowInfo.addUnitInfo(redText[i], usbIndex[i], y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            Iterator<Map.Entry<String, String>> iterator = tendency.generalTendency.redCodeMiss.entrySet().iterator();
            int i = 0;
            while(iterator.hasNext()){
                String code = iterator.next().getValue();
                if ("0".equals(code)) {
                    rowInfo.addUnitInfo(redText[i], usbIndex[i], y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                } else {
                    rowInfo.addUnitInfo(code, usbIndex[i], y, 1, 1, Color.WHITE, colorMiss, sizeCodeText, false);
                }
                i++;
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void redEndValue2() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("尾数统计", 0, y++, 10, 1, colorBg, colorTitle, sizeTitleText, false);
        for (int i = 0; i < 10; i++) {
            rowInfo.addUnitInfo(String.valueOf(i), i, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            for (int i = 0; i < 10; i++) {
                String code = tendency.redEndValue.endValue[i];

                if ("0".equals(code)) {
                    rowInfo.addUnitInfo(String.valueOf(i), i, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                } else if ("DTF".contains(code)) {
                    CodeFormView.UnitInfo unitInfo = rowInfo.addUnitInfo(String.valueOf(i), i, y, 1, 1,
                            Color.WHITE, Color.RED, sizeCodeText, true);
                    if ("D".equals(code)) {
                        unitInfo.smallText = "②";
                    } else if ("T".equals(code)) {
                        unitInfo.smallText = "③";
                    } else {
                        unitInfo.smallText = "④";
                    }
                } else {
                    rowInfo.addUnitInfo(code, i, y, 1, 1, Color.WHITE, colorMiss, sizeCodeText, false);
                }
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void blueGeneral1(){
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("蓝球", 0, y, 1, 2, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            rowInfo.addUnitInfo(blueText[tendency.blueGeneral.blueBall - 1], 0, y, 1, 1,
                    Color.WHITE, Color.BLUE, sizeCodeText, true);
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void blueGeneral2(){
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("蓝球走势", 0, y++, 16, 1, colorBg, colorTitle, sizeTitleText, false);

        for (int i = 0; i < blueText.length; i++) {
            rowInfo.addUnitInfo(blueText[i], i, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            Iterator<Map.Entry<String, String>> iterator = tendency.blueGeneral.ballMiss.entrySet().iterator();
            int i = 0;
            while(iterator.hasNext()){
                String code = iterator.next().getValue();
                if ("0".equals(code)) {
                    rowInfo.addUnitInfo(redText[i], i, y, 1, 1, Color.WHITE, Color.BLUE, sizeCodeText, true);
                } else {
                    rowInfo.addUnitInfo(code, i, y, 1, 1, Color.WHITE, colorMiss, sizeCodeText, false);
                }
                i++;
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }

    private void blueGeneral3(){
        int y = 0;
        int x = 0;
        int w1 = 2;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        String[] keys = new String[]{"奇偶","大小","201路"};
        for (int i = 0; i < keys.length; i++) {
            rowInfo.addUnitInfo(keys[i], x, y, i==2 ? 3 : 2, 1, colorBg, colorTitle, sizeTitleText, false);
            x += w1;
        }
        y++;
        keys = new String[]{"奇","偶","大","小","0路","1路","2路"};
        x = 0;
        for (String key : keys) {
            rowInfo.addUnitInfo(key, x, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
            x += 1;
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            TendencySsq tendency = (TendencySsq) trendJson.get(itemIndex);
            int i = 0;
            if (tendency.blueGeneral.odd == 0) {
                rowInfo.addUnitInfo("奇", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                rowInfo.addUnitInfo(String.valueOf(tendency.blueGeneral.even), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
            } else {
                rowInfo.addUnitInfo(String.valueOf(tendency.blueGeneral.odd), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo("偶", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
            }

            if (tendency.blueGeneral.big == 0) {
                rowInfo.addUnitInfo("大", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                rowInfo.addUnitInfo(String.valueOf(tendency.blueGeneral.small), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
            } else {
                rowInfo.addUnitInfo(String.valueOf(tendency.blueGeneral.big), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo("小", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
            }

            if (tendency.blueGeneral.mod0 == 0) {
                rowInfo.addUnitInfo("0路", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                rowInfo.addUnitInfo(String.valueOf(tendency.blueGeneral.mod1), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo(String.valueOf(tendency.blueGeneral.mod2), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
            } else if (tendency.blueGeneral.mod1 == 0){
                rowInfo.addUnitInfo(String.valueOf(tendency.blueGeneral.mod0), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo("1路", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
                rowInfo.addUnitInfo(String.valueOf(tendency.blueGeneral.mod2), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
            } else {
                rowInfo.addUnitInfo(String.valueOf(tendency.blueGeneral.mod0), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo(String.valueOf(tendency.blueGeneral.mod1), i++, y, 1, 1,
                        Color.WHITE, colorMiss, sizeCodeText, false);
                rowInfo.addUnitInfo("2路", i++, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, true);
            }

            y++;
        }

        rowInfos.add(rowInfo);
    }
}
