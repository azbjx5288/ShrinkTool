package com.shrinktool.app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

public class DataProvider extends ContentProvider {

    /**
     * 自定义函数
     */
    public static final String FUNCTION_CACHE = "function_cache";

    private static final UriMatcher sUriMatcher;


    private CacheDBHelper cacheDBHelper;

    static {

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        for (int i = 0, size = DBConfig.getItems().size(); i < size; i++) {
            ProviderItem providerItem = DBConfig.getItems().get(i);

            sUriMatcher.addURI(providerItem.uri.getAuthority(), providerItem.uri.getLastPathSegment(), i * 2);
            sUriMatcher.addURI(providerItem.uri.getAuthority(), providerItem.uri.getLastPathSegment() + "/#", i * 2 + 1);
        }
    }

    @Override
    public boolean onCreate() {
        cacheDBHelper = CacheDBHelper.getInstance(getContext());
        return true;
    }

    private void notifyUriChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sUriMatcher.match(uri);
        if (uriType < 0) {
            throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        return DBConfig.getItems().get(uriType / 2).getMimeType();
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //ELog.i(TAG, "URI = " + uri.getPath() + " -- selection = " + selection + " -- login = " + SharedPreferenceManager.getLoginState(getContext()));
        SQLiteDatabase db = cacheDBHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        if (sUriMatcher.match(uri) % 2 != 0) {
            String rowID = uri.getPathSegments().get(1);
            queryBuilder.appendWhere("_id" + "=" + rowID);
        }
        String tableName = getTableName(uri);
        queryBuilder.setTables(tableName);
        selection = appendUser(selection);
        String limit = uri.getQueryParameter("limit");
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
    }

    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = cacheDBHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String tableName = getTableName(uri);
            if (sUriMatcher.match(uri) % 2 != 0) {
                String rowID = uri.getPathSegments().get(1);
                selection = "_id" + "=" + rowID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
            }
            if (selection == null) {
                selection = "1";
            }
            selection = appendUser(selection);
            int deleteCount = db.delete(tableName, selection, selectionArgs);

            db.setTransactionSuccessful();
            if (deleteCount > 0) {
                notifyUriChange(uri);
            }
            return deleteCount;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = cacheDBHelper.getWritableDatabase();
        if (db == null) {
            throw new IllegalStateException("Couldn't open database for " + uri);
        }
        String tableName = getTableName(uri);
        db.beginTransaction();
        int numInserted = 0;
        String userId = getUserId();
        try {
            int len = values.length;
            for (ContentValues value : values) {
                if (value != null) {
                    value.put(CacheDBHelper.KEY_LOGIN_ACCOUNT, userId);
                    value.put(CacheDBHelper.KEY_TABLE_VERSION, CacheDBHelper.DATABASE_VERSION);
                    db.insert(tableName, null, value);
                }
            }
            numInserted = len;
            db.setTransactionSuccessful();
            if (numInserted > 0) {
                notifyUriChange(uri);
            }

            return numInserted;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (FUNCTION_CACHE.equals(method)) {
            deleteTHENbulkInsert((Uri)extras.getParcelable("uri"), extras.getString("selection"), extras.getStringArray("selectionArgs"), (ContentValues[])extras.getParcelableArray("contentValues"));
            return null;
        } else {
            return super.call(method, arg, extras);
        }
    }

    /**
     * 先删除过期缓存再批量插入最新数据
     * @param uri
     * @param selection
     * @param selectionArgs
     * @param values
     */
    public synchronized void deleteTHENbulkInsert(Uri uri, String selection, String[] selectionArgs, ContentValues[] values) {
        SQLiteDatabase db = cacheDBHelper.getWritableDatabase();

        if (null == db) {
            throw new IllegalStateException("Couldn't open db for " + uri);
        }

        String tableName = getTableName(uri);
        int deleteCount = 0;
        String userId = getUserId();
        try {
            db.beginTransaction();

            if (sUriMatcher.match(uri) % 2 != 0) {
                String rowID = uri.getPathSegments().get(1);
                selection = "_id" + "=" + rowID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
            }
            if (selection == null) {
                selection = "1";
            }
            selection = appendUser(selection);
            deleteCount = db.delete(tableName, selection, selectionArgs);

            int insertCount = values.length;
            for (ContentValues value : values) {
                if (value != null) {
                    value.put(CacheDBHelper.KEY_LOGIN_ACCOUNT, userId);
                    value.put(CacheDBHelper.KEY_TABLE_VERSION, CacheDBHelper.DATABASE_VERSION);
                    db.insert(tableName, null, value);
                }
            }

            db.setTransactionSuccessful();
            if ((deleteCount + insertCount) > 0) {
                notifyUriChange(uri);
            }
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {
        //ELog.i(TAG, "insert () uri = " + uri.getPath());
        SQLiteDatabase db = cacheDBHelper.getWritableDatabase();
        try {
            db.beginTransaction();

            String tableName = getTableName(uri);
            String nullColumnHack = null;

            values.put(CacheDBHelper.KEY_LOGIN_ACCOUNT, getUserId());
            values.put(CacheDBHelper.KEY_TABLE_VERSION, CacheDBHelper.DATABASE_VERSION);

            long mainId = db.insert(tableName, nullColumnHack, values);
            db.setTransactionSuccessful();
            if (mainId > -1) {
                Uri insertedId = ContentUris.withAppendedId(getUri(uri), mainId);
                notifyUriChange(insertedId);
                return insertedId;
            } else {
                return null;
            }

        } finally {
            db.endTransaction();
        }
    }


    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //ELog.i(TAG, "update () uri = " + uri.getPath() + ", selection=" + selection);
        SQLiteDatabase db = cacheDBHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String tableName = getTableName(uri);
            if (sUriMatcher.match(uri) % 2 != 0) {
                String rowID = uri.getPathSegments().get(1);
                selection = "_id" + "=" + rowID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
            }
            selection = appendUser(selection);

            values.put(CacheDBHelper.KEY_LOGIN_ACCOUNT, getUserId());
            values.put(CacheDBHelper.KEY_TABLE_VERSION, CacheDBHelper.DATABASE_VERSION);

            int updateCount = db.update(tableName, values, selection, selectionArgs);
            if (updateCount > 0) {
                notifyUriChange(uri);
            }
            db.setTransactionSuccessful();
            return updateCount;
        } finally {
            db.endTransaction();
        }
    }

    private String appendUser(String selection) {
        StringBuilder where = new StringBuilder();
        //where.append('(');
        where.append(CacheDBHelper.KEY_LOGIN_ACCOUNT);
        where.append("='");
        where.append(getUserId());
        where.append("'");

        if (selection != null && selection.length() > 0) {
            where.append(" AND ");
            where.append('(');
            where.append(selection);
            where.append(')');
        }

        //ELog.d(TAG, "appendUser " + where);
        return where.toString();
    }

    private String getTableName(Uri uri) {
        int uriType = sUriMatcher.match(uri);
        if (uriType < 0) {
            throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        return DBConfig.getItems().get(uriType / 2).tableName;
    }

    private Uri getUri(Uri uri) {
        int uriType = sUriMatcher.match(uri);
        if (uriType < 0) {
            throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        return DBConfig.getItems().get(uriType / 2).uri;
    }

    private String getUserId() {
        return GoldenAsiaApp.getUserCentre().getUserID();
    }

    public static class ProviderItem {
        String tableName;
        Uri uri;
        String tableCreateSql;

        public ProviderItem(Uri uri, String tableName, String tableCreateSql) {
            this.tableName = tableName;
            this.uri = uri;
            this.tableCreateSql = tableCreateSql;
        }

        public String getTableCreateSql() {
            return tableCreateSql;
        }

        public String getMimeType() {
            return "vnd.android.cursor.dir/vnd.golden.aia." + uri.getLastPathSegment();
        }
    }
}
