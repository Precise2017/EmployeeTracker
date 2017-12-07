package com.trackkarlo.employeetracker.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;

/**
 * Created by precise on 09-May-17.
 */

public class Emp_ExampleProvider extends ContentProvider {


    private static final UriMatcher sUriMatcher;
    private static final int USER_INFO = 1;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Emp_UserTableData.AUTHORITY_USER_INFO, "user_info", USER_INFO);
        sUriMatcher.addURI(Emp_UserTableData.AUTHORITY_USER_INFO, "user_info/#", USER_INFO);
    }

    // Map table columns
    private static final HashMap<String, String> sUserInfoColumnProjectionMap;
    static {
        sUserInfoColumnProjectionMap = new HashMap<String, String>();
        sUserInfoColumnProjectionMap.put(Emp_UserTableData.UserTable._ID,
                Emp_UserTableData.UserTable._ID);
        sUserInfoColumnProjectionMap.put(Emp_UserTableData.UserTable.USER_INFO_USER_ID,
                Emp_UserTableData.UserTable.USER_INFO_USER_ID);
        sUserInfoColumnProjectionMap.put(Emp_UserTableData.UserTable.USER_INFO_NAME,
                Emp_UserTableData.UserTable.USER_INFO_NAME);
        sUserInfoColumnProjectionMap.put(Emp_UserTableData.UserTable.USER_INFO_IMEI,
                Emp_UserTableData.UserTable.USER_INFO_IMEI);
        sUserInfoColumnProjectionMap.put(Emp_UserTableData.UserTable.USER_INFO_EMAIL,
                Emp_UserTableData.UserTable.USER_INFO_EMAIL);
        sUserInfoColumnProjectionMap.put(Emp_UserTableData.UserTable.USER_INFO_IMAGE,
                Emp_UserTableData.UserTable.USER_INFO_IMAGE);
        sUserInfoColumnProjectionMap.put(Emp_UserTableData.UserTable.USER_INFO_TYPE,
                Emp_UserTableData.UserTable.USER_INFO_TYPE);
        sUserInfoColumnProjectionMap.put(Emp_UserTableData.UserTable.USER_INFO_LOGGED_IN,
                Emp_UserTableData.UserTable.USER_INFO_LOGGED_IN);
    }

    private static class NotesDBHelper extends SQLiteOpenHelper {
        public NotesDBHelper(Context c) {
            super(c, Emp_UserTableData.DATABASE_NAME, null,
                    Emp_UserTableData.DATABASE_VERSION);
        }

        private static final String SQL_QUERY_CREATE_USER_TABLE = "CREATE TABLE "
                + Emp_UserTableData.UserTable.TABLE_NAME_USER_INFO + " (" + Emp_UserTableData.UserTable._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Emp_UserTableData.UserTable.USER_INFO_USER_ID + " TEXT NOT NULL, "
                + Emp_UserTableData.UserTable.USER_INFO_NAME + " TEXT NOT NULL, "
                + Emp_UserTableData.UserTable.USER_INFO_IMEI + " TEXT NOT NULL, "
                + Emp_UserTableData.UserTable.USER_INFO_EMAIL + " TEXT NOT NULL, "
                + Emp_UserTableData.UserTable.USER_INFO_IMAGE + " TEXT NOT NULL, "
                + Emp_UserTableData.UserTable.USER_INFO_TYPE + " TEXT NOT NULL, "
                + Emp_UserTableData.UserTable.USER_INFO_LOGGED_IN + " TEXT NOT NULL" + ");";

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_QUERY_CREATE_USER_TABLE);
        }

        private static final String SQL_QUERY_DROP = "DROP TABLE IF EXISTS "
                + Emp_UserTableData.UserTable.TABLE_NAME_USER_INFO + ";";

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            db.execSQL(SQL_QUERY_DROP);
            onCreate(db);
        }
    }


    // create a db helper object
    private NotesDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new NotesDBHelper(getContext());
        return false;
    }

    // Get values from Content Provider
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case USER_INFO:
            builder.setTables(Emp_UserTableData.UserTable.TABLE_NAME_USER_INFO);
            builder.setProjectionMap(sUserInfoColumnProjectionMap);
            break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor queryCursor = builder.query(db, projection, selection,
                selectionArgs, null, null, null);
        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return queryCursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case USER_INFO:
            return Emp_UserTableData.CONTENT_TYPE_USER_INFO;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // Insert once row
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(Emp_UserTableData.UserTable.TABLE_NAME_USER_INFO, null, values);
        if (rowId> 0) {
            Uri notesUri = ContentUris.withAppendedId(Emp_UserTableData.CONTENT_URI_USER_INFO,
                    rowId);
            getContext().getContentResolver().notifyChange(notesUri, null);
            return notesUri;
        }
        throw new IllegalArgumentException("<Illegal>Unknown URI: " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case USER_INFO:
            count = db.delete(Emp_UserTableData.UserTable.TABLE_NAME_USER_INFO, where, whereArgs);
            break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                     String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case USER_INFO:
            count = db.update(Emp_UserTableData.UserTable.TABLE_NAME_USER_INFO, values, where,
                    whereArgs);
            break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
