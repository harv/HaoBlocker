package com.haoutil.xposed.haoblocker.util;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.haoutil.xposed.haoblocker.BuildConfig;

public class BlockerProvider extends ContentProvider {
    private static final String TABLE_RULE = "rule";
    private static final String TABLE_SMS = "sms";
    private static final String TABLE_CALL = "call";

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.BlockProvider";
    public static final String PERMISSION = BuildConfig.APPLICATION_ID + ".permission.ACCESS_BLOCKER_DATA";

    private static final int RULE_ALL = 0;
    private static final int RULE_ITEM = 1;
    private static final int SMS_ALL = 2;
    private static final int SMS_ITEM = 3;
    private static final int CALL_ALL = 4;
    private static final int CALL_ITEM = 5;

    private static final String RULE_TYPE = "vnd.android.cursor.dir/vnd.hblocker.rule";
    private static final String RULE_ITEM_TYPE = "vnd.android.cursor.item/vnd.hblocker.rule";
    private static final String SMS_TYPE = "vnd.android.cursor.dir/vnd.hblocker.sms";
    private static final String SMS_ITEM_TYPE = "vnd.android.cursor.item/vnd.hblocker.sms";
    private static final String CALL_TYPE = "vnd.android.cursor.dir/vnd.hblocker.call";
    private static final String CALL_ITEM_TYPE = "vnd.android.cursor.item/vnd.hblocker.call";

    private static final UriMatcher sMatcher;

    private ContentResolver mResolver;
    private DbHelper mDbHelper;

    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sMatcher.addURI(AUTHORITY, "rule", RULE_ALL);
        sMatcher.addURI(AUTHORITY, "rule/#", RULE_ITEM);
        sMatcher.addURI(AUTHORITY, "sms", SMS_ALL);
        sMatcher.addURI(AUTHORITY, "sms/#", SMS_ITEM);
        sMatcher.addURI(AUTHORITY, "call", CALL_ALL);
        sMatcher.addURI(AUTHORITY, "call/#", CALL_ITEM);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context != null) {
            mResolver = context.getContentResolver();
        }
        mDbHelper = new DbHelper(context);

        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sMatcher.match(uri)) {
            case RULE_ALL:
                return RULE_TYPE;
            case RULE_ITEM:
                return RULE_ITEM_TYPE;
            case SMS_ALL:
                return SMS_TYPE;
            case SMS_ITEM:
                return SMS_ITEM_TYPE;
            case CALL_ALL:
                return CALL_TYPE;
            case CALL_ITEM:
                return CALL_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Error URI: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sMatcher.match(uri)) {
            case RULE_ALL:
            case RULE_ITEM:
                return db.query(TABLE_RULE, projection, selection, selectionArgs, null, null, sortOrder);
            case SMS_ALL:
            case SMS_ITEM:
                return db.query(TABLE_SMS, projection, selection, selectionArgs, null, null, sortOrder);
            case CALL_ALL:
            case CALL_ITEM:
                return db.query(TABLE_CALL, projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Error URI: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long rowId;
        switch (sMatcher.match(uri)) {
            case RULE_ALL:
                rowId = db.insert(TABLE_RULE, null, values);
                break;
            case SMS_ALL:
                rowId = db.insert(TABLE_SMS, null, values);
                break;
            case CALL_ALL:
                rowId = db.insert(TABLE_CALL, null, values);
                break;
            default:
                throw new IllegalArgumentException("Error URI: " + uri);
        }
        if (rowId < 0) {
            throw new SQLiteException("Unable to insert " + values + " for " + uri);
        }

        Uri newUri = ContentUris.withAppendedId(uri, rowId);
        mResolver.notifyChange(newUri, null);

        return newUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int count;
        switch (sMatcher.match(uri)) {
            case RULE_ITEM:
                count = db.update(TABLE_RULE, values, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case SMS_ITEM:
                count = db.update(TABLE_SMS, values, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case CALL_ITEM:
                count = db.update(TABLE_CALL, values, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case RULE_ALL:
                count = db.update(TABLE_RULE, values, null, null);
                break;
            case SMS_ALL:
                count = db.update(TABLE_SMS, values, null, null);
                break;
            case CALL_ALL:
                count = db.update(TABLE_CALL, values, null, null);
                break;
            default:
                throw new IllegalArgumentException("Error URI: " + uri);
        }

        mResolver.notifyChange(uri, null);

        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int count;
        switch (sMatcher.match(uri)) {
            case RULE_ITEM:
                count = db.delete(TABLE_RULE, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case SMS_ITEM:
                count = db.delete(TABLE_SMS, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case CALL_ITEM:
                count = db.delete(TABLE_CALL, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new IllegalArgumentException("Error URI: " + uri);
        }

        mResolver.notifyChange(uri, null);

        return count;
    }

    class DbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "blocker.db";
        private static final int DATABASE_VERSION = 3;

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RULE + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT, type INTEGER, sms INTEGER, call INTEGER, exception INTEGER, created INTEGER, remark TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SMS + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, sender TEXT, content TEXT, created INTEGER, read INTEGER)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CALL + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, caller TEXT, created INTEGER, read INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion) {
                case 1:
                    db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RULE + "_temp(_id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT, type INTEGER, sms INTEGER, call INTEGER, exception INTEGER, created INTEGER)");
                    db.execSQL("INSERT INTO " + TABLE_RULE + "_temp(_id,content,type,sms,call,exception,created) SELECT _id,content,type,sms,call,0,created FROM " + TABLE_RULE);
                    db.execSQL("DROP TABLE " + TABLE_RULE);
                    db.execSQL("ALTER TABLE " + TABLE_RULE + "_temp RENAME TO " + TABLE_RULE);
                case 2:
                    db.execSQL("ALTER TABLE " + TABLE_RULE + " ADD COLUMN remark TEXT");
            }
        }
    }
}
