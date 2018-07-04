package com.shrinktool.rule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.shrinktool.app.CacheDBHelper;
import com.shrinktool.app.DBConfig;
import com.shrinktool.base.net.GsonHelper;

import java.util.ArrayList;

/**
 * 用于操作“规则方案”数据库表的类
 * Created by Alashi on 2016/6/2.
 */
public class RuleRecordHelper {
    public static final String TABLE_NAME = "rule_record";

    public static final String KEY_MAIN_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_LOTTERY_ID = "lottery_id";
    public static final String KEY_METHOD_ID = "method_id";
    public static final String KEY_JSON = "json";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + KEY_MAIN_ID + " integer primary key autoincrement, "
            + KEY_NAME + " text, "
            + KEY_LOTTERY_ID + " bigint, "
            + KEY_METHOD_ID + " bigint, "
            + KEY_JSON + " text, "
            + CacheDBHelper.KEY_LOGIN_ACCOUNT + " text, "
            + CacheDBHelper.KEY_TABLE_VERSION + " integer);";

    public static void save(Context context, RuleRecord ruleRecord) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, ruleRecord.getName());
        values.put(KEY_LOTTERY_ID, ruleRecord.getLotteryId());
        values.put(KEY_METHOD_ID, ruleRecord.getMethodId());
        values.put(KEY_JSON, GsonHelper.toJson(ruleRecord));
        context.getContentResolver().insert(DBConfig.URI_RULE_RECORD, values);
    }

    private static final String[] PROJECTION = {
            KEY_MAIN_ID,//0
            KEY_NAME,//1
            KEY_LOTTERY_ID,//2
            KEY_METHOD_ID,//3
            KEY_JSON//4
    };
    public static ArrayList<RuleRecord> getRuleRecords(Context context, int lotteryId,
                                                       int methodId) {
        ArrayList<RuleRecord> list = new ArrayList<>();
        String selection = KEY_LOTTERY_ID + "=='" + lotteryId + "' AND " + KEY_METHOD_ID
                + "=='" + methodId + "'";
        Cursor cursor = context.getContentResolver().query(DBConfig.URI_RULE_RECORD, PROJECTION,
                selection, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                RuleRecord ruleRecord = GsonHelper.fromJson(cursor.getString(4), RuleRecord.class);
                ruleRecord.setId(cursor.getInt(0));
                list.add(ruleRecord);
            }
            cursor.close();
        }
        return list;
    }

    public static void delete(Context context, RuleRecord ruleRecord) {
        context.getContentResolver().delete(DBConfig.URI_RULE_RECORD, KEY_MAIN_ID
                + "=" + ruleRecord.getId(), null);
    }
}
