package com.shrinktool.app;

import android.net.Uri;

import com.shrinktool.rule.ResultRecordHelper;
import com.shrinktool.rule.RuleRecordHelper;

import java.util.ArrayList;

/**
 * 数据库相关配置
 * Created by Alashi on 2016/6/2.
 */
public class DBConfig {
    private static final String URI_HEAD = "content://com.shrinktool.dataprovider/";

    public static final Uri URI_RULE_RECORD = Uri.parse(URI_HEAD + "ruleRecord");
    public static final Uri URI_RESULT_RECORD = Uri.parse(URI_HEAD + "resultRecord");


    private static final ArrayList<DataProvider.ProviderItem> sItems = new ArrayList<>();

    static {
        addItem(URI_RULE_RECORD, RuleRecordHelper.TABLE_NAME, RuleRecordHelper.SQL_CREATE_TABLE);
        addItem(URI_RESULT_RECORD, ResultRecordHelper.TABLE_NAME, ResultRecordHelper.SQL_CREATE_TABLE);
    }

    private static void addItem(Uri uri, String tableName, String tableCreateSql){
        sItems.add(new DataProvider.ProviderItem(uri, tableName, tableCreateSql));
    }

    public static ArrayList<DataProvider.ProviderItem> getItems() {
        return sItems;
    }
}
