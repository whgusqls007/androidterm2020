package com.example.androidterm2020;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.Nullable;

public class ScheduleProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.androidterm2020";
    private static final String BASE_PATH = "test4"; // db이름.
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    private static final int PERSONS = 1;
    private static final int PERSON_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, PERSONS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", PERSON_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBHelper helper = new DBHelper(getContext());
        database = helper.getWritableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case PERSONS:
                cursor =  database.query(DBHelper.TABLE_NAME, DBHelper.ALL_COLUMNS,
                        s,null,null,null,DBHelper.SCHEDULE_START_DATE +" ASC");
                //cursor =  database.rawQuery("select * from " + DBHelper.TABLE_NAME + " order by " + DBHelper.SCHEDULE_START_DATE + " ASC;", null);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PERSONS:
                return "vnd.android.cursor.dir/persons";
            default:
                throw new IllegalArgumentException("알 수 없는 URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = database.insert(DBHelper.TABLE_NAME, null, contentValues);

        if (id > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("추가 실패 -> URI :" + uri);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case PERSONS:
                count =  database.delete(DBHelper.TABLE_NAME, s, strings);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case PERSONS:
                count =  database.update(DBHelper.TABLE_NAME, contentValues, s, strings);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
}
