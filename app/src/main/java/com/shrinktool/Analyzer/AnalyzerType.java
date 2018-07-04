package com.shrinktool.Analyzer;

/**
 * 走势图类型
 * Created by Alashi on 2016/7/25.
 */
public class AnalyzerType {
    private final int id;
    private final String display;

    public AnalyzerType(int id, String display) {
        this.id = id;
        this.display = display;
    }

    public int getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }
}
