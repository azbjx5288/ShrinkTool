package com.shrinktool.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库
 * Created by Alashi on 2016/6/2.
 */
public class CacheDBHelper extends SQLiteOpenHelper {

    private static CacheDBHelper sInstance;

    public static final String KEY_LOGIN_ACCOUNT = "login_account";
    public static final String KEY_TABLE_VERSION = "table_version";

    public static final int DATABASE_VERSION = 1;

    public CacheDBHelper(Context context) {
        super(context, "goldenasia.db", null, DATABASE_VERSION);
    }

    public synchronized static CacheDBHelper getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new CacheDBHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (DataProvider.ProviderItem item : DBConfig.getItems()) {
            db.execSQL(item.getTableCreateSql());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
