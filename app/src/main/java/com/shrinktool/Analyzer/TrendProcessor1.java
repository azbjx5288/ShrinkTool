package com.shrinktool.Analyzer;

import android.graphics.Color;

import com.google.gson.reflect.TypeToken;
import com.shrinktool.Analyzer.CodeFormView.UnitInfo;
import com.shrinktool.Analyzer.CodeFormView.UnitTextDrawer;
import com.shrinktool.base.net.RestResponse;
import com.shrinktool.data.Tendency1;

import java.util.ArrayList;

/**
 * 适用于福彩3D等彩种，号码是“百十个”的
 * Created by Alashi on 2016/8/8.
 */
public class TrendProcessor1 extends TrendProcessor{

    private static final int _综合 = 1;
    private static final int _直选定位 = 2;
    private static final int _奇偶 = 3;
    private static final int _大小 = 4;
    private static final int _质合 = 5;
    private static final int _和值 = 6;
    private static final int _跨度 = 7;

    private UnitTextDrawer unitTextDrawer;

    public TrendProcessor1(int lotteryId, int methodId) {
        super(lotteryId, methodId);
        addAnalyzerType(_综合, "综合走势");
        addAnalyzerType(_直选定位, "直选定位走势");
        addAnalyzerType(_奇偶, "奇偶走势");
        addAnalyzerType(_大小, "大小走势");
        addAnalyzerType(_质合, "质合走势");
        addAnalyzerType(_和值, "和值走势");
        addAnalyzerType(_跨度, "跨度走势");

        if (methodId == 1 || methodId == 103 || methodId == 190) {
            unitTextDrawer = new UnitTextSplitDrawer();
        }
    }

    @Override
    public TypeToken getTypeToken() {
        return new TypeToken<RestResponse<ArrayList<Tendency1>>>() {};
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
                addCodeRowInfo(2);
                group();
                sum();
                span();
                codeTable();
                break;

            case _直选定位:
                addCodeRowInfo(2);
                group();
                sum();
                span();
                splitCode(0, "百位");
                splitCode(1, "十位");
                splitCode(2, "个位");
                break;

            case _奇偶:
                addCodeRowInfo(3);
                splitCodeList(0, "百位", type);
                splitCodeList(1, "十位", type);
                splitCodeList(2, "个位", type);
                break;

            case _质合:
            case _大小:
                addCodeRowInfo(3);
                splitCodeList(0, "百位", type);
                splitCodeList(1, "十位", type);
                splitCodeList(2, "个位", type);
                ratio(type);
                break;

            case _跨度:
                addCodeRowInfo(1);
                span1();
                break;

            case _和值:
                addCodeRowInfo(1);
                sumList();
                sumList1();
                break;
        }
    }

    private void processTitleInfo(AnalyzerType type) {
        switch (type.getId()) {
            case _跨度:
            case _和值:
                addIssueRowInfo(1);
                break;

            case _综合:
            case _直选定位:
                addIssueRowInfo(2);
                break;

            case _质合:
            case _大小:
            case _奇偶:
            //case _012路:
                addIssueRowInfo(3);
                break;

        }
    }

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
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            int length = tendency1.issue.length();
            if (length > 7) {
                rowInfo.addUnitInfo(tendency1.issue.substring(length - 7), 0, y, 1, 1,
                        colorBg, Color.BLACK, sizeTitleText, false);
            } else {
                rowInfo.addUnitInfo(tendency1.issue, 0, y, 1, 1, colorBg, Color.BLACK, sizeTitleText, false);
            }
            y++;
        }

        rowInfos.add(rowInfo);
    }

    protected void addCodeRowInfo(int high) {
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = (int) (unitWidth * 1.5);
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("号码", 0, 0, 1, high, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = (int) (unitWidth * 1.5);
        rowInfo.unitH = unitHeight;
        int y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            UnitInfo unitInfo = rowInfo.addUnitInfo(tendency1.code.replace(",", ""), 0, y, 1, 1,
                    Color.WHITE, colorTitle, sizeTitleText, false);
            unitInfo.unitTextDrawer = unitTextDrawer;
            y++;
        }

        rowInfos.add(rowInfo);
    }

    /** 组三和组六 */
    private void group() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("类型", 0, y++, 4, 1, colorBg, colorTitle, sizeTitleText, false);
        rowInfo.addUnitInfo("组三", 0, y, 2, 1, colorBg, colorTitle, sizeTitleText, false);
        rowInfo.addUnitInfo("组六", 2, y, 2, 1, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            groupText(rowInfo, tendency1.generalTendency.zusanMiss, tendency1.generalTendency.zuliuMiss, y);
            y++;
        }

        rowInfos.add(rowInfo);
    }

    private void groupText(CodeFormView.RowInfo rowInfo, int zusanMiss, int zuliuMiss, int y) {
        if (zusanMiss == 0 && zuliuMiss == 0) {
            rowInfo.addUnitInfo("豹子", 0, y, 4, 1,
                    Color.WHITE, Color.RED, sizeCodeText, false);
        } else if (zusanMiss == 0) {
            rowInfo.addUnitInfo("三", 0, y, 2, 1,
                    Color.WHITE, Color.RED, sizeCodeText, false);
            rowInfo.addUnitInfo(String.valueOf(zuliuMiss), 2, y, 2, 1,
                    Color.WHITE, colorMiss, sizeCodeText, false);
        } else if (zuliuMiss == 0){
            rowInfo.addUnitInfo(String.valueOf(zusanMiss), 0, y, 2, 1,
                    Color.WHITE, colorMiss, sizeCodeText, false);
            rowInfo.addUnitInfo("六", 2, y, 2, 1,
                    Color.WHITE, Color.RED, sizeCodeText, false);
        } else {
            rowInfo.addUnitInfo(String.valueOf(zusanMiss), 0, y, 2, 1,
                    Color.WHITE, colorMiss, sizeCodeText, false);
            rowInfo.addUnitInfo(String.valueOf(zuliuMiss), 2, y, 2, 1,
                    Color.WHITE, colorMiss, sizeCodeText, false);
        }
    }

    /** 和值(单列) */
    protected void sum() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("和值", 0, y, 2, 2, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            rowInfo.addUnitInfo(String.valueOf(tendency1.generalTendency.hezhi), 0, y++, 2, 1,
                    Color.WHITE, Color.BLACK, sizeCodeText, false);
        }

        rowInfos.add(rowInfo);
    }

    /** 跨度 */
    protected void span() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("跨度", 0, y, 2, 2, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            rowInfo.addUnitInfo(String.valueOf(tendency1.generalTendency.kuadu), 0, y++, 2, 1,
                    Color.WHITE, Color.BLACK, sizeCodeText, false);
        }

        rowInfos.add(rowInfo);
    }

    /** 号码分布 */
    protected void codeTable() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo("号码分布", 0, y++, 10, 1, colorBg, colorTitle, sizeTitleText, false);

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
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            for (int i = 0; i < 10; i++) {
                String code = tendency1.generalTendency.codeMiss[i];
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

    protected void splitCode(int index, String name) {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.supportHighlight = true;
        rowInfo.addUnitInfo(name, 0, y++, 10, 1, colorBg, colorTitle, sizeTitleText, false);

        for (int i = 0; i < 10; i++) {
            rowInfo.addUnitInfo(String.valueOf(i), i, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.supportHighlight = true;
        y = 0;

        int size = trendJson.size();

        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            String[] codeMiss = null;
            switch (index) {
                case 0:
                    codeMiss = tendency1.selectFix.codeMiss0;
                    break;
                case 1:
                    codeMiss = tendency1.selectFix.codeMiss1;
                    break;
                case 2:
                    codeMiss = tendency1.selectFix.codeMiss2;
                    break;
            }

            for (int i = 0; i < 10; i++) {
                if ("0".equals(codeMiss[i])) {
                    rowInfo.addUnitInfo(String.valueOf(i), i, y, 1, 1, Color.WHITE, Color.WHITE, sizeCodeText, true);
                } else {
                    rowInfo.addUnitInfo(codeMiss[i], i, y, 1, 1, Color.WHITE, colorMiss, sizeCodeText, false);
                }
            }

            y++;
        }

        rowInfos.add(rowInfo);
    }

    protected void splitCodeList(int index, String name, AnalyzerType type){
        String[] subTitle = null;
        int[] titleCode = null, dependCode0 = null, dependCode1 = null;

        switch (type.getId()) {
            case _大小:
                subTitle = new String[]{"大", "小", "小", "大"};
                titleCode = new int[]{0,1,2,3,4,5,6,7,8,9};
                dependCode0 = new int[] {5,6,7,8,9};
                dependCode1 = new int[] {0,1,2,3,4};
                break;

            case _质合:
                subTitle = new String[]{"质", "合", "质", "合"};
                titleCode = new int[]{1,2,3,5,7,0,4,6,8,9};
                dependCode0 = new int[] {1,2,3,5,7};
                dependCode1 = new int[] {0,4,6,8,9};
                break;

            case _奇偶:
                subTitle = new String[]{"奇", "偶", "奇", "偶"};
                titleCode = new int[]{1,3,5,7,9,0,2,4,6,8};
                dependCode0 = new int[] {1,3,5,7,9};
                dependCode1 = new int[] {0,2,4,6,8};
                break;
        }

        splitCodeXX(index, name, subTitle, titleCode, dependCode0, dependCode1, type);
    }

    private void splitCodeXX(int index, String name, String[] subTitle, int[] titleCode,
                             int[] dependCode0, int[] dependCode1, AnalyzerType type) {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.supportHighlight = true;
        rowInfo.addUnitInfo(name, 0, y++, 12, 1, colorBg, colorTitle, sizeTitleText, false);
        rowInfo.addUnitInfo(subTitle[0], 0, y, 1, 2, colorBg, colorTitle, sizeTitleText, false);
        rowInfo.addUnitInfo(subTitle[1], 1, y, 1, 2, colorBg, colorTitle, sizeTitleText, false);
        rowInfo.addUnitInfo(subTitle[2], 2, y, 5, 1, colorBg, colorTitle, sizeTitleText, false);
        rowInfo.addUnitInfo(subTitle[3], 7, y, 5, 1, colorBg, colorTitle, sizeTitleText, false);
        y++;
        for (int i = 0; i < 10; i++) {
            rowInfo.addUnitInfo(String.valueOf(titleCode[i]), 2 + i, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.supportHighlight = true;
        y = 0;

        int size = trendJson.size();

        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            switch (type.getId()) {
                case _奇偶:{
                    if (tendency1.oddEven[index].odd == 0) {
                        rowInfo.addUnitInfo(subTitle[0], 0, y, 1, 1, Color.WHITE, colorType, sizeCodeText, false);
                        rowInfo.addUnitInfo(String.valueOf(tendency1.oddEven[index].even), 1, y, 1, 1,
                                Color.WHITE, colorMiss, sizeCodeText, false);
                    } else {
                        rowInfo.addUnitInfo(String.valueOf(tendency1.oddEven[index].odd), 0, y, 1, 1,
                                Color.WHITE, colorMiss, sizeCodeText, false);
                        rowInfo.addUnitInfo(subTitle[1], 1, y, 1, 1, Color.WHITE, colorType, sizeCodeText, false);
                    }
                    for (int i = 0; i < 10; i++) {
                        if ("0".equals(tendency1.oddEven[index].number[titleCode[i]])) {
                            rowInfo.addUnitInfo(String.valueOf(titleCode[i]), i + 2, y, 1, 1,
                                    Color.WHITE, Color.WHITE, sizeCodeText, true);
                        } else {
                            rowInfo.addUnitInfo(String.valueOf(tendency1.oddEven[index].number[titleCode[i]]), i + 2, y, 1, 1,
                                    Color.WHITE, colorMiss,sizeCodeText, false);
                        }
                    }
                    break;
                }

                case _大小:{
                    Tendency1.BigSmallItem bigSmallItem = null;
                    switch (index){
                        case 0:
                            bigSmallItem = tendency1.bigSmall._0;
                            break;
                        case 1:
                            bigSmallItem = tendency1.bigSmall._1;
                            break;
                        case 2:
                            bigSmallItem = tendency1.bigSmall._2;
                            break;
                    }
                    if (bigSmallItem.big == 0) {
                        rowInfo.addUnitInfo(subTitle[0], 0, y, 1, 1, Color.WHITE, colorType, sizeCodeText, false);
                        rowInfo.addUnitInfo(String.valueOf(bigSmallItem.small), 1, y, 1, 1,
                                Color.WHITE, colorMiss, sizeCodeText, false);
                    } else {
                        rowInfo.addUnitInfo(String.valueOf(bigSmallItem.big), 0, y, 1, 1,
                                Color.WHITE, colorMiss, sizeCodeText, false);
                        rowInfo.addUnitInfo(subTitle[1], 1, y, 1, 1, Color.WHITE, colorType, sizeCodeText, false);
                    }
                    for (int i = 0; i < 10; i++) {
                        if ("0".equals(bigSmallItem.number[i])) {
                            rowInfo.addUnitInfo(String.valueOf(titleCode[i]), i + 2, y, 1, 1,
                                    Color.WHITE, Color.WHITE, sizeCodeText, true);
                        } else {
                            rowInfo.addUnitInfo(String.valueOf(bigSmallItem.number[i]), i + 2, y, 1, 1,
                                    Color.WHITE, colorMiss,sizeCodeText, false);
                        }
                    }
                    break;
                }

                case _质合:{
                    Tendency1.PrimCompositeItem primCompositeItem = null;
                    switch (index){
                        case 0:
                            primCompositeItem = tendency1.primComposite._0;
                            break;
                        case 1:
                            primCompositeItem = tendency1.primComposite._1;
                            break;
                        case 2:
                            primCompositeItem = tendency1.primComposite._2;
                            break;
                    }
                    if (primCompositeItem.prim == 0) {
                        rowInfo.addUnitInfo(subTitle[0], 0, y, 1, 1, Color.WHITE, colorType, sizeCodeText, false);
                        rowInfo.addUnitInfo(String.valueOf(primCompositeItem.composite), 1, y, 1, 1,
                                Color.WHITE, colorMiss, sizeCodeText, false);
                    } else {
                        rowInfo.addUnitInfo(String.valueOf(primCompositeItem.prim), 0, y, 1, 1,
                                Color.WHITE, colorMiss, sizeCodeText, false);
                        rowInfo.addUnitInfo(subTitle[1], 1, y, 1, 1, Color.WHITE, colorType, sizeCodeText, false);
                    }
                    for (int i = 0; i < 10; i++) {
                        if ("0".equals(primCompositeItem.number[titleCode[i]])) {
                            rowInfo.addUnitInfo(String.valueOf(titleCode[i]), i + 2, y, 1, 1,
                                    Color.WHITE, Color.WHITE, sizeCodeText, true);
                        } else {
                            rowInfo.addUnitInfo(String.valueOf(primCompositeItem.number[titleCode[i]]), i + 2, y, 1, 1,
                                    Color.WHITE, colorMiss,sizeCodeText, false);
                        }
                    }
                    break;
                }
            }

            y++;
        }

        rowInfos.add(rowInfo);
    }

    /** 大小比、 奇偶比、质合*/
    protected void ratio(AnalyzerType type) {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        switch (type.getId()) {
            case _大小:
                rowInfo.addUnitInfo("大小比", 0, y, 2, 3, colorBg, colorTitle, sizeTitleText, false);
                break;
            case _质合:
                rowInfo.addUnitInfo("质合比", 0, y, 2, 3, colorBg, colorTitle, sizeTitleText, false);
                break;
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.supportHighlight = true;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            switch (type.getId()) {
                case _大小:
                    rowInfo.addUnitInfo(tendency1.bigSmall.specificOfBigSamll, 0, y++, 2, 1,
                            Color.WHITE, Color.BLACK, sizeTitleText, false);
                    break;
                case _质合:
                    rowInfo.addUnitInfo(tendency1.primComposite.specificOfPrimComposite, 0, y++, 2, 1,
                            Color.WHITE, Color.BLACK, sizeTitleText, false);
                    break;
            }
        }
        rowInfos.add(rowInfo);
    }

    /** 和值(多列) */
    private void sumList() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = (int) (unitWidth * 1.5);
        rowInfo.unitH = unitHeight;
        for (int i = 0; i < 28; i++) {
            rowInfo.addUnitInfo(String.valueOf(i), i, 0, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = (int) (unitWidth * 1.5);
        rowInfo.unitH = unitHeight;
        rowInfo.supportHighlight = true;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            for (int i = 0; i < 28; i++) {
                if ("0".equals(tendency1.sumValue.number[i])) {
                    rowInfo.addUnitInfo(String.valueOf(i), i, y, 1, 1,
                            Color.WHITE, Color.RED, sizeCodeText, false);
                } else {
                    rowInfo.addUnitInfo(tendency1.sumValue.number[i], i, y, 1, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                }
            }
            y++;
        }

        rowInfos.add(rowInfo);
    }

    /** 和值（大小、奇偶、质合、除3、振幅） */
    private void sumList1() {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = (int) (unitWidth * 1.5);
        rowInfo.unitH = unitHeight;
        String[] name = new String[]{"大小", "奇偶", "质合", "除3", "振幅"};
        for (int i = 0; i < name.length; i++) {
            rowInfo.addUnitInfo(name[i], i, 0, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = (int) (unitWidth * 1.5);
        rowInfo.unitH = unitHeight;
        rowInfo.supportHighlight = true;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            rowInfo.addUnitInfo(tendency1.sumValue.bigSamll, 0, y, 1, 1,
                    Color.WHITE, Color.BLACK, sizeCodeText, false);
            rowInfo.addUnitInfo(tendency1.sumValue.oddEven, 1, y, 1, 1,
                    Color.WHITE, Color.BLACK, sizeCodeText, false);
            rowInfo.addUnitInfo(tendency1.sumValue.primComposite, 2, y, 1, 1,
                    Color.WHITE, Color.BLACK, sizeCodeText, false);
            rowInfo.addUnitInfo(String.valueOf(tendency1.sumValue.divide3), 3, y, 1, 1,
                    Color.WHITE, Color.BLACK, sizeCodeText, false);
            rowInfo.addUnitInfo(String.valueOf(tendency1.sumValue.area), 4, y, 1, 1,
                    Color.WHITE, Color.BLACK, sizeCodeText, false);
            y++;
        }

        rowInfos.add(rowInfo);
    }

    /** 跨度 */
    protected void span1(){
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.supportHighlight = true;
        int y = 0;
        for (int i = 0; i < 10; i++) {
            rowInfo.addUnitInfo(String.valueOf(i), i, y, 1, 1, colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.supportHighlight = true;
        y = 0;

        int size = trendJson.size();
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            Tendency1 tendency1 = (Tendency1) trendJson.get(itemIndex);
            for (int i = 0; i < 10; i++) {
                if ("0".equals(tendency1.differenceValue.codeMiss[i])) {
                    rowInfo.addUnitInfo(String.valueOf(i), i, y, 1, 1, Color.WHITE, Color.RED, sizeCodeText, false);
                } else {
                    rowInfo.addUnitInfo(tendency1.differenceValue.codeMiss[i], i, y, 1, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                }
            }
            y++;
        }

        rowInfos.add(rowInfo);
    }
}
