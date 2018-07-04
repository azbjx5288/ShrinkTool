package com.shrinktool.rule;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.shrinktool.app.CacheDBHelper;
import com.shrinktool.app.DBConfig;
import com.shrinktool.base.net.GsonHelper;

/**
 * ResultRecord的数据库操作相关
 * Created by Alashi on 2016/6/3.
 */
public class ResultRecordHelper {
    public static final String TABLE_NAME = "result_record";

    public static final String KEY_MAIN_ID = "_id";

    public static final String KEY_JSON = "json";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + KEY_MAIN_ID + " integer primary key autoincrement, "
            + KEY_JSON + " text, "
            + CacheDBHelper.KEY_LOGIN_ACCOUNT + " text, "
            + CacheDBHelper.KEY_TABLE_VERSION + " integer);";

    public static Uri save(Context context, ResultRecord resultRecord) {
        ContentValues values = new ContentValues();
        values.put(KEY_JSON, GsonHelper.toJson(resultRecord));
        return context.getContentResolver().insert(DBConfig.URI_RESULT_RECORD, values);
    }
}
