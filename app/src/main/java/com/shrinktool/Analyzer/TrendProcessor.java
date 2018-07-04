package com.shrinktool.Analyzer;

import android.graphics.Color;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 将服务器数据解析成表格数据
 * Created by Alashi on 2016/8/8.
 */
public abstract class TrendProcessor {

    protected int colorBg = Color.parseColor("#F6F6F6");//灰色背景
    protected int colorTitle = Color.parseColor("#1ABC9C");//绿色字体
    protected int colorMiss = Color.parseColor("#D4D4D4");//浅灰色字体
    protected int colorType = Color.parseColor("#0885DC");//蓝色

    protected int sizeTitleText = 40;
    protected int sizeCodeText = 36;
    protected int unitWidth = 80;
    protected int unitHeight = 80;

    protected List trendJson;
    protected int lotteryId;
    protected int methodId;
    protected ArrayList<AnalyzerType> types = new ArrayList<>();

    protected ArrayList<CodeFormView.RowInfo> rowInfos;
    protected ArrayList<CodeFormView.RowInfo> titleInfos;

    public TrendProcessor(int lotteryId, int methodId) {
        this.lotteryId = lotteryId;
        this.methodId = methodId;
    }

    protected ArrayList<AnalyzerType> addAnalyzerType(int id, String display) {
        types.add(new AnalyzerType(id, display));
        return types;
    }

    public void setJson(List json) {
        this.trendJson = json;
    }

    public int getLotteryId() {
        return lotteryId;
    }

    public int getMethodId() {
        return methodId;
    }

    public ArrayList<AnalyzerType> getSupportType(){
        return types;
    }

    public ArrayList<CodeFormView.RowInfo> getRowInfo(){
        return rowInfos;
    }
    public ArrayList<CodeFormView.RowInfo> getTitleInfo(){
        return titleInfos;
    }

    public abstract void process(AnalyzerType type);
    public abstract TypeToken getTypeToken();

    /** 单行标题的大组 */
    protected void commonOneLineRow(String title, int titleH, int uW, String[] code) {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo(title, 0, y, uW, titleH, colorBg, colorTitle, sizeTitleText, false);

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        for (String codeItem : code) {
            rowInfo.addUnitInfo(codeItem, 0, y++, uW, 1,
                    Color.WHITE, Color.BLACK, sizeCodeText, false);
        }

        rowInfos.add(rowInfo);
    }

    /** 2行标题的大组 */
    protected void commonTwoLineRow(String title, String[] subTitle, int uW, String[][] codeMiss) {
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        rowInfo.addUnitInfo(title, 0, y++, subTitle.length * uW, 1, colorBg, colorTitle, sizeTitleText, false);
        for (int i = 0; i < subTitle.length; i++) {
            rowInfo.addUnitInfo(subTitle[i], i, y, uW, 1,
                    colorBg, colorTitle, sizeTitleText, false);
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;
        int size = codeMiss.length;
        for (int itemIndex = size - 1; itemIndex >= 0; itemIndex--) {
            int i = 0;
            for (String code : codeMiss[itemIndex]) {
                if ("0".equals(code)) {
                    rowInfo.addUnitInfo(subTitle[i], i, y, uW, 1,
                            Color.WHITE, Color.RED, sizeCodeText, true);
                } else {
                    rowInfo.addUnitInfo(code, i, y, uW, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                }

                i += uW;
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }

    /** 两行标题的，LinkedHashMap对应的表 */
    protected void commonTwoLineIteratorTable(String title, int uW, LinkedHashMap<String, String>[] linkedHashMaps){
        Set<String> subTitleSet = linkedHashMaps[0].keySet();
        String[] subTitles = new String[subTitleSet.size()];
        int i = 0;
        for (String subTitle : subTitleSet) {
            subTitles[i++] = subTitle;
        }
        commonTwoLineIteratorTable(title, uW, subTitles, linkedHashMaps);
    }

    /** 两行标题的，LinkedHashMap对应的表 */
    protected void commonTwoLineIteratorTable(String title, int uW, String[] subTitles,
                                              LinkedHashMap<String, String>[] linkedHashMaps){
        int y = 0;
        CodeFormView.RowInfo rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;

        rowInfo.addUnitInfo(title, 0, y++, subTitles.length * uW, 1,
                colorBg, colorTitle, sizeTitleText, false);
        int i = 0;
        for (String subTitle : subTitles) {
            rowInfo.addUnitInfo(subTitle, i * uW, y, uW, 1,
                    colorBg, colorTitle, sizeTitleText, false);
            i++;
        }

        titleInfos.add(rowInfo);
        rowInfo = new CodeFormView.RowInfo();
        rowInfo.unitW = unitWidth;
        rowInfo.unitH = unitHeight;
        y = 0;

        for (LinkedHashMap<String, String> linkedHashMap : linkedHashMaps) {
            Iterator<Map.Entry<String, String>> iterator = linkedHashMap.entrySet().iterator();
            i = 0;
            while (iterator.hasNext()) {
                String code = iterator.next().getValue();
                if ("0".equals(code)) {
                    rowInfo.addUnitInfo(subTitles[i], i * uW, y, uW, 1,
                            Color.WHITE, Color.RED, sizeCodeText, true);
                } else {
                    rowInfo.addUnitInfo(code, i * uW, y, uW, 1,
                            Color.WHITE, colorMiss, sizeCodeText, false);
                }
                i++;
            }
            y++;
        }
        rowInfos.add(rowInfo);
    }
}
