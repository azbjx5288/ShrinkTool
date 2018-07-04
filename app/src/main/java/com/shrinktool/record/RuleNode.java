package com.shrinktool.record;

import java.util.LinkedHashMap;

/**
 * 数据节点
 * Created by Alashi on 2017/1/18.
 */

public class RuleNode {
    public static final int HIDE = 0;//不显示
    public static final int MATCH = 1;//匹配
    public static final int MISMATCH = 2;//不匹配
    public static final int REVISE = 3;//校正

    public String name;
    public int match = MISMATCH;

    public LinkedHashMap<String, Integer> item;
}
